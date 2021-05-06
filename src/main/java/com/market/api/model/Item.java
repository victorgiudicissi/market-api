package com.market.api.model;

import com.market.api.dto.item.ItemResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Builder
@Document(collection = "item")
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    private String uuid;
    private String description;
    private Long amount;
    private Long quantity;
    private boolean enabled;

    public ItemResponseDto toItemResponseDto() {
        return ItemResponseDto.builder()
                .uuid(this.uuid)
                .enabled(this.enabled)
                .quantityAvailable(this.quantity)
                .amount(this.amount)
                .description(this.description)
                .build();
    }
}
