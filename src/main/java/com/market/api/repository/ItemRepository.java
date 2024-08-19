package com.market.api.repository;

import com.market.api.entity.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends MongoRepository<Item, String> {
    Item save(Item item);
    void deleteByUuid(String uuid);
}
