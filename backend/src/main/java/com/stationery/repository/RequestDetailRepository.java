package com.stationery.repository;

import com.stationery.entity.RequestDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestDetailRepository extends JpaRepository<RequestDetail, Long> {
    // Used primarily by the RequestService for detailed access, if needed
}