package com.stationery.controller;

import com.stationery.dto.RequestDTO;
import com.stationery.entity.Item;
import com.stationery.entity.Request;
import com.stationery.service.RequestService;
import com.stationery.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RequestController {

    @Autowired
    private RequestService requestService;
    
    @Autowired
    private ItemService itemService; // Assuming you have an ItemService

    // GET /api/items (Module 2: Stationery Availability)
    @GetMapping("/items")
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.findAll());
    }

    // GET /api/users/{id}/eligibility (Module 2: Eligibility Check)
    @GetMapping("/users/{userId}/eligibility")
    public ResponseEntity<Double> getEligibility(@PathVariable Long userId) {
        Double remainingLimit = requestService.calculateRemainingEligibility(userId);
        return ResponseEntity.ok(remainingLimit);
    }

    // POST /api/requests (Module 3: Create Stationery Request)
    @PostMapping("/requests")
    public ResponseEntity<Request> createRequest(@RequestBody RequestDTO requestDto) {
        Request newRequest = requestService.createRequest(requestDto);
        return ResponseEntity.status(201).body(newRequest);
    }
    
    // GET /api/requests/my (Module 5: View My Requests)
    // NOTE: User ID should come from the authenticated token, not the path
    @GetMapping("/requests/my/{userId}")
    public ResponseEntity<List<Request>> getMyRequests(@PathVariable Long userId) {
        List<Request> requests = requestService.findRequestsByRequester(userId);
        return ResponseEntity.ok(requests);
    }

    // DELETE /api/requests/{id} (Module 3: Withdraw PENDING request)
    @DeleteMapping("/requests/{requestId}")
    public ResponseEntity<Void> withdrawRequest(@PathVariable Long requestId) {
        requestService.userCancelRequest(requestId); // Logic handles PENDING->WITHDRAWN
        return ResponseEntity.noContent().build();
    }

    // PUT /api/requests/{id}/cancel (Module 3: Request Cancellation for APPROVED request)
    @PutMapping("/requests/{requestId}/cancel")
    public ResponseEntity<Request> requestCancellation(@PathVariable Long requestId) {
        // Logic handles APPROVED->CANCEL_REQUESTED
        requestService.userCancelRequest(requestId); 
        return ResponseEntity.ok().build();
    }
}