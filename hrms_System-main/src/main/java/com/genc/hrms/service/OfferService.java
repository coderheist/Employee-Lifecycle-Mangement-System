package com.genc.hrms.service;

import com.genc.hrms.model.Interview;
import com.genc.hrms.model.Offer;
import com.genc.hrms.model.Recruiter;

import com.genc.hrms.repository.InterviewRepository;
import com.genc.hrms.repository.OfferRepository;
import com.genc.hrms.repository.RecruiterRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OfferService {

    private final Logger logger = LoggerFactory.getLogger(OfferService.class);

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private RecruiterRepository recruiterRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Transactional
    public Offer rolloutOffer(Offer offer) {
        logger.info("Rolling out offer for candidate ID: {}", offer.getCandidateId());
        Recruiter candidate = recruiterRepository.findById(offer.getCandidateId())
                .orElseThrow(() -> {
                    logger.warn("Candidate not found with ID: {}", offer.getCandidateId());
                    return new RuntimeException("Candidate not found with ID: " + offer.getCandidateId());
                });

        List<Interview> interviews = interviewRepository.findByCandidateId(offer.getCandidateId());
        boolean hasCompletedInterview = interviews.stream()
                .anyMatch(i -> i.getInterviewStatus() == Interview.InterviewStatus.COMPLETED);
        if (!hasCompletedInterview) {
            logger.warn("Cannot rollout offer: candidate ID {} has not completed any interview", offer.getCandidateId());
            throw new RuntimeException("Cannot rollout offer: candidate has not completed any interview");
        }

        candidate.setCandidateStatus(Recruiter.CandidateStatus.OFFERED);
        candidate.setInterviewStage("Offer Rolled Out");
        recruiterRepository.save(candidate);

        Offer saved = offerRepository.save(offer);
        logger.info("Offer rolled out successfully with ID: {}", saved.getOfferId());
        return saved;
    }

    public List<Offer> getAllOffers() {
        logger.info("Retrieving all offers");
        return offerRepository.findAll();
    }

    public Offer getOfferById(Integer id) {
        logger.info("Retrieving offer by ID: {}", id);
        return offerRepository.findById(id).orElse(null);
    }


    public Offer updateOffer(Offer offer) {
        logger.info("Updating offer with ID: {}", offer.getOfferId());
        return offerRepository.save(offer);
    }

    public void deleteOffer(Integer id) {
        logger.info("Deleting offer with ID: {}", id);
        offerRepository.deleteById(id);
    }

    public long getOfferCount() {
        logger.info("Getting offer count");
        return offerRepository.count();
    }

    @Transactional
    public Offer updateOfferStatus(Integer offerId, Offer.OfferStatus newStatus) {
        logger.info("Updating offer status for ID: {} to {}", offerId, newStatus);
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> {
                    logger.error("Offer not found with ID: {}", offerId);
                    return new RuntimeException("Offer not found with ID: " + offerId);
                });
        offer.setOfferStatus(newStatus);
        if (newStatus == Offer.OfferStatus.ACCEPTED) {
            logger.info("Offer accepted - updating candidate status to HIRED for candidate ID: {}", offer.getCandidateId());
            Recruiter candidate = recruiterRepository.findById(offer.getCandidateId())
                    .orElseThrow(() -> {
                        logger.error("Candidate not found for offer ID: {}", offerId);
                        return new RuntimeException("Candidate not found");
                    });
            candidate.setCandidateStatus(Recruiter.CandidateStatus.HIRED);
            candidate.setInterviewStage("Hired");
            recruiterRepository.save(candidate);
        }
        Offer updated = offerRepository.save(offer);
        logger.info("Offer status updated successfully for ID: {}", offerId);
        return updated;
    }

    
}
