package ru.otus.dataprocessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.otus.model.Measurement;

public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(final List<Measurement> data) {
        // группирует выходящий список по name, при этом суммирует поля value
        if (data.isEmpty()) {
            throw new FileProcessException("Dataset is empty");
        }
        final Map<String, Double> summedValues = new HashMap<>();
        for (Measurement measurement : data) {
            if (summedValues.get(measurement.name()) != null) {
                summedValues.put(measurement.name(), summedValues.get(measurement.name()) + measurement.value());
                continue;
            }
            summedValues.put(measurement.name(), measurement.value());
        }
        return summedValues;
    }
}
