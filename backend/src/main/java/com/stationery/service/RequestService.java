package com.stationery.service;

import com.stationery.dto.RequestDTO;
import com.stationery.dto.RequestItemDTO;
import com.stationery.dto.ItemCostReport;
import com.stationery.entity.*;
import com.stationery.repository.ItemRepository;
import com.stationery.repository.RequestRepository;
import com.stationery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RequestService {

    @Autowired private RequestRepository requestRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EmailService emailService;

    // --- Module 2 & 3: Eligibility & Viewing ---

    // Req 3: Calculate remaining purchase limit
    public Double calculateRemainingEligibility(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found for eligibility check."));
        
        Double usedAmount = requestRepository.calculateUsedAmount(userId);
        return user.getMaxPurchaseLimit() - usedAmount;
    }
    
    // Req 5: Get requests made by the user
    public List<Request> findRequestsByRequester(Long userId) {
        return requestRepository.findByRequesterId(userId);
    }
    
    // Req 8: Get requests submitted to a manager (approver)
    public List<Request> findRequestsBySuperiorEmail(String superiorEmail) {
        return requestRepository.findBySuperiorEmail(superiorEmail);
    }

    // --- Module 3: Request Submission & Cancellation ---

    // Req 4: Create Request with Eligibility Check
    public Request createRequest(RequestDTO dto) {
        User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("Requester not found."));
        
        double currentRequestCost = 0.0;
        
        // 1. Pre-calculate Cost and check item existence
        for (RequestItemDTO itemDto : dto.getItems()) {
            Item item = itemRepository.findById(itemDto.getItemId()).orElseThrow(() -> new RuntimeException("Item not found: " + itemDto.getItemId()));
            currentRequestCost += item.getUnitPrice() * itemDto.getQuantity();
        }

        // 2. Check Eligibility (Req 3)
        Double remainingLimit = calculateRemainingEligibility(user.getId());
        if (currentRequestCost > remainingLimit) {
            throw new RuntimeException("Request cost ($" + currentRequestCost + ") exceeds eligibility limit ($" + remainingLimit + ").");
        }

        // 3. Build and Save Request
        Request request = new Request();
        request.setRequester(user);
        request.setSuperiorEmail(dto.getSuperiorEmail());
        request.setStatus(RequestStatus.PENDING);
        request.setTotalCost(currentRequestCost);
        
        // 4. Create Details and link them
        List<RequestDetail> detailEntities = new ArrayList<>();
        Request finalRequest = request; // Required for lambda
        dto.getItems().forEach(itemDto -> {
            Item item = itemRepository.findById(itemDto.getItemId()).get(); 
            detailEntities.add(RequestDetail.builder()
                .item(item)
                .quantity(itemDto.getQuantity())
                .request(finalRequest)
                .build());
        });
        request.setRequestDetails(detailEntities);
        
        Request saved = requestRepository.save(request);

        // Req 11: Email Trigger
        emailService.sendNotification(user.getEmail(), "Request Submitted (PENDING)", "Your request ID " + saved.getId() + " is awaiting approval.");
        emailService.sendNotification(dto.getSuperiorEmail(), "New Request for Approval", "Request ID " + saved.getId() + " requires your action.");

        return saved;
    }

    // Req 6: Withdraw (PENDING) or Request Cancellation (APPROVED)
    public void userCancelRequest(Long requestId) {
        Request request = requestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request not found."));

        if (request.getStatus() == RequestStatus.PENDING) {
            // Withdraw immediately
            request.setStatus(RequestStatus.WITHDRAWN);
            emailService.sendNotification(request.getRequester().getEmail(), "Request Withdrawn", "Your request ID " + requestId + " has been withdrawn.");
        } else if (request.getStatus() == RequestStatus.APPROVED) {
            // Request cancellation (requires manager approval)
            request.setStatus(RequestStatus.CANCEL_REQUESTED);
            emailService.sendNotification(request.getSuperiorEmail(), "Cancellation Request", "Request ID " + requestId + " needs approval for cancellation.");
        } else {
            throw new IllegalStateException("Cannot cancel request with status: " + request.getStatus());
        }
        requestRepository.save(request);
    }

    public Request processRequest(Long requestId, String action) {
        Request request = requestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request not found."));
        
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request is not PENDING. Current status: " + request.getStatus());
        }

        if ("APPROVE".equalsIgnoreCase(action)) {
            // 1. Stock Check (Req 2)
            for (RequestDetail detail : request.getRequestDetails()) {
                Item item = detail.getItem();
                if (item.getStockQuantity() < detail.getQuantity()) {
                    throw new RuntimeException("Approval failed: Insufficient Stock for item: " + item.getItemName());
                }
            }

            // 2. Deduct Stock
            for (RequestDetail detail : request.getRequestDetails()) {
                Item item = detail.getItem();
                item.setStockQuantity(item.getStockQuantity() - detail.getQuantity());
                itemRepository.save(item);
            }
            request.setStatus(RequestStatus.APPROVED);
        } else if ("REJECT".equalsIgnoreCase(action)) {
            request.setStatus(RequestStatus.REJECTED);
        } else {
            throw new IllegalArgumentException("Invalid action: " + action);
        }

        Request saved = requestRepository.save(request);
        
        // Req 11: Email Notification
        emailService.sendNotification(request.getRequester().getEmail(), 
                                      "Request Status Update", 
                                      "Your request ID " + saved.getId() + " was " + saved.getStatus() + " by your superior.");
        return saved;
    }

    // Req 9 (Extension): Manager Approves Cancellation (Restocking)
    public void approveCancellation(Long requestId) {
        Request request = requestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request not found."));
            
        if (request.getStatus() != RequestStatus.CANCEL_REQUESTED) {
            throw new IllegalStateException("Request is not requesting cancellation.");
        }
        
        // Restocking the items
        for (RequestDetail detail : request.getRequestDetails()) {
            Item item = detail.getItem();
            item.setStockQuantity(item.getStockQuantity() + detail.getQuantity());
            itemRepository.save(item);
        }
        
        request.setStatus(RequestStatus.CANCELLED);
        requestRepository.save(request);
        
        // Req 11: Email Notification
        emailService.sendNotification(request.getRequester().getEmail(), 
                                      "Cancellation Approved", 
                                      "Your request ID " + requestId + " has been CANCELLED and items restocked.");
    }

    // --- Module 7: Reports ---
    
    // Req 12: Item-wise cost report
    public List<ItemCostReport> generateItemCostReport() {
        return requestRepository.generateItemCostReport();
    }
}