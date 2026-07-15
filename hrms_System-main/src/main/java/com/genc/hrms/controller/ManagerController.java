package com.genc.hrms.controller;

import com.genc.hrms.model.Attendance;
import com.genc.hrms.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {
    @Autowired
    private ManagerService managerService;

    @PutMapping("/approve/{leaveId}")
    public void approveLeaveRequest(@PathVariable long leaveId) {
        managerService.approveLeaveRequest(leaveId);
    }

    @PutMapping("/reject/{leaveId}")
    public void rejectLeaveRequest(@PathVariable long leaveId) {
        managerService.rejectLeaveRequest(leaveId);
    }

    @GetMapping("/all-leaves")
    public List<Attendance> allLeaves() {
        return managerService.returnApplied();
    }
}
