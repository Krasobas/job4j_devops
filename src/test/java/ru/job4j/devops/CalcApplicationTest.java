package ru.job4j.devops;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CalcApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainMethodTest() {
        CalcApplication.main(new String[] {});
    }

    @Test
    public void whenNegativeNumber() {
        var input = new TwoArgs(-1, -1);
        var expected = new Result(-2);
        var output = new CalcController().summarise(input);
        assertThat(output.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(output.getBody()).isEqualTo(expected);
    }
}