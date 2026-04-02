package ru.job4j.devops.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.job4j.devops.models.CalcEvent;
import ru.job4j.devops.models.User;
import ru.job4j.devops.repository.CalcEventRepository;
import ru.job4j.devops.repository.UserRepository;

@Component
@AllArgsConstructor
@Slf4j
public class CalcEventListener {

    private final CalcEventRepository calcEventRepository;

    @KafkaListener(topics = "saveCalc", groupId = "job4j")
    public void save(CalcEvent calcEvent) {
        log.debug("Received calc from user: {}", calcEvent.getUserId());
        calcEventRepository.save(calcEvent);
    }
}