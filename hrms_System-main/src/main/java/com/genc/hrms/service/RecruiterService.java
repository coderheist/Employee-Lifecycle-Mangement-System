package com.genc.hrms.service;

import com.genc.hrms.model.Recruiter;
import com.genc.hrms.repository.RecruiterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecruiterService {

    private final Logger logger = LoggerFactory.getLogger(RecruiterService.class);

    @Autowired
    private RecruiterRepository candidateRepository;
    public Recruiter saveData(Recruiter candidate){
        logger.info("Saving candidate data: {}", candidate.getFullName());
        Recruiter saved = candidateRepository.save(candidate);
        logger.info("Candidate saved with ID: {}", saved.getCandidateId());
        return saved;
    }

    public List<Recruiter> getAllCandidates() {
        logger.info("Retrieving all candidates");
        return candidateRepository.findAll();
    }

    public Recruiter getCandidateById(Integer id) {
        logger.info("Retrieving candidate by ID: {}", id);
        return candidateRepository.findById(id).orElse(null);
    }

    public Recruiter updateCandidate(Recruiter candidate) {
        logger.info("Updating candidate with ID: {}", candidate.getCandidateId());
        return candidateRepository.save(candidate);
    }

    public long getCandidateCount() {
        logger.info("Getting candidate count");
        return candidateRepository.count();
    }
}

