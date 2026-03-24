package ru.job4j.devops.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An object with two args
 */
@Data
public class TwoArgs {
    private double first;
    private double second;

    /**
     * Default constructor
     */
    public TwoArgs() {
    }
    /**
     * All args constructor
     * @param first to set
     * @param second to set
     */
    public TwoArgs(double first, double second) {
        this.first = first;
        this.second = second;
    }
}
