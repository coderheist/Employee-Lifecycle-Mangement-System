package com.genc.hrms.controller;

import com.genc.hrms.model.Interview;
import com.genc.hrms.model.JobRequisition;
import com.genc.hrms.model.Offer;
import com.genc.hrms.model.Recruiter;
import com.genc.hrms.service.InterviewService;
import com.genc.hrms.service.JobRequisitionService;
import com.genc.hrms.service.OfferService;
import com.genc.hrms.service.RecruiterService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/recruitment")
public class RecruitmentRestController {

    private final Logger logger = LoggerFactory.getLogger(RecruitmentRestController.class);
    @Autowired
    private RecruiterService candidateService;
    @Autowired
    private JobRequisitionService jobRequisitionService;
    @Autowired
    private InterviewService interviewService;
    @Autowired
    private OfferService offerService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String,Long>> getDashboard() {
        logger.info("Fetching dashboard data");
        Map<String, Long> dashboard = new HashMap<>();
        dashboard.put("candidateCount", candidateService.getCandidateCount());
        dashboard.put("requisitionCount", jobRequisitionService.getRequisitionCount());
        dashboard.put("interviewCount", interviewService.getInterviewCount());
        dashboard.put("offerCount", offerService.getOfferCount());
        logger.info("Dashboard data: {}", dashboard);
        return ResponseEntity.ok(dashboard);
    }


    @PostMapping("/candidates")
    public ResponseEntity<?> addCandidate(@Valid @RequestBody Recruiter candidate) {
        logger.info("Adding new candidate: {}", candidate.getFullName());
        try {
            Recruiter saved = candidateService.saveData(candidate);
            logger.info("Candidate added successfully with ID: {}", saved.getCandidateId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            logger.error("Error adding candidate: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/candidates")
    public ResponseEntity<List<Recruiter>> getAllCandidates() {
        logger.info("Fetching all candidates");
        return ResponseEntity.ok(candidateService.getAllCandidates());
    }

    @GetMapping("/candidates/{id}")
    public ResponseEntity<?> getCandidateById(@PathVariable Integer id) {
        logger.info("Fetching candidate with ID: {}", id);
        Recruiter candidate = candidateService.getCandidateById(id);
        if (candidate == null) {
            logger.warn("Candidate not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Candidate not found"));
        }
        return ResponseEntity.ok(candidate);
    }

    @PutMapping("/candidates/{id}")
    public ResponseEntity<?> updateCandidate(@PathVariable Integer id, @Valid @RequestBody Recruiter candidate) {
        logger.info("Updating candidate with ID: {}", id);
        try {
            Recruiter existing = candidateService.getCandidateById(id);
            if (existing == null) {
                logger.warn("Candidate not found for update with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Candidate not found"));
            }
            candidate.setCandidateId(id);
            Recruiter updated = candidateService.updateCandidate(candidate);
            logger.info("Candidate updated successfully with ID: {}", id);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error updating candidate with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/job-requisitions")
    public ResponseEntity<?> createJobRequisition(@Valid @RequestBody JobRequisition jobRequisition) {
        logger.info("Creating new job requisition: {}", jobRequisition.getJobTitle());
        try {
            JobRequisition saved = jobRequisitionService.createJobRequisition(jobRequisition);
            logger.info("Job requisition created successfully with ID: {}", saved.getRequisitionId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            logger.error("Error creating job requisition: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/job-requisitions")
    public ResponseEntity<List<JobRequisition>> getAllJobRequisitions() {
        logger.info("Fetching all job requisitions");
        return ResponseEntity.ok(jobRequisitionService.getAllJobRequisitions());
    }

    @GetMapping("/job-requisitions/{id}")
    public ResponseEntity<?> getJobRequisitionById(@PathVariable Integer id) {
        logger.info("Fetching job requisition with ID: {}", id);
        JobRequisition req = jobRequisitionService.getJobRequisitionById(id);
        if (req == null) {
            logger.warn("Job requisition not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Job Requisition not found"));
        }
        return ResponseEntity.ok(req);
    }

    @PutMapping("/job-requisitions/{id}")
    public ResponseEntity<?> updateJobRequisition(@PathVariable Integer id, @Valid @RequestBody JobRequisition jobRequisition) {
        logger.info("Updating job requisition with ID: {}", id);
        try {
            JobRequisition existing = jobRequisitionService.getJobRequisitionById(id);
            if (existing == null) {
                logger.warn("Job requisition not found for update with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Job Requisition not found"));
            }
            jobRequisition.setRequisitionId(id);
            JobRequisition updated = jobRequisitionService.updateJobRequisition(jobRequisition);
            logger.info("Job requisition updated successfully with ID: {}", id);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error updating job requisition with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/job-requisitions/{id}")
    public ResponseEntity<?> deleteJobRequisition(@PathVariable Integer id) {
        logger.info("Deleting job requisition with ID: {}", id);
        JobRequisition req = jobRequisitionService.getJobRequisitionById(id);
        if (req == null) {
            logger.warn("Job requisition not found for deletion with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Job Requisition not found"));
        }
        jobRequisitionService.deleteJobRequisition(id);
        logger.info("Job requisition deleted successfully with ID: {}", id);
        return ResponseEntity.ok(Map.of("message", "Job Requisition deleted successfully"));
    }

    @PostMapping("/interviews")
    public ResponseEntity<?> scheduleInterview(@Valid @RequestBody Interview interview) {
        logger.info("Scheduling interview for candidate ID: {}", interview.getCandidateId());
        try {
            Interview scheduled = interviewService.scheduleInterview(interview);
            logger.info("Interview scheduled successfully with ID: {}", scheduled.getInterviewId());
            return ResponseEntity.status(HttpStatus.CREATED).body(scheduled);
        } catch (Exception e) {
            logger.error("Error scheduling interview: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/interviews")
    public ResponseEntity<List<Interview>> getAllInterviews() {
        logger.info("Fetching all interviews");
        return ResponseEntity.ok(interviewService.getAllInterviews());
    }

    @GetMapping("/interviews/{id}")
    public ResponseEntity<?> getInterviewById(@PathVariable Integer id) {
        logger.info("Fetching interview with ID: {}", id);
        Interview interview = interviewService.getInterviewById(id);
        if (interview == null) {
            logger.warn("Interview not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Interview not found"));
        }
        return ResponseEntity.ok(interview);
    }

    @GetMapping("/interviews/candidate/{candidateId}")
    public ResponseEntity<List<Interview>> getInterviewsByCandidateId(@PathVariable Integer candidateId) {
        logger.info("Fetching interviews for candidate ID: {}", candidateId);
        return ResponseEntity.ok(interviewService.getInterviewsByCandidateId(candidateId));
    }

    @PutMapping("/interviews/{id}")
    public ResponseEntity<?> updateInterview(@PathVariable Integer id, @Valid @RequestBody Interview interview) {
        logger.info("Updating interview with ID: {}", id);
        try {
            Interview existing = interviewService.getInterviewById(id);
            if (existing == null) {
                logger.warn("Interview not found for update with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Interview not found"));
            }
            interview.setInterviewId(id);
            Interview updated = interviewService.updateInterview(interview);
            logger.info("Interview updated successfully with ID: {}", id);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error updating interview with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/interviews/{id}")
    public ResponseEntity<?> deleteInterview(@PathVariable Integer id) {
        logger.info("Deleting interview with ID: {}", id);
        Interview interview = interviewService.getInterviewById(id);
        if (interview == null) {
            logger.warn("Interview not found for deletion with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Interview not found"));
        }
        interviewService.deleteInterview(id);
        logger.info("Interview deleted successfully with ID: {}", id);
        return ResponseEntity.ok(Map.of("message", "Interview deleted successfully"));
    }

    @PostMapping("/offers")
    public ResponseEntity<?> rolloutOffer(@Valid @RequestBody Offer offer) {
        logger.info("Rolling out offer for candidate ID: {}", offer.getCandidateId());
        try {
            Offer saved = offerService.rolloutOffer(offer);
            logger.info("Offer rolled out successfully with ID: {}", saved.getOfferId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            logger.error("Error rolling out offer: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/offers")
    public ResponseEntity<List<Offer>> getAllOffers() {
        logger.info("Fetching all offers");
        return ResponseEntity.ok(offerService.getAllOffers());
    }

    @GetMapping("/offers/{id}")
    public ResponseEntity<?> getOfferById(@PathVariable Integer id) {
        logger.info("Fetching offer with ID: {}", id);
        Offer offer = offerService.getOfferById(id);
        if (offer == null) {
            logger.warn("Offer not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Offer not found"));
        }
        return ResponseEntity.ok(offer);
    }



    @PutMapping("/offers/{id}")
    public ResponseEntity<?> updateOffer(@PathVariable Integer id, @Valid @RequestBody Offer offer) {
        logger.info("Updating offer with ID: {}", id);
        try {
            Offer existing = offerService.getOfferById(id);
            if (existing == null) {
                logger.warn("Offer not found for update with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Offer not found"));
            }
            offer.setOfferId(id);
            Offer updated = offerService.updateOffer(offer);
            logger.info("Offer updated successfully with ID: {}", id);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error updating offer with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/offers/{id}/status")
    public ResponseEntity<?> updateOfferStatus(@PathVariable Integer id, @RequestBody Map<String,String> statusUpdate) {
        logger.info("Updating offer status for ID: {}", id);
        try {
            String statusStr = statusUpdate.get("status");
            Offer.OfferStatus newStatus = Offer.OfferStatus.valueOf(statusStr);
            Offer updated = offerService.updateOfferStatus(id, newStatus);
            logger.info("Offer status updated to {} for ID: {}", newStatus, id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid status value for offer ID {}: {}", id, statusUpdate.get("status"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid status value"));
        } catch (Exception e) {
            logger.error("Error updating offer status for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/offers/{id}")
    public ResponseEntity<?> deleteOffer(@PathVariable Integer id) {
        logger.info("Deleting offer with ID: {}", id);
        Offer offer = offerService.getOfferById(id);
        if (offer == null) {
            logger.warn("Offer not found for deletion with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Offer not found"));
        }
        offerService.deleteOffer(id);
        logger.info("Offer deleted successfully with ID: {}", id);
        return ResponseEntity.ok(Map.of("message", "Offer deleted successfully"));
    }

}