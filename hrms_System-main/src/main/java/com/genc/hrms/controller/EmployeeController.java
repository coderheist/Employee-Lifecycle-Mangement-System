package com.genc.hrms.controller;
import com.genc.hrms.model.Employee;
import com.genc.hrms.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {



    @Autowired
    private final EmployeeService employeeService;



    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }



    @GetMapping
    public List<Employee> getAllEmployees() {
        log.info("Fetching all employees");
        return employeeService.getAllEmployees();
    }


    @GetMapping("/{id}")
    public Employee getEmployeeDetails(@PathVariable Long id) {
        log.info("Fetching details for employee ID: {}", id);
        return employeeService.getEmployeeById(id);
    }



    @PostMapping
    public Employee createEmployee(@Valid @RequestBody Employee employee) {
        log.info("Received request to create employee: {}", employee.getName());
        Employee created = employeeService.createEmployee(employee);
        log.info("Employee created successfully with ID: {}", created.getEmployeeId());
        return created;
    }



    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        log.info("Deleting employee with ID: {}", id);
        employeeService.deleteEmployee(id);
    }



    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable Long id,
                                   @Valid @RequestBody Employee employee) {
        log.info("Updating employee with ID: {}", id);
        Employee updated = employeeService.updateEmployee(id, employee);
        log.info("Employee updated successfully with ID: {}", updated.getEmployeeId());
        return updated;
    }



    @PutMapping("/{id}/manager/{managerId}")
    public Employee assignManager(@PathVariable Long id,
                                  @PathVariable Long managerId) {
        log.info("Assigning manager with ID {} to employee ID {}", managerId, id);
        Employee updated = employeeService.assignManager(id, managerId);
        log.info("Manager assigned successfully to employee ID {}", id);
        return updated;
    }



    @GetMapping("/{id}/getId")
    public Long getId(@PathVariable Long id){
        return employeeService.getId(id);
    }



    @GetMapping("/{id}/getName")
    public String getName(@PathVariable Long id){
        return employeeService.getName(id);
    }



    @GetMapping("/{id}/getRole")
    public String getRole(@PathVariable Long id){
        return employeeService.getRole(id);
    }



    @GetMapping("/{id}/getDepartment")
    public String getDepartment(@PathVariable Long id){
        return employeeService.getDepartment(id);
    }



    @GetMapping("/{id}/getSalary")
    public Double getSalary(@PathVariable Long id){
        return employeeService.getSalary(id);
    }



    @GetMapping("/{id}/getHireDate")
    public LocalDate getHireDate(@PathVariable Long id){
        return employeeService.getHireDate(id);
    }



    @GetMapping("/{id}/getDesignation")
    public String getDesignation(@PathVariable Long id){
        return employeeService.getDesignation(id);
    }



    @GetMapping("/{id}/getStatus")
    public String getStatus(@PathVariable Long id){
        return employeeService.getStatus(id);
    }
}


