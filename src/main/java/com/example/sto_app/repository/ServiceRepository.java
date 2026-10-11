package com.example.sto_app.repository;

import com.example.sto_app.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    boolean existsByName(String name);
}