package com.market.api.dto.cart;

import com.market.api.dto.item.CartItemRequestDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.List;

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
}
