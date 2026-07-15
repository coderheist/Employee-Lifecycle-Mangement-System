package com.genc.hrms.dto;

import com.genc.hrms.model.Attendance;
import com.genc.hrms.model.MarkAttendance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;
@AllArgsConstructor
@Getter
public class HomeDto {
    LeaveDto leaveDto;
    String employeeName;
    List<Attendance> attendanceList;
    List<MarkAttendance> markAttendanceList;
}
