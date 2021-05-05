package com.market.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Table(name = "chart_items")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartItems {
    @Id
    private Long id;
    @ManyToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "chart_id")
    private List<Chart> chart;
    @ManyToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private List<Item> item;
    private int quantity;
    private Long itemValue;
}
