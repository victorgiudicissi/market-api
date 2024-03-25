package com.market.api.model;

import com.market.api.dto.item.ItemResponseDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
@Builder(toBuilder = true)
@Document(collection = "item")
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    private String uuid;
    private String marketUuid;
    private String description;
    private Long price;
    private Long quantity;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ItemResponseDto toItemResponseDto() {
        return ItemResponseDto.builder()
                .uuid(this.uuid)
                .marketUuid(this.marketUuid)
                .enabled(this.enabled)
                .quantity(this.quantity)
                .price(this.price)
                .description(this.description)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
