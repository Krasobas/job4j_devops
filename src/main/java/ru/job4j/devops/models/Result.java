package ru.job4j.devops.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "results")
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "first_arg")
    private Double firstArg;

    @Column(name = "second_arg")
    private Double secondArg;

    @Column(name = "result")
    private Double result;

    @Column(name = "create_date")
    private LocalDate createDate;

    private String operation;
}