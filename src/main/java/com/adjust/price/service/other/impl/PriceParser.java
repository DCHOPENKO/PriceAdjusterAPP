package com.adjust.price.service.other.impl;

import com.adjust.price.model.Price;
import com.adjust.price.repository.PriceRepository;
import com.adjust.price.service.other.ManualManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PriceParser implements ManualManager<Price> {

    private final List<Price> prices = new ArrayList<>();

    private final PriceRepository repository;

    public PriceParser(PriceRepository repository) {
        this.repository = repository;
    }

    @Override
    public JpaRepository<Price, Long> getRepo() {
        return repository;
    }

    @Override
    public void addLog(String logs) {
        log.info(logs);
    }

    @Override
    public List<Price> getData() {
        return prices;
    }

}
