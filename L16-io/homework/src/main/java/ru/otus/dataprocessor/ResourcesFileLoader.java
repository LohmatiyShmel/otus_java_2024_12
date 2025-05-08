package ru.otus.dataprocessor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import ru.otus.model.Measurement;

public class ResourcesFileLoader implements Loader {

    private final String fileName;
    private final ObjectMapper objectMapper;

    public ResourcesFileLoader(final String fileName) {
        this.fileName = fileName;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Measurement> load() {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return objectMapper.readValue(reader, new TypeReference<List<Measurement>>() {});
        } catch (Exception e) {
            System.err.println("Error loading file: " + e.getMessage());
            throw new FileProcessException(e);
        }
    }
}
