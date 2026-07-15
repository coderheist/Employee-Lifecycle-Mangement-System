package com.genc.hrms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "OfferLetter")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer offerId;

    @NotNull(message = "Candidate ID cannot be null")
    @Column(nullable = false)
    private Integer candidateId;

    @NotBlank(message = "Position offered cannot be empty")
    @Size(min = 2, max = 100, message = "Position must be between 2 and 100 characters")
    @Column(length = 100)
    private String positionOffered;

    @NotBlank(message = "Department cannot be empty")
    @Size(min = 2, max = 100, message = "Department must be between 2 and 100 characters")
    @Column(length = 100)
    private String department;

    @NotNull(message = "Salary offered cannot be null")
    @DecimalMin(value = "0.0", message = "Salary must be positive")
    @Column(precision = 12, scale = 2)
    private BigDecimal salaryOffered;

    @NotNull(message = "Offer date cannot be null")
    @FutureOrPresent(message = "Offer date must be today or in the future")
    private LocalDate offerDate;

    @NotNull(message = "Joining date cannot be null")
    @FutureOrPresent(message = "Joining date must be today or in the future")
    private LocalDate joiningDate;

    @NotNull(message = "Offer status cannot be null")
    @Enumerated(EnumType.STRING)
    private OfferStatus offerStatus;

    @Size(max = 1000, message = "Additional benefits must not exceed 1000 characters")
    @Column(length = 1000)
    private String additionalBenefits;

    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    @Column(length = 500)
    private String remarks;

    public enum OfferStatus {
        DRAFTED, SENT, ACCEPTED, REJECTED, WITHDRAWN
    }
}
