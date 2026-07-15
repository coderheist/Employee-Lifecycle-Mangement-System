package com.genc.hrms.repository;

import com.genc.hrms.model.JobRequisition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRequisitionRepository extends JpaRepository<JobRequisition, Integer> {
}
