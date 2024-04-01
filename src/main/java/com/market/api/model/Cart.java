package com.market.api.model;

import com.market.api.dto.cart.CartResponseDto;
import com.market.api.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@Document(collection = "cart")
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    private String uuid;
    private String marketUuid;
    private Status status;
    private Long price;
    private Set<Item> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CartResponseDto toCartResponseDto() {
        return CartResponseDto.builder()
                .uuid(this.uuid)
                .marketUuid(this.marketUuid)
                .price(this.price)
                .status(this.status)
                .items(this.items.stream().map(item -> item.toItemResponseDto()).collect(Collectors.toList()))
                .createdAt(this.createdAt)
                .build();
    }
}
