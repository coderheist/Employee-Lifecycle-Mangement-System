package com.genc.hrms.repository;

import com.genc.hrms.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    boolean existsByEmployee_EmployeeIdAndPayPeriod(Long employeeId, String payPeriod);

    List<Payroll> findByPayPeriod(String payPeriod);

    Payroll findTopByEmployee_EmployeeIdOrderByPayrollIdDesc(Long employeeId);
}

