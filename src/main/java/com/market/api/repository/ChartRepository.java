package com.market.api.repository;

import com.market.api.model.Chart;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChartRepository  extends MongoRepository<Chart, Long> {
}
