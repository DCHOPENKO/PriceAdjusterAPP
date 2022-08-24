package com.adjust.price.service.file;

import com.adjust.price.model.Price;

import java.nio.file.Path;
import java.util.List;

public interface FileHandler {

    Path createFileWithData(List<Price> data);
}
