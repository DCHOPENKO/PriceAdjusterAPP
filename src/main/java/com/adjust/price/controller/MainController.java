package com.adjust.price.controller;

import com.adjust.price.service.main.MainHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/merger")
public class MainController {

    private final MainHandler priceService;

    @GetMapping("/prices")
    public ResponseEntity<Resource> downloadAllDataToFile() {
        return priceService.getFileWithAllDataFromDataBase();
    }

    @PostMapping("/prices")
    public void uploadPrices(MultipartHttpServletRequest request) {
        var filenames = request.getFileNames();
        List<MultipartFile> files = new ArrayList<>();
        while (filenames.hasNext()) {
            var fileName = filenames.next();
            var mFile = Optional.ofNullable(request.getFile(fileName)).orElseThrow(() -> new RuntimeException("no file"));
            if (Objects.requireNonNull(mFile.getOriginalFilename()).endsWith(".csv")) {
                files.add(mFile);
            }
        }
        priceService.renewPriceDatainToDatabase(files);
    }
}
