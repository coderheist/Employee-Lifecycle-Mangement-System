package com.genc.hrms.service;

import com.genc.hrms.model.Attendance;
import com.genc.hrms.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ManagerService {
    @Autowired
    private AttendanceRepository attendanceRepository;

    public void approveLeaveRequest(long leaveId) {
        Attendance existingRequest = attendanceRepository.findById(leaveId).orElse(null);
        existingRequest.setStatus(Attendance.LeaveStatus.APPROVED);
        attendanceRepository.save(existingRequest);
    }

    public void rejectLeaveRequest(long leaveId) {
        Attendance existingRequest = attendanceRepository.findById(leaveId).orElse(null);
        existingRequest.setStatus(Attendance.LeaveStatus.REJECTED);
        attendanceRepository.save(existingRequest);
    }

    public List<Attendance> returnApplied()
    {
        List<Attendance> lst=attendanceRepository.findByStatus(Attendance.LeaveStatus.APPLIED);
        return lst;
    }
}
