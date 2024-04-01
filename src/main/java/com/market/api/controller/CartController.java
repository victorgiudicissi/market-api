package com.market.api.controller;

import com.market.api.dto.Page;
import com.market.api.dto.cart.CartRequestDto;
import com.market.api.dto.cart.CartResponseDto;
import com.market.api.dto.item.FilterCartRequestDto;
import com.market.api.model.enums.Status;
import com.market.api.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponseDto> createCart(@Valid @RequestBody CartRequestDto cartRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.createCart(cartRequestDto));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CartResponseDto> getCartByUuid(@PathVariable("uuid") String uuid) {
        return ResponseEntity.status(HttpStatus.OK).body(cartService.getByUuid(uuid));
    }

    @GetMapping()
    public ResponseEntity<Page<CartResponseDto>> filterCart(@Valid FilterCartRequestDto filter) {
        return ResponseEntity.status(HttpStatus.OK).body(cartService.findCartByFilter(filter));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<CartResponseDto> deleteCartByUuid(@PathVariable("uuid") String uuid) {
        cartService.deleteByUuid(uuid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{uuid}/status/{status}")
    public ResponseEntity<CartResponseDto> changeCartStatus(@PathVariable("uuid") String uuid, @PathVariable("status") Status status) {
        return ResponseEntity.status(HttpStatus.OK).body(cartService.updateStatus(uuid, status));
    }
}
