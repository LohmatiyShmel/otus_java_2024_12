package ru.otus.dataprocessor;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import ru.otus.model.Measurement;

public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(final List<Measurement> data) {
        // группирует выходящий список по name, при этом суммирует поля value
        if (data.isEmpty()) {
            throw new FileProcessException("Dataset is empty");
        }
        return data.stream()
                .collect(Collectors.groupingBy(
                        Measurement::name, TreeMap::new, Collectors.summingDouble(Measurement::value)));
    }
}
