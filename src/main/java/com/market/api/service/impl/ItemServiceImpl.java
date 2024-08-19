package com.market.api.service.impl;

import com.market.api.dto.event.Event;
import com.market.api.dto.event.EventAction;
import com.market.api.dto.event.EventType;
import com.market.api.dto.item.FilterItemRequestDto;
import com.market.api.exception.DataNotFoundException;
import com.market.api.entity.Item;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final MongoTemplate mongoTemplate;
    private final KafkaServiceImpl kafkaService;

    @Override
    public Item save(Item data) {
        Item result = itemRepository.save(data);

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
    public Item updateItem(String itemUuid, Item data) {
        this.findItem(itemUuid);

        data.setUuid(itemUuid);

        Item result = itemRepository.save(data);

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
    public Item changeItemStatus(String itemUuid, boolean status) {
        Item item = this.findItem(itemUuid);

        item.setEnabled(status);

        Item result = itemRepository.save(item);

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
    public Item findItemByUuid(String itemUuid) {
        return this.findItem(itemUuid);
    }

    @Override
    public List<Item> findItemsByFilter(FilterItemRequestDto filter) {
        PageRequest pageRequest = PageRequest.of(filter.getPage() - 1, filter.getSize());

        Query query = new Query();

        if (filter.getEnabled() != null) {
            query.addCriteria(Criteria.where("enabled").is(filter.getEnabled()));
        } else if (filter.getDescription() != null) {
            query.addCriteria(Criteria.where("description").regex(filter.getDescription(), "i"));
        } else if (filter.getMarketUuid() != null) {
            query.addCriteria(Criteria.where("marketUuid").regex(filter.getMarketUuid(), "i"));
        }

        query.with(pageRequest);

        return mongoTemplate.find(query, Item.class);
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
