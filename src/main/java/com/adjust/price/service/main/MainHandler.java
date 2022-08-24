package com.adjust.price.service.main;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MainHandler {

    ResponseEntity<Resource> getFileWithAllDataFromDataBase ();

    void renewPriceDatainToDatabase(List<MultipartFile> files);
}
