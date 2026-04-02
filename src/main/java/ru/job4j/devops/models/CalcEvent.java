package ru.job4j.devops.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "calc_events")
public class CalcEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    private Long first;
    private Long second;
    private Long result;
    @Column(name = "create_date")
    private LocalDateTime createDate;
    private String type;
}
