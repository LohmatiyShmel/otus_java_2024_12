package ru.otus.dataprocessor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import ru.otus.model.Measurement;

public class FileSerializer implements Serializer {
    private final String fileName;

    public FileSerializer(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void serialize(final Map<String, Double> data) {
        // формирует результирующий json и сохраняет его в файл
        final List<Measurement> measurements = new ArrayList<>();
        data.forEach((name, value) -> measurements.add(new Measurement(name, value)));
        measurements.sort(Comparator.comparing(Measurement::name));
        String json = buildJson(measurements);

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
        } catch (IOException e) {
            throw new FileProcessException(e);
        }
    }

    private String buildJson(final List<Measurement> measurements) {
        final StringBuilder sb = new StringBuilder("{");

        for (int i = 0; i < measurements.size(); i++) {
            Measurement m = measurements.get(i);
            sb.append("\"").append(escapeJson(m.name())).append("\":").append(m.value());

            if (i < measurements.size() - 1) {
                sb.append(",");
            }
        }
        return sb.append("}").toString();
    }

    private String escapeJson(final String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
