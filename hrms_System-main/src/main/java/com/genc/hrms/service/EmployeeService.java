package com.genc.hrms.service;

import com.genc.hrms.model.Employee;
import com.genc.hrms.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee createEmployee(Employee employee) {
        log.info("Creating new employee: {}", employee.getName());
        return employeeRepository.save(employee);
    }

    public Employee getEmployeeById(Long id) {
        log.info("Fetching employee with ID: {}", id);
        return employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with ID: {}", id);
                    return new EntityNotFoundException("Employee not found with id " + id);
                });
    }

    public List<Employee> getAllEmployees() {
        log.info("Fetching all employees");
        return employeeRepository.findAll();
    }

    public Employee updateEmployee(Long id, Employee employee) {
        log.info("Updating employee with ID: {}", id);
        Employee existing = getEmployeeById(id);

        existing.setName(employee.getName());
        existing.setDepartment(employee.getDepartment());
        existing.setDesignation(employee.getDesignation());
        existing.setStatus(employee.getStatus());
        existing.setRole(employee.getRole());
        existing.setSalary(employee.getSalary());
        existing.setHireDate(employee.getHireDate());

        Employee updated = employeeRepository.save(existing);
        log.info("Employee updated successfully with ID: {}", updated.getEmployeeId());
        return updated;
    }

    public void deleteEmployee(Long id) {
        log.info("Deleting employee with ID: {}", id);
        Employee existing = getEmployeeById(id);
        employeeRepository.delete(existing);
    }

    public Employee assignManager(Long id, Long managerId) {
        log.info("Assigning manager with ID {} to employee ID {}", managerId, id);
        Employee employee = getEmployeeById(id);
        Employee manager = getEmployeeById(managerId);

        Employee updated = employeeRepository.save(employee);

        log.info("Manager assigned successfully to employee ID {}", id);
        return updated;
    }

    public Long getId(Long id) {
        return getEmployeeById(id).getEmployeeId();
    }

    public String getName(Long id) {
        return getEmployeeById(id).getName();
    }

    public String getRole(Long id) {
        return getEmployeeById(id).getRole();
    }

    public String getDepartment(Long id) {
        return getEmployeeById(id).getDepartment();
    }

    public Double getSalary(Long id) {
        return getEmployeeById(id).getSalary();
    }

    public LocalDate getHireDate(Long id) {
        return getEmployeeById(id).getHireDate();
    }

    public String getDesignation(Long id) {
        return getEmployeeById(id).getDesignation();
    }

    public String getStatus(Long id) {
        return getEmployeeById(id).getStatus();
    }
}

