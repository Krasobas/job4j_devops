package ru.job4j.devops.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.devops.models.Result;
import ru.job4j.devops.repository.ResultRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ResultDbService implements ResultService {
    private final ResultRepository resultRepository;

    public void save(Result result) {
        resultRepository.save(result);
    }

    public List<Result> findAll() {
        return resultRepository.findAll();
    }
}

