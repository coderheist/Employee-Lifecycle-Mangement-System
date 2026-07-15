package com.genc.hrms.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "AppraisalRecord")
@Getter
@Setter
public class AppraisalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appraisalId;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "appraisalCycle", length = 20)
    private String appraisalCycle;

    @Column(name = "goalsAchieved")
    private Integer goalsAchieved;

    @Column(name = "overallRating", precision = 3, scale = 1)
    private BigDecimal overallRating;

    @Enumerated(EnumType.STRING)
    @Column(name = "appraisalStatus")
    private AppraisalStatus appraisalStatus;

    public enum AppraisalStatus {
        DRAFT, SELF_REVIEW, MANAGER_REVIEW, PUBLISHED
    }

}