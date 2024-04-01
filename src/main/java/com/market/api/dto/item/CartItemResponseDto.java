package com.market.api.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDto {
    private String uuid;
    private String description;
    private Long price;
    private Long quantity;
    private boolean enabled;
}
