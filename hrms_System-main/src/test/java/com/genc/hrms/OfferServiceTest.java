package com.genc.hrms;

import com.genc.hrms.model.Interview;
import com.genc.hrms.model.Offer;
import com.genc.hrms.model.Recruiter;
import com.genc.hrms.repository.InterviewRepository;
import com.genc.hrms.repository.OfferRepository;
import com.genc.hrms.repository.RecruiterRepository;
import com.genc.hrms.service.OfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;
    @Mock
    private RecruiterRepository recruiterRepository;
    @Mock
    private InterviewRepository interviewRepository;
    @InjectMocks
    private OfferService offerService;
    private Recruiter candidate;
    private Offer offer;
    private Interview completedInterview;
    @BeforeEach
    void setUp() {
        candidate = new Recruiter();
        candidate.setCandidateId(1);
        candidate.setFullName("John Doe");
        candidate.setAppliedRole("Java Developer");
        candidate.setExperienceYears(5);
        candidate.setInterviewStage("Technical");
        candidate.setCandidateStatus(Recruiter.CandidateStatus.IN_INTERVIEW);

        offer = new Offer();
        offer.setOfferId(1);
        offer.setCandidateId(1);
        offer.setPositionOffered("Senior Java Developer");
        offer.setDepartment("Engineering");
        offer.setSalaryOffered(new BigDecimal("1200000.00"));
        offer.setOfferDate(LocalDate.now());
        offer.setJoiningDate(LocalDate.now().plusDays(15));
        offer.setOfferStatus(Offer.OfferStatus.DRAFTED);
        offer.setAdditionalBenefits("Health Insurance");
        offer.setRemarks("Excellent candidate");

        completedInterview = new Interview();
        completedInterview.setInterviewId(1);
        completedInterview.setCandidateId(1);
        completedInterview.setInterviewStatus(Interview.InterviewStatus.COMPLETED);
    }
    @Test
    void rolloutOfferSuccessfullyWhenCandidateHasCompletedInterview() {

        when(recruiterRepository.findById(1)).thenReturn(Optional.of(candidate));
        when(interviewRepository.findByCandidateId(1)).thenReturn(List.of(completedInterview));
        when(recruiterRepository.save(any(Recruiter.class))).thenReturn(candidate);
        when(offerRepository.save(any(Offer.class))).thenReturn(offer);
        Offer result = offerService.rolloutOffer(offer);
        assertNotNull(result);
        assertEquals(1,result.getOfferId());
        verify(offerRepository).save(offer);
    }
    @Test
    void rolloutOfferThrowsExceptionWhenCandidateHasNoInterviews() {
        when(recruiterRepository.findById(1)).thenReturn(Optional.of(candidate));
        when(interviewRepository.findByCandidateId(1)).thenReturn(List.of());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> offerService.rolloutOffer(offer));
        assertTrue(ex.getMessage().contains("has not completed any interview"));
    }
}
