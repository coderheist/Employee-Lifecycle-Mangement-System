package com.genc.hrms.controller;

import com.genc.hrms.dto.HomeDto;
import com.genc.hrms.model.Attendance;
import com.genc.hrms.model.MarkAttendance;
import com.genc.hrms.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave")
public class AttendanceController {
    @Autowired
    private AttendanceService attendanceService;



    @PostMapping("/apply")
    public String saveLeaveRequest(@Valid @RequestBody Attendance attendance) {
        attendanceService.saveLeaveRequest(attendance);
        return "Leave request processed successfully";
    }

    @GetMapping
    public HomeDto getLeaveSummary() {
        return new HomeDto(
                attendanceService.leaves(),
                attendanceService.getEmployeeName(),
                attendanceService.getLeaveHistory(),
                attendanceService.getTimeSheetHistory()
        );
    }


    @PostMapping("/mark-attendance")
    public String markAttendance() {
        attendanceService.saveAttendance();
        return "Attendance marked successfully";
    }

    @GetMapping("/today-status")
    public MarkAttendance getTodayStatus() {
        return attendanceService.getTodayAttendance();
    }

    @PostMapping("/submit-timesheet")
    public String submitTimesheet() {
        attendanceService.saveTimeSheet();
        return "Timesheet submitted successfully!";
    }

}
