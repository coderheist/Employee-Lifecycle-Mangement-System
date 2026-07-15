package com.genc.hrms.repository;


import com.genc.hrms.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Integer> {
    List<Offer> findByCandidateId(Integer candidateId);
    Optional<Offer> findFirstByCandidateIdOrderByOfferDateDesc(Integer candidateId);
}

