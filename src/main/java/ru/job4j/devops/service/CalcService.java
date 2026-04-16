package ru.job4j.devops.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.job4j.devops.models.CalcEvent;
import ru.job4j.devops.models.User;
import ru.job4j.devops.repository.CalcEventRepository;

import java.util.List;

@Service
@SuppressFBWarnings(
    value = "EI_EXPOSE_REP2",
    justification = "KafkaTemplate is a Spring-managed bean, not an external mutable object"
)
@RequiredArgsConstructor
public class CalcService {
    private final CalcEventRepository calcEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final MeterRegistry meterRegistry;

    private Counter calcOperationCounter;
    private Counter asyncOperationCounter;
    private Timer dbSaveTimer;

    @PostConstruct
    private void initMetrics() {
        this.calcOperationCounter = meterRegistry.counter("calculator.operations.total");
        this.asyncOperationCounter = meterRegistry.counter("calculator.operations.async");
        this.dbSaveTimer = meterRegistry.timer("calculator.db.save.time");
    }

    public void add(User user, long first, long second) {
        dbSaveTimer.record(() -> {
            long result = first + second;
            var event = new CalcEvent();
            event.setFirst(first);
            event.setSecond(second);
            event.setResult(result);
            event.setType("SUM");
            event.setUserId(user.getId());
            calcEventRepository.save(event);
        });

        calcOperationCounter.increment();
    }

    public void addAsync(User user, long first, long second) {
        long result = first + second;
        var event = new CalcEvent();
        event.setFirst(first);
        event.setSecond(second);
        event.setResult(result);
        event.setType("SUM");
        event.setUserId(user.getId());

        kafkaTemplate.send("saveCalc", event);

        calcOperationCounter.increment();
        asyncOperationCounter.increment();
    }

    public List<CalcEvent> findByUserId(Long userId) {
        return calcEventRepository.findByUserId(userId);
    }
}