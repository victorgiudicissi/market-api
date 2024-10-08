package com.market.api.repository;

import com.market.api.entity.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<Cart, String> {
    Cart findByUuid(String uuid);
}
