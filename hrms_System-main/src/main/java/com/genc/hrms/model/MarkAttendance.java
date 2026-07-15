package com.genc.hrms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Data
public class MarkAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDate presentDate;

    private LocalTime inTime;

    private LocalTime outTime;

    private long totalHours;


    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonIgnoreProperties({"manager", "subordinates"})
    private Employee employee;

    @Column(name = "attendance_status")
    @Enumerated(EnumType.STRING)
    private AttendanceStatus attendanceStatus;

    public enum AttendanceStatus {
        PRESENT,
        PENDING,
        ABSENT
    }

}
