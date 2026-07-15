package com.genc.hrms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "JobRequisition")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRequisition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requisitionId;

    @NotBlank(message = "Job title cannot be empty")
    @Size(min = 2, max = 100, message = "Job title must be between 2 and 100 characters")
    @Column(length = 100)
    private String jobTitle;

    @NotBlank(message = "Department cannot be empty")
    @Size(min = 2, max = 100, message = "Department must be between 2 and 100 characters")
    @Column(length = 100)
    private String department;

    @NotNull(message = "Number of positions cannot be null")
    @Min(value = 1, message = "Number of positions must be at least 1")
    @Max(value = 100, message = "Number of positions must not exceed 100")
    private Integer numberOfPositions;

    @NotNull(message = "Priority cannot be null")
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @NotNull(message = "Status cannot be null")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Column(length = 2000, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Requisition date is mandatory")
    @FutureOrPresent(message = "The date must be today or in the future")
    private LocalDate requisitionDate;

    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }

    public enum Status {
        OPEN, IN_PROGRESS, CLOSED
    }
}
