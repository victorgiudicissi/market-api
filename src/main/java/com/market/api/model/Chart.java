package com.market.api.model;

import com.market.api.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String uuid;
    @Column(nullable = false)
    private Long amountInCents;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "id")
    private List<Item> items;
}
