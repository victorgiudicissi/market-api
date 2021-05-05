package com.market.api.dto.item;

import com.market.api.model.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private String description;
    private Long amount;
    private Long quantityAvailable;
    private boolean enabled;

    public Item toItem() {
        return Item.builder()
                .uuid(UUID.randomUUID().toString())
                .description(this.description)
                .quantityAvailable(this.quantityAvailable)
                .amount(this.amount)
                .enabled(this.enabled)
                .build();
    }
}
