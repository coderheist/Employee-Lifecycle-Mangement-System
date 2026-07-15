package com.genc.hrms.repository;

import com.genc.hrms.model.MarkAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface MarkAttendanceRepository extends JpaRepository<MarkAttendance,Long> {

    boolean existsByEmployee_EmployeeIdAndPresentDate(long employeeId, LocalDate date);


    MarkAttendance findByEmployee_EmployeeIdAndPresentDate(long employeeId, LocalDate date);

    List<MarkAttendance> findByEmployee_EmployeeId(long employeeId);
}
