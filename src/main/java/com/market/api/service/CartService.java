package com.market.api.service;

import com.market.api.dto.item.FilterCartRequestDto;
import com.market.api.entity.Cart;
import com.market.api.model.enums.Status;

import java.util.List;

public interface CartService {
    Cart createCart(Cart cartRequestDto);
    Cart getByUuid(String uuid);
    List<Cart> findCartByFilter(FilterCartRequestDto filter);
    void deleteByUuid(String uuid);
    Cart updateStatus(String uuid, Status status);
}
