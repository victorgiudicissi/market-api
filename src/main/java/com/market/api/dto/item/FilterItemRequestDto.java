package com.market.api.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterItemRequestDto {
    private String marketUuid;
    private String description;
    private Boolean enabled;
    @Min(value = 1, message = "Minimum value for page is 1.")
    private int page = 1;
    @Min(value = 1, message = "Minimum value for size is 1.")
    private int size = 10;
}
