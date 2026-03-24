package ru.job4j.devops.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result object
 */
@Data
public class Result {
    private double value;

    /**
     * Default constructor
     */
    public Result() {
    }

    /**
     * Constructor with value
     * @param value to set
     */
    public Result(double value) {
        this.value = value;
    }
}
