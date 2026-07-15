package com.genc.hrms.service;

import com.genc.hrms.model.Attendance;
import com.genc.hrms.model.Employee;
import com.genc.hrms.model.Payroll;
import com.genc.hrms.repository.AttendanceRepository;
import com.genc.hrms.repository.EmployeeRepository;
import com.genc.hrms.repository.PayrollRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PayrollService {
        @Autowired
        private EmployeeRepository employeeRepository;

        @Autowired
        private PayrollRepository payrollRepository;

        @Autowired
        private AttendanceRepository attendanceRepository;

        public Map<String, Object> getFormData() {
            Map<String, Object> response = new HashMap<>();
            response.put("statuses", Payroll.PayrollStatus.values());
            response.put("employees", employeeRepository.findAll());
            return response;
        }

        public List<Payroll> getPayrolls(String month) {
            if (month == null || month.trim().isEmpty() || "ALL".equalsIgnoreCase(month)) {
                return payrollRepository.findAll();
            } else {
                return payrollRepository.findByPayPeriod(month);
            }
        }

        public Map<String, Object> getDashboardData(String month) {
            List<Payroll> payrolls = getPayrolls(month);

            double totalGross = 0;
            double totalNetPay = 0;
            int paidCount = 0;
            int pendingCount = 0;

            for (Payroll p : payrolls) {
                if (p.getGrossSalary() != null) totalGross += p.getGrossSalary();
                if (p.getNetSalary() != null) totalNetPay += p.getNetSalary();

                if (p.getPayrollStatus() != null && "PAID".equalsIgnoreCase(p.getPayrollStatus().name())) {
                    paidCount++;
                } else {
                    pendingCount++;
                }
            }

            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("selectedMonth", (month == null || month.isEmpty()) ? "ALL" : month);
            dashboardData.put("totalGross", totalGross);
            dashboardData.put("totalNetPay", totalNetPay);
            dashboardData.put("paidCount", paidCount);
            dashboardData.put("pendingCount", pendingCount);
            dashboardData.put("payrolls", payrolls);

            return dashboardData;
        }

        @Transactional
        public void processAndSavePayroll(Payroll payroll) {
            if (payroll.getEmployee() == null || payroll.getEmployee().getEmployeeId() == null || payroll.getEmployee().getEmployeeId() <= 0) {
                throw new IllegalStateException("Employee ID is missing or invalid.");
            }
            if (payroll.getPayPeriod() == null || payroll.getPayPeriod().trim().isEmpty()) {
                throw new IllegalStateException("Pay period (month) is missing.");
            }

            Employee realEmployee = employeeRepository.findById(payroll.getEmployee().getEmployeeId())
                    .orElseThrow(() -> new IllegalStateException("Employee not found in database."));

            boolean alreadyExists = payrollRepository.existsByEmployee_EmployeeIdAndPayPeriod(
                    realEmployee.getEmployeeId(), payroll.getPayPeriod());

            if (alreadyExists) {
                throw new IllegalStateException("Payroll already generated for Employee ID " + realEmployee.getEmployeeId() + " for month: " + payroll.getPayPeriod());
            }

            double deductions = this.deductions(payroll, realEmployee.getEmployeeId());

            payroll.setEmployee(realEmployee);
            payroll.setGrossSalary(realEmployee.getSalary());
            payroll.setTotalDeductions(deductions);
            payroll.setNetSalary(realEmployee.getSalary() - deductions);


            payrollRepository.save(payroll);
        }
        public Map<String, Double> computeStatutoryDeductions(Payroll payroll) {
            Employee realEmployee = employeeRepository.findById(payroll.getEmployee().getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            double deductions = this.deductions(payroll, realEmployee.getEmployeeId());
            double grossSalary = realEmployee.getSalary();
            double netSalary = grossSalary - deductions;

            Map<String, Double> response = new HashMap<>();
            response.put("deduct", deductions);
            response.put("grossSalary", grossSalary);
            response.put("netSalary", netSalary);

            return response;
        }

        public double deductions(Payroll payroll, long id) {
            Employee employee = employeeRepository.findByEmployeeId(id);
            if (employee == null) {
                throw new IllegalArgumentException("Invalid employee ID: " + id);
            }

            double gross = employee.getSalary();
            double totalDaysInMonth = 30.0;
            double dailyWage = gross / totalDaysInMonth;

            List<Attendance> acceptedLeaves = attendanceRepository.findByEmployee_EmployeeIdAndStatus(id, Attendance.LeaveStatus.APPROVED);
            long unpaidDays = 0;

            for (Attendance leave : acceptedLeaves) {
                if (Attendance.Leave.CASUAL == leave.getLeaveType() || Attendance.Leave.SICK == leave.getLeaveType()) {
                    long daysAbsent = ChronoUnit.DAYS.between(leave.getFromDate(), leave.getToDate()) + 1;
                    unpaidDays += daysAbsent;
                }
            }

            double leaveDeduction = unpaidDays * dailyWage;

            double pf = gross * 0.12;
            double tds = gross * 0.05;
            double esi = gross * 0.03;

            return pf + tds + esi + leaveDeduction;
        }
        public Payroll generateValidPayslip(Long employeeId) {
            Payroll payroll = getLatestPayrollByEmployeeId(employeeId);
            if (payroll != null && payroll.getPayrollStatus() == Payroll.PayrollStatus.PAID) {
                return payroll;
            }
            return null;
        }

        public boolean markAsPaid(long payrollId) {
            Payroll payroll = payrollRepository.findById(payrollId).orElse(null);
            if (payroll != null) {
                payroll.setPayrollStatus(Payroll.PayrollStatus.PAID);
                payrollRepository.save(payroll);
                return true;
            }
            return false;
        }
        public List<Payroll> getData() {
            return payrollRepository.findAll();
        }

        public Payroll getLatestPayrollByEmployeeId(Long employeeId) {
            return payrollRepository.findTopByEmployee_EmployeeIdOrderByPayrollIdDesc(employeeId);
        }

        public void savePayroll(Payroll payroll) {
            payrollRepository.save(payroll);
        }

        public List<Payroll> getPayrollsByMonth(String payPeriod) {
            return payrollRepository.findByPayPeriod(payPeriod);
        }

}
