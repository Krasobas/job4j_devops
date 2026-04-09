package ru.job4j.devops.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "results")
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(name = "first_arg", columnDefinition = "numeric")
    private double firstArg;

    @Column(name = "second_arg", columnDefinition = "numeric")
    private double secondArg;

    @Column(name = "result", columnDefinition = "numeric")
    private Double result;

    @Column(name = "create_date")
    private OffsetDateTime createDate;

    private String operation;
}