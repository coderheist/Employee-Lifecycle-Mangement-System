package com.genc.hrms.repository;

import com.genc.hrms.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance,Long> {

    List<Attendance> findByEmployee_EmployeeId(long id);

    List<Attendance> findByStatus(Attendance.LeaveStatus status);

    List<Attendance> findByLeaveTypeAndStatusAndEmployee_EmployeeId(Attendance.Leave leaveType, Attendance.LeaveStatus status, long id);


    List<Attendance> findByEmployee_EmployeeIdAndStatus(long employeeId, Attendance.LeaveStatus status);

}

