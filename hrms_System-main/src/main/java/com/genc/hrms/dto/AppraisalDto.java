package com.genc.hrms.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class AppraisalDto {
    private Long employeeId;
    private String appraisalCycle;
    private Integer goalsAchieved;
    private BigDecimal overallRating;
}