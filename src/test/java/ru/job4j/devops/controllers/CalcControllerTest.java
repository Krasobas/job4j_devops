package ru.job4j.devops.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import ru.job4j.devops.models.Result;
import ru.job4j.devops.models.TwoArgs;
import ru.job4j.devops.service.ResultFakeService;

import static org.assertj.core.api.Assertions.assertThat;

class CalcControllerTest {

    @Test
    public void whenOnePlusOneThenTwo() {
        var input = new TwoArgs(1, 1);
        var expected = new Result();
        expected.setResult(2D);
        var output = new CalcController(
            new ResultFakeService()
        ).summarise(input);
        assertThat(output.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        var result = output.getBody();
        assertThat(result).isNotNull();
        assertThat(result.getResult()).isEqualTo(expected.getResult());
    }
}