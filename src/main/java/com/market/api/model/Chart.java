package com.market.api.model;

import com.market.api.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Builder
@Document(collection = "chart")
@NoArgsConstructor
@AllArgsConstructor
public class Chart {
    @Id
    private String uuid;
    private Long amountInCents;
    private Status status;
}
