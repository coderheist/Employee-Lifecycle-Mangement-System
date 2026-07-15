package com.genc.hrms.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Interview")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer interviewId;

    @NotNull(message = "Candidate ID cannot be null")
    @Column(nullable = false)
    private Integer candidateId;

    @NotBlank(message = "Interviewer name cannot be empty")
    @Size(min = 2, max = 100, message = "Interviewer name must be between 2 and 100 characters")
    @Column(length = 100)
    private String interviewerName;

    @NotNull(message = "Interview date and time cannot be null")
    @FutureOrPresent(message = "Interview date must be today or in the future")
    private LocalDateTime interviewDateTime;

    @NotBlank(message = "Interview mode cannot be empty")
    @Column(length = 50)
    private String interviewMode;

    @Size(max = 200, message = "Location must not exceed 200 characters")
    @Column(length = 200)
    private String location;

    @Size(max = 500, message = "Meeting link must not exceed 500 characters")
    @Column(length = 500)
    private String meetingLink;

    @NotNull(message = "Interview round cannot be null")
    @Column(length = 50)
    private String interviewRound;

    @NotNull(message = "Interview status cannot be null")
    @Enumerated(EnumType.STRING)
    private InterviewStatus interviewStatus;

    @Size(max = 1000, message = "Remarks must not exceed 1000 characters")
    @Column(length = 1000)
    private String remarks;

    public enum InterviewStatus {
        SCHEDULED, COMPLETED, CANCELLED, RESCHEDULED, NO_SHOW
    }
}
