package com.genc.hrms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="leave_id")
    private long leaveId;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonIgnoreProperties({"manager", "subordinates"})
    private Employee employee;

    private long totalDays;

    @Column(name = "leave_type")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please select one type of leave")
    private Leave leaveType;
    public enum Leave {
        SICK,
        CASUAL,
        EARNED
    }

    @Column(name = "from_date")
    @NotNull(message = "Please select a start date")
    @FutureOrPresent(message = "From date must be in the present or future")
    private LocalDate fromDate;

    @Column(name = "to_date")
    @NotNull(message = "Please select an end date")
    @FutureOrPresent(message = "To date must be in the present or future")
    private LocalDate toDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private LeaveStatus status;
    public enum LeaveStatus {
        APPLIED,
        APPROVED,
        REJECTED
    }
}
