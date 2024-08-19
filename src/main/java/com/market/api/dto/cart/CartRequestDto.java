package com.market.api.dto.cart;

import com.market.api.dto.item.CartItemRequestDto;
import com.market.api.entity.Cart;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartRequestDto {
    @Valid
    @NotNull(message = "Cart items may not be null")
    List<CartItemRequestDto> items;
    @NotNull(message = "Market uuid may not be null")
    String marketUuid;

    public Cart toCart() {
        return Cart.builder()
                .marketUuid(this.marketUuid)
                .items(this.items.stream().map(CartItemRequestDto::toItem).collect(Collectors.toSet()))
                .build();
    }
}
