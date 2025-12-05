package com.stationery.repository;

import com.stationery.dto.ItemCostReport;
import com.stationery.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    
    // Req 5: See his/her current stationery applications
    List<Request> findByRequesterId(Long requester_id);
    
    // Req 8: Manager can view all stationery requests submitted to him/her
    List<Request> findBySuperiorEmail(String email);

    // Req 3: Calculate total spent by user on APPROVED requests to check eligibility
    @Query("SELECT COALESCE(SUM(r.totalCost), 0) FROM Request r WHERE r.requester.id = :userId AND r.status = 'APPROVED'")
    Double calculateUsedAmount(@Param("userId") Long userId);

    // Req 12: Report - Cost per Item
    @Query("SELECT i.itemName as itemName, SUM(rd.quantity * i.unitPrice) as totalCost, COUNT(r) as requestCount " +
           "FROM RequestDetail rd " +
           "JOIN rd.item i " +
           "JOIN rd.request r " +
           "WHERE r.status = 'APPROVED' " +
           "GROUP BY i.id")
    List<ItemCostReport> generateItemCostReport();
}