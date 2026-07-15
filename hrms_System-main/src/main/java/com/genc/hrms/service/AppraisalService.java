package com.genc.hrms.service;

import com.genc.hrms.model.AppraisalRecord;
import com.genc.hrms.model.Employee;
import com.genc.hrms.repository.AppraisalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;

@Service
 public class AppraisalService {

    @Autowired
    private final AppraisalRepository appraisalRepository;

    public AppraisalService(AppraisalRepository appraisalRepository) {
        this.appraisalRepository = appraisalRepository;
    }
    public List<AppraisalRecord> getAllRecords() {
        return appraisalRepository.findAll();
    }
    public AppraisalRecord getById(Long appraisalId) {
        return appraisalRepository.findById(appraisalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appraisal record not found for id: " + appraisalId));
    }

    public AppraisalRecord initializeGoals(Long employeeId, String cycle) {
        if (employeeId == null || cycle == null || cycle.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "employeeId and appraisalCycle are required");
        }

        AppraisalRecord record = appraisalRepository.findByEmployee_EmployeeIdAndAppraisalCycle(employeeId, cycle)
                .orElse(new AppraisalRecord());

        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);

        record.setEmployee(employee);
        record.setAppraisalCycle(cycle);
        record.setAppraisalStatus(AppraisalRecord.AppraisalStatus.DRAFT);
        return appraisalRepository.save(record);
    }

    public AppraisalRecord submitSelfReview(Long appraisalId, Integer goalsAchieved) {
        if (goalsAchieved == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "goalsAchieved is required");
        }

        AppraisalRecord record = appraisalRepository.findById(appraisalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appraisal record not found for id: " + appraisalId));

        record.setGoalsAchieved(goalsAchieved);
        record.setAppraisalStatus(AppraisalRecord.AppraisalStatus.SELF_REVIEW);
        return appraisalRepository.save(record);
    }

    public AppraisalRecord submitManagerReview(Long appraisalId, BigDecimal rating) {
        if (rating == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "overallRating is required");
        }

        AppraisalRecord record = appraisalRepository.findById(appraisalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appraisal record not found for id: " + appraisalId));

        record.setOverallRating(rating);
        record.setAppraisalStatus(AppraisalRecord.AppraisalStatus.MANAGER_REVIEW);
        return appraisalRepository.save(record);
    }

    public AppraisalRecord publishFinalRating(Long appraisalId) {
        AppraisalRecord record = appraisalRepository.findById(appraisalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appraisal record not found for id: " + appraisalId));

        record.setAppraisalStatus(AppraisalRecord.AppraisalStatus.PUBLISHED);
        return appraisalRepository.save(record);
    }
}
