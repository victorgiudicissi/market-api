package com.market.api.dto.chart;

import com.market.api.dto.item.ItemResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartRequestDto {
    List<ItemResponseDto> items;
}
