package com.example.sto_app.repository;

import com.example.sto_app.model.AdditionalWork;
import com.example.sto_app.model.AdditionalWorkStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdditionalWorkRepository
        extends JpaRepository<AdditionalWork, Long> {

    List<AdditionalWork> findByOrderId(Long orderId);

    List<AdditionalWork> findByStatus(AdditionalWorkStatus status);
}