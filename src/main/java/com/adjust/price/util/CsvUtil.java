package com.adjust.price.util;

import com.adjust.price.annotation.CsvColumn;
import com.adjust.price.exception.CsvFileException;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.*;

@Slf4j
@UtilityClass
public class CsvUtil {
    private static final String CELL_DELIMITER = ",";
    private static final String DATE_TIME_PATTERNS = "dd.MM.yyyy HH:mm:ss";


    public static <T> List<T> parseFromCsvFile(MultipartFile mFile, Class<T> clazz) {
        List<T> dataList = new ArrayList<>();
        List<String> records = readFromFile(mFile);
        List<Field> fields;

        try {
            checkRawSourceData(records, mFile.getName());
            fields = createFields(clazz);
        } catch (CsvFileException e) {
            log.warn(e.getMessage(), e);
            return dataList;
        }

        Map<Integer, Field> reflectionMap = new HashMap<>();
        Map<String, Field> classMap = createClassMap(fields);

        boolean firstRow = true;
        for (String record : records) {
            String[] row = record.split(CELL_DELIMITER);
            if (firstRow) {
                conventFirstRow(row, reflectionMap, classMap);
                firstRow = false;
            } else {
                if (row.length == 0) {
                    continue;
                }
                try {
                    T rowData = clazz.getDeclaredConstructor().newInstance();
                    boolean allBlank = convertRowData(row, reflectionMap, rowData);
                    if (allBlank) {
                        dataList.add(rowData);
                    }
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                    log.error("Exception in parsed row: {}", row, e);
                }
            }
        }
        return dataList;
    }

    @SneakyThrows
    public <T> Path writeToFile(Path path, List<T> data, Class<T> clazz) {
        List<Field> fields;

        try {
            fields = createFields(clazz);
        } catch (CsvFileException e) {
            log.warn(e.getMessage(), e);
            return Path.of("NO_FILE");
        }

        Map<String, Field> classMap = createClassMap(fields);
        List<String> output = new ArrayList<>();
        String header = String.join(CELL_DELIMITER, classMap.keySet());
        output.add(header);
        List<String> rows = data.stream()
                .map(it -> {
                    List<String> values = new ArrayList<>();
                    classMap.forEach((key, field) -> values.add(getValueByType(field, it)));
                    return String.join(CELL_DELIMITER, values);
                })
                .toList();
        output.addAll(rows);

        Files.deleteIfExists(path);
        Files.write(path, output, CREATE, TRUNCATE_EXISTING);
        return path;
    }

    private List<String> readFromFile(MultipartFile mFile) {
        List<String> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(mFile.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                records.add(line);
            }
        } catch (IOException e) {
            log.warn("Problem with reading file with name - {}", mFile.getName(), e);
        }
        return records;
    }

    private boolean validateEmptyData(List<String> records) {
        return records.stream()
                .anyMatch(it -> it.contains(CELL_DELIMITER + CELL_DELIMITER));
    }

    private Map<String, Field> createClassMap(List<Field> fields) {
        return fields.stream()
                .collect(Collectors.toMap(
                        it -> {
                            CsvColumn annotation = it.getAnnotation(CsvColumn.class);
                            return annotation.name().trim();
                        },
                        Function.identity(),
                        (o1, o2) -> o1,
                        LinkedHashMap::new));
    }


    private <T> List<Field> createFields(Class<T> clazz) throws CsvFileException {
        Field[] allFields = clazz.getDeclaredFields();

        List<Field> annotatedFields = Arrays.stream(allFields)
                .filter(field -> field.isAnnotationPresent(CsvColumn.class))
                .sorted(Comparator.comparing(field -> {
                    int col = 0;
                    CsvColumn annotation = field.getAnnotation(CsvColumn.class);
                    if (annotation != null) {
                        col = annotation.col();
                    }
                    return col;
                }))
                .toList();

        if (annotatedFields.isEmpty()) {
            throw new CsvFileException("not support for CSV parsing, no any usage @CsvColumn under fields ",
                    clazz.getSimpleName());
        }
        return annotatedFields;
    }

    private void conventFirstRow(String[] cells, Map<Integer, Field> reflectionMap,
                                 Map<String, Field> classMap) {

        for (int i = 0; i < cells.length; i++) {
            var cellValue = cells[i].trim();
            if (classMap.containsKey(cellValue)) {
                reflectionMap.put(i + 1, classMap.get(cellValue));
            }
        }
    }

    private <T> Boolean convertRowData(String[] row, Map<Integer, Field> reflectionMap, T rowData) {
        boolean allBlank = true;
        for (int i = 0; i < row.length; i++) {
            if (reflectionMap.containsKey(i + 1)) {
                var cell = row[i];
                Field field = reflectionMap.get(i + 1);
                try {
                    setValueByType(cell, field, rowData);
                } catch (IllegalAccessException e) {
                    log.warn("reflect field: {} name: {} exception!", field.getName(), cell, e);
                } catch (ParseException e) {
                    log.warn("DateFormat in source file issue, value is {} ", cell, e);
                }
            }
        }
        return allBlank;

    }

    private <T> void setValueByType(String value, Field field, T rowData) throws IllegalAccessException, ParseException {
        field.setAccessible(true);
        Class<?> type = field.getType();

        if (type == int.class || type == Integer.class) {
            field.set(rowData, Integer.parseInt(value));
        } else if (type == long.class || type == Long.class) {
            field.set(rowData, Long.parseLong(value));
        } else if (type == LocalDateTime.class) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERNS);
            field.set(rowData, LocalDateTime.parse(value, formatter));
        } else {
            field.set(rowData, value);
        }
    }

    @SneakyThrows
    private <T> String getValueByType(Field field, T rowData) {
        field.setAccessible(true);
        Class<?> type = field.getType();
        return String.valueOf(field.get(rowData));
    }

    private <T> void checkRawSourceData(List<String> records, String fileName) throws CsvFileException {
        if (records.isEmpty()) {
            throw new CsvFileException("file is empty", fileName);
        }

        if (validateEmptyData(records)) {
            throw new CsvFileException("not all fields filled in file ", fileName);
        }
    }
}
