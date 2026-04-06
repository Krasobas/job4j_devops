package ru.job4j.devops.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.job4j.devops.models.Result;
import ru.job4j.devops.models.TwoArgs;
import ru.job4j.devops.service.ResultService;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("calc")
@AllArgsConstructor
public class CalcController {
    private final ResultService resultService;

    @PostMapping("summarise")
    public ResponseEntity<Result> summarise(@RequestBody TwoArgs twoArgs) {
        log.info("/calc/summarise");
        var result = new Result();
        result.setFirstArg(twoArgs.getFirst());
        result.setSecondArg(twoArgs.getSecond());
        result.setResult(twoArgs.getFirst() + twoArgs.getSecond());
        result.setOperation("+");
        result.setCreateDate(LocalDateTime.now());
        resultService.save(result);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/")
    public ResponseEntity<List<Result>> logs() {
        return ResponseEntity.ok(resultService.findAll());
    }
}
