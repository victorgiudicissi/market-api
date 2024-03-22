package com.market.api.dto.item;

import com.market.api.model.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    @NotNull(message = "Description may not be null")
    private String description;
    @Positive(message = "Must be greater than 0")
    @NotNull(message = "Price may not be null")
    private Long price;
    @Positive(message = "Must be greater than 0")
    @NotNull(message = "Quantity may not be null")
    private Long quantity;
    private boolean enabled = true;

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
