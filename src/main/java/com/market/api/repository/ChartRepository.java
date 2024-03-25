package com.market.api.repository;

import com.market.api.model.Chart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChartRepository extends MongoRepository<Chart, String> {
    Chart findByUuid(String uuid);
}
