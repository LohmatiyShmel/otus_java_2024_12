package ru.otus.services.processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;
import ru.otus.lib.SensorDataBufferedWriter;

// Этот класс нужно реализовать
@SuppressWarnings({"java:S1068", "java:S125"})
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final ArrayBlockingQueue<SensorData> dataBuffer;
    private final ReentrantLock lock = new ReentrantLock();

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size must be positive");
        }
        this.bufferSize = bufferSize;
        this.writer = writer;
        this.dataBuffer = new ArrayBlockingQueue<>(bufferSize);
    }

    @Override
    public void process(SensorData data) {
        try {
            dataBuffer.put(data);
            if (dataBuffer.size() >= bufferSize) {
                flush();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Ошибка добавления данных в буфер", e);
        }
    }

    public void flush() {
        List<SensorData> dataToWrite = new ArrayList<>(bufferSize);
        dataBuffer.drainTo(dataToWrite);
        if (!dataToWrite.isEmpty()) {
            try {
                dataToWrite.sort((d1, d2) -> d1.getMeasurementTime().compareTo(d2.getMeasurementTime()));
                writer.writeBufferedData(dataToWrite);
            } catch (Exception e) {
                log.error("Ошибка в процессе записи буфера", e);
            }
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}
