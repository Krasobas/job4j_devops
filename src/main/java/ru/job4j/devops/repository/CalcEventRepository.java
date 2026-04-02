package ru.job4j.devops.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.devops.models.CalcEvent;

import java.util.List;

public interface CalcEventRepository extends CrudRepository<CalcEvent, Long> {
    List<CalcEvent> findByUserId(Long userId);
}
