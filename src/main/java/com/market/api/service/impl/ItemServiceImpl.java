package com.market.api.service.impl;

import com.market.api.dto.Page;
import com.market.api.dto.event.Event;
import com.market.api.dto.event.EventAction;
import com.market.api.dto.event.EventType;
import com.market.api.dto.item.FilterItemRequestDto;
import com.market.api.dto.item.ItemRequestDto;
import com.market.api.dto.item.ItemResponseDto;
import com.market.api.exception.DataNotFoundException;
import com.market.api.model.Item;
import com.market.api.repository.ItemRepository;
import com.market.api.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final MongoTemplate mongoTemplate;
    private final KafkaServiceImpl kafkaService;

    @Override
    public ItemResponseDto save(ItemRequestDto itemRequestDto) {
        ItemResponseDto result = itemRepository.save(itemRequestDto.toItem())
                .toItemResponseDto();

        kafkaService.sendEvent(Event
                        .builder()
                        .content(result)
                        .action(com.market.api.dto.event.EventAction.SAVE)
                        .type(EventType.ITEM)
                        .createdAt(LocalDateTime.now())
                        .build(),
                EventType.ITEM);

        return result;
    }

    @Override
    public ItemResponseDto updateItem(String itemUuid, ItemRequestDto itemRequestDto) {
        Item item = this.findItem(itemUuid);

        ItemResponseDto result = itemRepository.save(itemRequestDto.toItem(itemUuid, item.getCreatedAt()))
                .toItemResponseDto();

        kafkaService.sendEvent(Event
                .builder()
                .content(result)
                .action(EventAction.UPDATE)
                .type(EventType.ITEM)
                .createdAt(LocalDateTime.now())
                .build(), EventType.ITEM);

        return result;
    }

    @Override
    public List<Item> saveAll(Set<Item> items) {
        return itemRepository.saveAll(items);
    }

    @Override
    public ItemResponseDto changeItemStatus(String itemUuid, boolean status) {
        Item item = this.findItem(itemUuid);

        item.setEnabled(status);

        ItemResponseDto result = itemRepository.save(item)
                .toItemResponseDto();

        kafkaService.sendEvent(Event
                .builder()
                .content(result)
                .action(EventAction.UPDATE_STATUS)
                .type(EventType.ITEM)
                .createdAt(LocalDateTime.now())
                .build(), EventType.ITEM);

        return result;
    }

    @Override
    public void delete(String itemUuid) {
        Item item = this.findItem(itemUuid);

        log.info("Item encontrado com sucesso, iniciando a deleção. item: {}", item);

        kafkaService.sendEvent(Event
                .builder()
                .content(item.toItemResponseDto())
                .action(EventAction.DELETE)
                .type(EventType.ITEM)
                .createdAt(LocalDateTime.now())
                .build(), EventType.ITEM);

        itemRepository.deleteByUuid(item.getUuid());
    }

    @Override
    public ItemResponseDto findItemByUuid(String itemUuid) {
        Item item = this.findItem(itemUuid);

        log.info("Item encontrado com sucesso. item: {}", item);
        return item.toItemResponseDto();
    }

    @Override
    public Page<ItemResponseDto> findItemsByFilter(FilterItemRequestDto filter) {
        log.info("Filtering items. filter: {}", filter);
        PageRequest pageRequest = PageRequest.of(filter.getPage() - 1, filter.getSize());

        Query query = new Query();

        if (filter.getEnabled() != null) {
            query.addCriteria(Criteria.where("enabled").is(filter.getEnabled()));
        } else if (filter.getDescription() != null) {
            query.addCriteria(Criteria.where("description").regex(filter.getDescription(), "i"));
        } else if (filter.getMarketUuid() != null) {
            query.addCriteria(Criteria.where("marketUuid").regex(filter.getMarketUuid(), "i"));
        }

        long totalCount = mongoTemplate.count(query, Item.class);

        query.with(pageRequest);

        List<Item> items = mongoTemplate.find(query, Item.class);

        List<ItemResponseDto> itemsDTO = items.stream()
                .map(Item::toItemResponseDto)
                .collect(Collectors.toList());

        Page<ItemResponseDto> pagination = new Page<>();
        pagination.setPage(filter.getPage());
        pagination.setCount(totalCount);
        pagination.setSize(filter.getSize());
        pagination.setContent(itemsDTO);

        return pagination;
    }

    private Item findItem(String itemUuid) {
        Optional<Item> optItem = itemRepository.findById(itemUuid);

        if (optItem.isEmpty()) {
            String message = "It was not possible find the item with uuid %s.";
            log.warn(String.format(message, itemUuid));
            throw new DataNotFoundException(String.format(message, itemUuid));
        }

        return optItem.get();
    }
}
