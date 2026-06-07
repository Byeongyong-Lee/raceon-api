package com.raceon.api.domain.race.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "race")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Race {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "race_idx")
    private Long raceIdx;

    @Column(unique = true, nullable = false, length = 20)
    private String sourceId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "racedate")
    private LocalDate raceDate;

    @Column(length = 200)
    private String location;

    @Column(length = 200)
    private String course;

    @Column(length = 200)
    private String organizer;

    @Column(length = 20)
    private String phone;

    @Column(length = 300)
    private String homepage;

    @Column(name = "detailurl", length = 500)
    private String detailUrl;

    @CreationTimestamp
    @Column(name = "create_dt", updatable = false, nullable = false)
    private LocalDateTime createDt;

    public void update(String name, LocalDate raceDate, String location, String course,
                       String organizer, String phone, String homepage) {
        this.name = name;
        this.raceDate = raceDate;
        this.location = location;
        this.course = course;
        this.organizer = organizer;
        this.phone = phone;
        this.homepage = homepage;
    }
}
