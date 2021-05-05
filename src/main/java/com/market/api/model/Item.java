package com.market.api.model;

import com.market.api.dto.item.ItemResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String uuid;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Long amount;
    @Column(nullable = false)
    private Long quantityAvailable;
    @Column(nullable = false)
    private boolean enabled;

    public ItemResponseDto toItemResponseDto() {
        return ItemResponseDto.builder()
                .uuid(this.uuid)
                .enabled(this.enabled)
                .quantityAvailable(this.quantityAvailable)
                .amount(this.amount)
                .description(this.description)
                .build();
    }
}
