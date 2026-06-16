package com.raceon.api.domain.area.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "area", indexes = {
        @Index(name = "idx_area_code",        columnList = "area_code", unique = true),
        @Index(name = "idx_area_parent_code", columnList = "parent_code"),
        @Index(name = "idx_area_level",       columnList = "area_level")
})
@Getter
@NoArgsConstructor
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "area_idx")
    private Long areaIdx;

    /** 행정안전부 법정동코드 앞자리 (시도 2자리 / 시군구 5자리 / 읍면동 8자리) */
    @Column(name = "area_code", nullable = false, length = 10, unique = true)
    private String areaCode;

    /** 행정구역명 (예: 종로구, 수원시 장안구) */
    @Column(name = "area_name", nullable = false, length = 50)
    private String areaName;

    /** 1=시도, 2=시군구, 3=읍면동 */
    @Column(name = "area_level", nullable = false)
    private Integer areaLevel;

    /** 상위 행정구역 코드 (시도는 NULL) */
    @Column(name = "parent_code", length = 10)
    private String parentCode;

    /** 전체 경로명 (예: 경기도 수원시 장안구) */
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "create_dt", nullable = false)
    private LocalDateTime createDt;

    public Area(String areaCode, String areaName, int areaLevel, String parentCode, String fullName) {
        this.areaCode   = areaCode;
        this.areaName   = areaName;
        this.areaLevel  = areaLevel;
        this.parentCode = parentCode;
        this.fullName   = fullName;
        this.createDt   = LocalDateTime.now();
    }
}
