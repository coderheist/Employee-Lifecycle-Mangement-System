package com.genc.hrms.repository;


import com.genc.hrms.model.AppraisalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppraisalRepository extends JpaRepository<com.genc.hrms.model.AppraisalRecord, Long> {

    Optional<AppraisalRecord> findByEmployee_EmployeeIdAndAppraisalCycle(Long employeeId, String appraisalCycle);
}
