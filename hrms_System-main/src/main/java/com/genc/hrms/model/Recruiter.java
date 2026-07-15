package com.genc.hrms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Candidate")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recruiter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer candidateId;

    @NotBlank(message = "Full name cannot be empty")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Column(length = 100)
    private String fullName;

    @NotBlank(message = "Applied role cannot be empty")
    @Size(min = 2, max = 100, message = "Applied role must be between 2 and 100 characters")
    @Column(length = 100)
    private String appliedRole;

    @NotNull(message = "Experience years cannot be null")
    @Min(value = 0, message = "Experience years must be at least 0")
    @Max(value = 50, message = "Experience years must not exceed 50")
    private Integer experienceYears;

    @NotBlank(message = "Interview stage cannot be empty")
    @Column(length = 50)
    private String interviewStage;

    @NotNull(message = "Candidate status cannot be null")
    @Enumerated(EnumType.STRING)
    private CandidateStatus candidateStatus;

    public enum CandidateStatus {
        APPLIED, IN_INTERVIEW, OFFERED, HIRED, REJECTED
    }
}
