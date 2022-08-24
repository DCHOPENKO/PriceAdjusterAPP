package com.adjust.price.exception;

import lombok.Getter;

@Getter
public class CsvFileException extends Exception{

    private final String message;

    public CsvFileException(String reason, String fileName) {
        this.message = String.format("CsvParser>> issue during parsing file/model -  %s, reason : %s", fileName, reason);
    }
}
