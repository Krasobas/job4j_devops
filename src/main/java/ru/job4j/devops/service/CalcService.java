package ru.job4j.devops.service;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.job4j.devops.models.CalcEvent;
import ru.job4j.devops.models.User;
import ru.job4j.devops.repository.CalcEventRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class CalcService {
    private CalcEventRepository calcEventRepository;
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void add(User user, long first, long second) {
        long result = first + second;
        var event = new CalcEvent();
        event.setFirst(first);
        event.setSecond(second);
        event.setResult(result);
        event.setType("SUM");
        event.setUserId(user.getId());
        calcEventRepository.save(event);
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
    }

    public List<CalcEvent> findByUserId(Long userId) {
        return calcEventRepository.findByUserId(userId);
    }
}
