package com.market.api.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDto {
    private String uuid;
    private String description;
    private Long amount;
    private Long quantity;
    private boolean enabled;
}
