package com.genc.hrms.controller;

import com.genc.hrms.model.Payroll;
import com.genc.hrms.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/payroll")
public class PayrollController {
    @Autowired
    private PayrollService payrollService;
    @GetMapping("/form-data")
    public ResponseEntity<Map<String, Object>> getFormData() {
        return ResponseEntity.ok(payrollService.getFormData());
    }
    @PostMapping("/run")
    public ResponseEntity<?> runPayroll(@RequestBody Payroll payroll) {
        try {
            payrollService.processAndSavePayroll(payroll);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Payroll executed successfully"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    @PostMapping("/deductions")
    public ResponseEntity<Map<String, Double>> computeStatutoryDeductions(@RequestBody Payroll payroll) {
        return ResponseEntity.ok(payrollService.computeStatutoryDeductions(payroll));
    }

    @GetMapping("/payrolls")
    public ResponseEntity<List<Payroll>> getPayrolls(@RequestParam(value = "month", required = false) String month) {
        return ResponseEntity.ok(payrollService.getPayrolls(month));
    }

    @GetMapping("/payslip")
    public ResponseEntity<Payroll> generatePayslip(@RequestParam("employeeId") Long employeeId) {
        Payroll payroll = payrollService.generateValidPayslip(employeeId);
        if (payroll != null) {
            return ResponseEntity.ok(payroll);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/markAsPaid/{payrollId}")
    public ResponseEntity<?> markAsPaid(@PathVariable("payrollId") long payrollId) {
        boolean isUpdated = payrollService.markAsPaid(payrollId);
        if (isUpdated) {
            return ResponseEntity.ok(Map.of("message", "Status updated to PAID"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Payroll not found"));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(@RequestParam(value = "month", required = false) String month) {
        return ResponseEntity.ok(payrollService.getDashboardData(month));
    }
}