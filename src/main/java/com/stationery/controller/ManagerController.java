package com.stationery.controller;

import com.stationery.dto.ActionRequest;
import com.stationery.dto.ItemCostReport;
import com.stationery.entity.Request;
import com.stationery.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    @Autowired
    private RequestService requestService;

    // GET /api/manager/requests (Module 4: Fetch requests pending approval)
    // NOTE: The manager's email should come from the authenticated token, not the path
    @GetMapping("/requests/{managerEmail}")
    public ResponseEntity<List<Request>> getPendingRequests(@PathVariable String managerEmail) {
        // Find requests where the superior_email matches the logged-in manager's email
        List<Request> pendingRequests = requestService.findRequestsBySuperiorEmail(managerEmail);
        return ResponseEntity.ok(pendingRequests);
    }

    // PUT /api/manager/requests/{id}/action (Module 4: Approve/Reject Request)
    @PutMapping("/requests/{requestId}/action")
    public ResponseEntity<Request> processRequestAction(@PathVariable Long requestId, @RequestBody ActionRequest actionRequest) {
        Request updatedRequest = requestService.processRequest(requestId, actionRequest.getAction());
        return ResponseEntity.ok(updatedRequest);
    }
    
    // PUT /api/manager/requests/{id}/cancel-approve (Module 4: Approve Cancellation)
    @PutMapping("/requests/{requestId}/cancel-approve")
    public ResponseEntity<Void> approveCancellation(@PathVariable Long requestId) {
        requestService.approveCancellation(requestId); // Restocks the items
        return ResponseEntity.ok().build();
    }

    // GET /api/manager/reports/item-cost (Module 7: Reporting)
    @GetMapping("/reports/item-cost")
    public ResponseEntity<List<ItemCostReport>> getItemCostReport() {
        List<ItemCostReport> report = requestService.generateItemCostReport();
        return ResponseEntity.ok(report);
    }
}