package com.adjust.price.service.main.impl;

import com.adjust.price.model.Price;
import com.adjust.price.service.file.FileHandler;
import com.adjust.price.service.main.MainHandler;
import com.adjust.price.service.merge.MergeService;
import com.adjust.price.service.other.impl.PriceParser;
import com.adjust.price.util.CsvUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MainHandlerImpl implements MainHandler {

    private final PriceParser priceParser;
    private final FileHandler fileService;
    private final MergeService mergeService;

    @SneakyThrows
    @Override
    public ResponseEntity<Resource> getFileWithAllDataFromDataBase() {
        List<Price> all = priceParser.getData();
        Path fileWithData = fileService.createFileWithData(all);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=all_data_from_price_table.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(new FileInputStream(String.valueOf(fileWithData))));
    }

    @Override
    public void renewPriceDatainToDatabase(List<MultipartFile> files) {
        List<Price> merged = new ArrayList<>();
        files.forEach(file -> {
            List<Price> newPrices = CsvUtil.parseFromCsvFile(file, Price.class);
            List<Price> oldPrices = priceParser.getData();
            merged.addAll(mergeService.merge(oldPrices, newPrices));
        });
        priceParser.saveData(merged);
    }
}
