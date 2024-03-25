package com.market.api.dto.chart;

import com.market.api.dto.item.ItemResponseDto;
import com.market.api.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartResponseDto {
    private String uuid;
    private String marketUuid;
    private Long price;
    private Status status;
    private List<ItemResponseDto> items;
    private LocalDateTime createdAt;
}
