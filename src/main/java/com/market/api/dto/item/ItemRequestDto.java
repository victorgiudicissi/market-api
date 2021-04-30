package com.market.api.dto.item;

import com.market.api.model.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private String description;
    private Long price;
    private Long quantity;
    private boolean enabled;

    public Item toItem() {
        return Item.builder()
                .uuid(UUID.randomUUID().toString())
                .description(this.description)
                .quantity(this.quantity)
                .price(this.price)
                .enabled(this.enabled)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Item toItem(String uuid, LocalDateTime createdAt) {
        return Item.builder()
                .uuid(uuid)
                .description(this.description)
                .quantity(this.quantity)
                .price(this.price)
                .enabled(this.enabled)
                .createdAt(createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
