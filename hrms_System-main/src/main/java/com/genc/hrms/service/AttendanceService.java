package com.genc.hrms.service;

import com.genc.hrms.dto.LeaveDto;
import com.genc.hrms.model.Attendance;
import com.genc.hrms.model.Employee;
import com.genc.hrms.model.MarkAttendance;
import com.genc.hrms.model.UserDetails;
import com.genc.hrms.repository.AttendanceRepository;
import com.genc.hrms.repository.EmployeeRepository;
import com.genc.hrms.repository.MarkAttendanceRepository;
import com.genc.hrms.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private MarkAttendanceRepository markAttendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    private long getCurrentEmployeeId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user session found.");
        }

        String username = authentication.getName();

        UserDetails userDetails = userDetailsRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User profile record not found for username: " + username));

        return userDetails.getEmployee().getEmployeeId();
    }

    public void saveLeaveRequest(Attendance attendance) {
        long currentEmpId = getCurrentEmployeeId();

        long totalDays = ChronoUnit.DAYS.between(attendance.getFromDate(), attendance.getToDate()) + 1;
        LocalDate fromDate = attendance.getFromDate();
        LocalDate toDate = attendance.getToDate();

        if (toDate.isBefore(fromDate)) {
            throw new IllegalArgumentException("To date cannot be before from date.");
        }

        attendance.setTotalDays(totalDays);
        attendance.setStatus(Attendance.LeaveStatus.APPLIED);

        Employee emp = new Employee();
        emp.setEmployeeId(currentEmpId);
        attendance.setEmployee(emp);

        attendanceRepository.save(attendance);
    }

    public void saveAttendance() {
        long currentEmpId = getCurrentEmployeeId();
        LocalDate today = LocalDate.now();

        boolean alreadyCheckedIn = markAttendanceRepository.existsByEmployee_EmployeeIdAndPresentDate(currentEmpId, today);

        if (!alreadyCheckedIn) {
            MarkAttendance markAttendance = new MarkAttendance();

            Employee emp = new Employee();
            emp.setEmployeeId(currentEmpId);
            markAttendance.setEmployee(emp);

            markAttendance.setInTime(LocalTime.now());
            markAttendance.setPresentDate(today);
            markAttendance.setAttendanceStatus(MarkAttendance.AttendanceStatus.PENDING);

            markAttendanceRepository.save(markAttendance);
        }
    }

    public void saveTimeSheet() {
        long currentEmpId = getCurrentEmployeeId();
        LocalDate today = LocalDate.now();
        MarkAttendance existingAttendance = markAttendanceRepository.findByEmployee_EmployeeIdAndPresentDate(currentEmpId, today);

        if (existingAttendance == null) {
            throw new IllegalStateException("You cannot clock out because you haven't checked in today!");
        }

        LocalTime alreadyCheckedOut = existingAttendance.getOutTime();
        if (alreadyCheckedOut == null) {
            existingAttendance.setOutTime(LocalTime.now());
            long totalHours = ChronoUnit.HOURS.between(existingAttendance.getInTime(), existingAttendance.getOutTime());
            existingAttendance.setTotalHours(totalHours);

            if (totalHours >= 9) {
                existingAttendance.setAttendanceStatus(MarkAttendance.AttendanceStatus.PRESENT);
            } else {
                existingAttendance.setAttendanceStatus(MarkAttendance.AttendanceStatus.ABSENT);
            }

            markAttendanceRepository.save(existingAttendance);
        } else {
            throw new IllegalStateException("You have already clocked out today!");
        }
    }

    public MarkAttendance getTodayAttendance() {
        long currentEmpId = getCurrentEmployeeId();
        LocalDate today = LocalDate.now();
        return markAttendanceRepository.findByEmployee_EmployeeIdAndPresentDate(currentEmpId, today);
    }

    public LeaveDto leaves() {
        long currentEmpId = getCurrentEmployeeId();

        int casual = attendanceRepository.findByLeaveTypeAndStatusAndEmployee_EmployeeId(
                Attendance.Leave.CASUAL, Attendance.LeaveStatus.APPROVED, currentEmpId).size();

        int sick = attendanceRepository.findByLeaveTypeAndStatusAndEmployee_EmployeeId(
                Attendance.Leave.SICK, Attendance.LeaveStatus.APPROVED, currentEmpId).size();

        int earned = attendanceRepository.findByLeaveTypeAndStatusAndEmployee_EmployeeId(
                Attendance.Leave.EARNED, Attendance.LeaveStatus.APPROVED, currentEmpId).size();

        return new LeaveDto(sick, casual, earned);
    }

    public List<Attendance> getLeaveHistory() {
        long currentEmpId = getCurrentEmployeeId();
        return attendanceRepository.findByEmployee_EmployeeId(currentEmpId);
    }

    public List<MarkAttendance> getTimeSheetHistory() {
        long currentEmpId = getCurrentEmployeeId();
        return markAttendanceRepository.findByEmployee_EmployeeId(currentEmpId);
    }

    public String getEmployeeName() {
        long currentEmpId = getCurrentEmployeeId();
       Employee emp=employeeRepository.findByEmployeeId(currentEmpId);
         return emp.getName();
    }
}