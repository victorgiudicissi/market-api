package com.market.api.repository;

import com.market.api.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Item save(Item item);
    Item findByUuid(String uuid);
}
