package com.market.api.dto.item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequestDto {
    @NotNull(message = "Uuid may not be null")
    private String uuid;
    @Positive(message = "Must be greater than 0")
    @NotNull(message = "Quantity may not be null")
    private Long quantity;
}
