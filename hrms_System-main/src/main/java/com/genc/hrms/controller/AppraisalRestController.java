package com.genc.hrms.controller;

import com.genc.hrms.dto.AppraisalDto;
import com.genc.hrms.model.AppraisalRecord;
import com.genc.hrms.service.AppraisalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/appraisal")
 class AppraisalRestController {

    private final AppraisalService appraisalService;

    public AppraisalRestController(AppraisalService appraisalService) {
        this.appraisalService = appraisalService;
    }

    @GetMapping
    public ResponseEntity<List<AppraisalRecord>> getAll() {
        return ResponseEntity.ok(appraisalService.getAllRecords());
    }

    @GetMapping("/{appraisalId}")
    public ResponseEntity<AppraisalRecord> getById(@PathVariable Long appraisalId) {
        return ResponseEntity.ok(appraisalService.getById(appraisalId));
    }

    @PostMapping("/initialize")
    public ResponseEntity<AppraisalRecord> initialize(@RequestBody AppraisalDto req) {
        return ResponseEntity.ok(appraisalService.initializeGoals(req.getEmployeeId(), req.getAppraisalCycle()));
    }

    @PutMapping("/{appraisalId}/self-review")
    public ResponseEntity<AppraisalRecord> submitSelfReview(@PathVariable Long appraisalId, @RequestBody AppraisalDto req) {
        return ResponseEntity.ok(appraisalService.submitSelfReview(appraisalId, req.getGoalsAchieved()));
    }

    @PutMapping("/{appraisalId}/manager-review")
    public ResponseEntity<AppraisalRecord> submitManagerReview(@PathVariable Long appraisalId, @RequestBody AppraisalDto req) {
        return ResponseEntity.ok(appraisalService.submitManagerReview(appraisalId, req.getOverallRating()));
    }

    @PutMapping("/{appraisalId}/publish")
    public ResponseEntity<AppraisalRecord> publishRating(@PathVariable Long appraisalId) {
        return ResponseEntity.ok(appraisalService.publishFinalRating(appraisalId));
    }
}