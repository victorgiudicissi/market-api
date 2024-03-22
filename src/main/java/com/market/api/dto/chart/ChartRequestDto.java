package com.market.api.dto.chart;

import com.market.api.dto.item.ChartItemRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartRequestDto {
    @Valid
    @NotNull(message = "Chart items may not be null")
    List<ChartItemRequestDto> items;
}
