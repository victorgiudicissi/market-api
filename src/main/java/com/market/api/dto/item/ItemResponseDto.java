package com.market.api.dto.item;

import com.market.api.model.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDto {
    private String uuid;
    private String description;
    private Long amount;
    private Long quantity;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Item toItem() {
        return Item.builder()
                .uuid(this.uuid)
                .enabled(this.enabled)
                .quantity(this.quantity)
                .amount(this.amount)
                .description(this.description)
                .build();
    }
}
