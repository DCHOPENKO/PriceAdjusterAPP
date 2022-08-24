package com.adjust.price.service.file.impl;

import com.adjust.price.model.Price;
import com.adjust.price.service.file.FileHandler;
import com.adjust.price.util.CsvUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service
@AllArgsConstructor
public class CsvFileHandlerImpl implements FileHandler {
    private static final String TMP_STRING_PATH = "D:\\tmp\\file.csv";

    public Path createFileWithData(List<Price> data) {
        Path path = CsvUtil.writeToFile(Path.of(TMP_STRING_PATH), data, Price.class);
        return path;
    }
}
