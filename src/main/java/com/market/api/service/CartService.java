package com.market.api.service;

import com.market.api.dto.Page;
import com.market.api.dto.cart.CartRequestDto;
import com.market.api.dto.cart.CartResponseDto;
import com.market.api.dto.item.FilterCartRequestDto;
import com.market.api.model.enums.Status;

public interface CartService {
    CartResponseDto createCart(CartRequestDto cartRequestDto);
    CartResponseDto getByUuid(String uuid);
    Page<CartResponseDto> findCartByFilter(FilterCartRequestDto filter);
    void deleteByUuid(String uuid);
    CartResponseDto updateStatus(String uuid, Status status);
}
