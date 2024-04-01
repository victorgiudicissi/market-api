package com.market.api.service.impl;

import com.market.api.dto.Page;
import com.market.api.dto.cart.CartRequestDto;
import com.market.api.dto.cart.CartResponseDto;
import com.market.api.dto.event.Event;
import com.market.api.dto.event.EventAction;
import com.market.api.dto.event.EventType;
import com.market.api.dto.item.FilterCartRequestDto;
import com.market.api.dto.item.ItemResponseDto;
import com.market.api.exception.DataNotFoundException;
import com.market.api.exception.OutOfStockException;
import com.market.api.exception.UnavailableProductException;
import com.market.api.model.Cart;
import com.market.api.model.Item;
import com.market.api.model.enums.Status;
import com.market.api.repository.CartRepository;
import com.market.api.service.CartService;
import com.market.api.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final ItemService itemService;
    private final CartRepository cartRepository;
    private final MongoTemplate mongoTemplate;
    private final KafkaServiceImpl kafkaService;

    @Override
    public CartResponseDto createCart(CartRequestDto cartRequestDto) {
        Cart cart = new Cart();

        AtomicReference<Long> cartPrice = new AtomicReference<>(0L);

        Set<Item> cartItems = new HashSet<>();
        Set<Item> updatedItems = new HashSet<>();

        cartRequestDto.getItems()
        .forEach(itemFromRequest -> {
            ItemResponseDto itemFound = itemService.findItemByUuid(itemFromRequest.getUuid());

            log.info(String.format("Item found. item: %s", itemFound));

            if (itemFound.getQuantity().compareTo(itemFromRequest.getQuantity()) < 0) {
                throw new OutOfStockException(String.format("The quantity informed for item %s is greater than the stock %s.", itemFromRequest.getUuid(), itemFound.getQuantity()));
            }

            if (!itemFound.isEnabled()) {
                throw new UnavailableProductException(String.format("The product %s is disabled.", itemFound.getDescription()));
            }

            ItemResponseDto cartItem = itemFound.toBuilder().quantity(itemFromRequest.getQuantity()).build();

            ItemResponseDto updatedItem = itemFound.toBuilder().quantity(itemFound.getQuantity() - itemFromRequest.getQuantity()).build();

            cartPrice.set(itemFound.getPrice() * itemFromRequest.getQuantity());

            updatedItems.add(updatedItem.toItem());
            cartItems.add(cartItem.toItem());
        });

        cart.setUuid(UUID.randomUUID().toString());
        cart.setMarketUuid(cartRequestDto.getMarketUuid());
        cart.setItems(cartItems);
        cart.setPrice(cartPrice.get());
        cart.setStatus(Status.PENDING);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);
        itemService.saveAll(updatedItems);

        CartResponseDto result = cart.toCartResponseDto();

        kafkaService.sendEvent(Event
                        .builder()
                        .content(result)
                        .action(com.market.api.dto.event.EventAction.SAVE)
                        .type(EventType.CART)
                        .createdAt(LocalDateTime.now())
                        .build(),
                EventType.CART);

        return result;
    }

    public CartResponseDto getByUuid(String uuid) {
        return this.findById(uuid).toCartResponseDto();
    }

    @Override
    public Page<CartResponseDto> findCartByFilter(FilterCartRequestDto filter) {
        log.info("Filtering carts. filter: {}", filter);
        PageRequest pageRequest = PageRequest.of(filter.getPage() - 1, filter.getSize());

        Query query = new Query();

        if (filter.getMarketUuid() != null) {
            query.addCriteria(Criteria.where("marketUuid").regex(filter.getMarketUuid(), "i"));
        } else if (filter.getStatus() != null) {
            query.addCriteria(Criteria.where("status").regex(filter.getStatus().toString(), "i"));
        }

        long totalCount = mongoTemplate.count(query, Cart.class);

        query.with(pageRequest);

        List<Cart> cart = mongoTemplate.find(query, Cart.class);

        List<CartResponseDto> itemsDTO = cart.stream()
                .map(Cart::toCartResponseDto)
                .collect(Collectors.toList());

        Page<CartResponseDto> pagination = new Page<>();
        pagination.setPage(filter.getPage());
        pagination.setCount(totalCount);
        pagination.setSize(filter.getSize());
        pagination.setContent(itemsDTO);

        return pagination;
    }

    public void deleteByUuid(String uuid) {
        CartResponseDto result = this.findById(uuid).toCartResponseDto();

        kafkaService.sendEvent(Event
                        .builder()
                        .content(result)
                        .action(EventAction.DELETE)
                        .type(EventType.CART)
                        .createdAt(LocalDateTime.now())
                        .build(),
                EventType.CART);

        cartRepository.deleteById(uuid);
    }

    @Override
    public CartResponseDto updateStatus(String uuid, Status status) {
        Cart cart = findById(uuid);

        cart.setStatus(status);

        kafkaService.sendEvent(Event
                        .builder()
                        .content(cart.toCartResponseDto())
                        .action(EventAction.UPDATE_STATUS)
                        .type(EventType.CART)
                        .createdAt(LocalDateTime.now())
                        .build(),
                EventType.CART);

        return cartRepository.save(cart).toCartResponseDto();
    }

    private Cart findById(String uuid) {
        Optional<Cart> optCart = cartRepository.findById(uuid);

        if (optCart.isEmpty()) {
            String message = "Cart with uuid %s not found.";
            log.error(String.format(message, uuid));
            throw new DataNotFoundException(String.format(message, uuid));
        }

        return optCart.get();
    }
}
