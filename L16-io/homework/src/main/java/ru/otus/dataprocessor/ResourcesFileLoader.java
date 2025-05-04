package ru.otus.dataprocessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.otus.model.Measurement;

public class ResourcesFileLoader implements Loader {

    private final String fileName;

    public ResourcesFileLoader(final String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String json = readFullContent(reader);
            return parseStringJson(json);
        } catch (Exception e) {
            System.err.println("Error loading file: " + e.getMessage());
            throw new FileProcessException(e);
        }
    }

    private String readFullContent(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line.trim());
        }
        return sb.toString();
    }

    private List<Measurement> parseStringJson(String json) {
        List<Measurement> measurements = new ArrayList<>();

        Pattern pattern =
                Pattern.compile("\\{\\s*\"name\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"value\"\\s*:\\s*([0-9.]+)\\s*\\}");
        Matcher matcher = pattern.matcher(json);

        while (matcher.find()) {
            String name = matcher.group(1);
            double value = Double.parseDouble(matcher.group(2));
            measurements.add(new Measurement(name, value));
        }

        return measurements;
    }
}
