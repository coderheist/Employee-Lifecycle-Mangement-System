package com.genc.hrms.service;

import com.genc.hrms.model.Interview;
import com.genc.hrms.model.Recruiter;
import com.genc.hrms.repository.InterviewRepository;
import com.genc.hrms.repository.RecruiterRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterviewService {

    private final Logger logger = LoggerFactory.getLogger(InterviewService.class);

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private RecruiterRepository recruiterRepository;

    @Transactional
    public Interview scheduleInterview(Interview interview) {
        logger.info("Scheduling interview for candidate ID: {}", interview.getCandidateId());
        Recruiter candidate = recruiterRepository.findById(interview.getCandidateId())
                .orElseThrow(() -> {
                    logger.error("Candidate not found with ID: {}", interview.getCandidateId());
                    return new RuntimeException("Candidate not found with ID: " + interview.getCandidateId());
                });

        candidate.setCandidateStatus(Recruiter.CandidateStatus.IN_INTERVIEW);
        candidate.setInterviewStage(interview.getInterviewRound());
        recruiterRepository.save(candidate);
        
        Interview saved = interviewRepository.save(interview);
        logger.info("Interview scheduled successfully with ID: {}", saved.getInterviewId());
        return saved;
    }

    public List<Interview> getAllInterviews() {
        logger.info("Retrieving all interviews");
        return interviewRepository.findAll();
    }

    public Interview getInterviewById(Integer id) {
        logger.info("Retrieving interview by ID: {}", id);
        return interviewRepository.findById(id).orElse(null);
    }

    public List<Interview> getInterviewsByCandidateId(Integer candidateId) {
        logger.info("Retrieving interviews for candidate ID: {}", candidateId);
        return interviewRepository.findByCandidateId(candidateId);
    }

    @Transactional
    public Interview updateInterview(Interview interview) {
        logger.info("Updating interview with ID: {}", interview.getInterviewId());
        Recruiter candidate = recruiterRepository.findById(interview.getCandidateId())
                .orElseThrow(() -> {
                    logger.error("Candidate not found with ID: {}", interview.getCandidateId());
                    return new RuntimeException("Candidate not found with ID: " + interview.getCandidateId());
                });
        switch (interview.getInterviewStatus()) {
            case COMPLETED:
                candidate.setCandidateStatus(Recruiter.CandidateStatus.IN_INTERVIEW);
                candidate.setInterviewStage(interview.getInterviewRound() + " - Completed");
                logger.info("Interview marked as COMPLETED for candidate ID: {}", interview.getCandidateId());
                break;
            case CANCELLED:
            case NO_SHOW:
                candidate.setCandidateStatus(Recruiter.CandidateStatus.REJECTED);
                candidate.setInterviewStage(interview.getInterviewRound() + " - " + interview.getInterviewStatus().name());
                logger.warn("Interview {} for candidate ID: {} - candidate rejected", interview.getInterviewStatus(), interview.getCandidateId());
                break;
            case SCHEDULED:
            case RESCHEDULED:
                candidate.setCandidateStatus(Recruiter.CandidateStatus.IN_INTERVIEW);
                candidate.setInterviewStage(interview.getInterviewRound());
                logger.info("Interview {} for candidate ID: {}", interview.getInterviewStatus(), interview.getCandidateId());
                break;
        }
        recruiterRepository.save(candidate);

        Interview updated = interviewRepository.save(interview);
        logger.info("Interview updated successfully with ID: {}", updated.getInterviewId());
        return updated;
    }

    public void deleteInterview(Integer id) {
        logger.info("Deleting interview with ID: {}", id);
        interviewRepository.deleteById(id);
    }

    public long getInterviewCount() {
        logger.info("Getting interview count");
        return interviewRepository.count();
    }
}
