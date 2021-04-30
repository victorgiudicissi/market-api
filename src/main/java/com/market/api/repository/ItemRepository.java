package com.market.api.repository;

import com.market.api.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends MongoRepository<Item, Long> {
    Item save(Item item);
    Item findByUuid(String uuid);
    void deleteByUuid(String uuid);
}
