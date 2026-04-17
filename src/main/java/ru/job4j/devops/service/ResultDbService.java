package ru.job4j.devops.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.devops.models.Result;
import ru.job4j.devops.repository.ResultRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResultDbService implements ResultService {
    private final ResultRepository resultRepository;
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    private final MeterRegistry meterRegistry;

    private Counter saveCounter;
    private Timer saveTimer;

    @PostConstruct
    public void init() {
        this.saveCounter = meterRegistry.counter("results.save");
        this.saveTimer = meterRegistry.timer("results.save.timer");
    }

    public void save(Result result) {
        saveTimer.record(() -> {
            resultRepository.save(result);
        });
        saveCounter.increment();
    }

    public List<Result> findAll() {
        return resultRepository.findAll();
    }
}