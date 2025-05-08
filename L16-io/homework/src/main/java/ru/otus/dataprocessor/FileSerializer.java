package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class FileSerializer implements Serializer {
    private final String fileName;
    private final ObjectMapper objectMapper;

    public FileSerializer(String fileName) {
        this.fileName = fileName;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void serialize(final Map<String, Double> data) {
        // формирует результирующий json и сохраняет его в файл
        try (FileWriter writer = new FileWriter(fileName)) {
            String json = objectMapper.writeValueAsString(data);
            writer.write(json);
        } catch (IOException e) {
            throw new FileProcessException(e);
        }
    }
}
