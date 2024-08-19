package com.market.api.service.impl;

import com.market.api.dto.cart.CartResponseDto;
import com.market.api.dto.event.Event;
import com.market.api.dto.event.EventAction;
import com.market.api.dto.event.EventType;
import com.market.api.dto.item.FilterCartRequestDto;
import com.market.api.entity.Cart;
import com.market.api.exception.DataNotFoundException;
import com.market.api.exception.OutOfStockException;
import com.market.api.exception.UnavailableProductException;
import com.market.api.entity.Item;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final ItemService itemService;
    private final CartRepository cartRepository;
    private final MongoTemplate mongoTemplate;
    private final KafkaServiceImpl kafkaService;

    @Override
    public Cart createCart(Cart cartRequestDto) {
        Cart cart = new Cart();

        AtomicReference<Long> cartPrice = new AtomicReference<>(0L);

        Set<Item> cartItems = new HashSet<>();
        Set<Item> updatedItems = new HashSet<>();

        cartRequestDto.getItems()
        .forEach(itemFromRequest -> {
            Item itemFound = itemService.findItemByUuid(itemFromRequest.getUuid());

            if (itemFound.getQuantity().compareTo(itemFromRequest.getQuantity()) < 0) {
                throw new OutOfStockException(String.format("The quantity informed for item %s is greater than the stock %s.", itemFromRequest.getUuid(), itemFound.getQuantity()));
            }

            if (!itemFound.isEnabled()) {
                throw new UnavailableProductException(String.format("The product %s is disabled.", itemFound.getDescription()));
            }

            Item cartItem = itemFound.toBuilder().quantity(itemFromRequest.getQuantity()).build();

            Item updatedItem = itemFound.toBuilder().quantity(itemFound.getQuantity() - itemFromRequest.getQuantity()).build();

            cartPrice.set(itemFound.getPrice() * itemFromRequest.getQuantity());

            updatedItems.add(updatedItem);
            cartItems.add(cartItem);
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

        kafkaService.sendEvent(Event
                        .builder()
                        .content(cart)
                        .action(com.market.api.dto.event.EventAction.SAVE)
                        .type(EventType.CART)
                        .createdAt(LocalDateTime.now())
                        .build(),
                EventType.CART);

        return cart;
    }

    public Cart getByUuid(String uuid) {
        return this.findById(uuid);
    }

    @Override
    public List<Cart> findCartByFilter(FilterCartRequestDto filter) {
        log.info("Filtering carts. filter: {}", filter);
        PageRequest pageRequest = PageRequest.of(filter.getPage() - 1, filter.getSize());

        Query query = new Query();

        if (filter.getMarketUuid() != null) {
            query.addCriteria(Criteria.where("marketUuid").regex(filter.getMarketUuid(), "i"));
        } else if (filter.getStatus() != null) {
            query.addCriteria(Criteria.where("status").regex(filter.getStatus().toString(), "i"));
        }

        query.with(pageRequest);

        return mongoTemplate.find(query, Cart.class);
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
    public Cart updateStatus(String uuid, Status status) {
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

        return cartRepository.save(cart);
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
