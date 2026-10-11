package com.example.sto_app.controller;

import com.example.sto_app.dto.ServiceRequest;
import com.example.sto_app.model.Service;
import com.example.sto_app.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    public List<Service> getAll() {
        return serviceService.getAll();
    }

    @GetMapping("/{id}")
    public Service getById(@PathVariable Long id) {
        return serviceService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Service create(
            @Valid @RequestBody ServiceRequest request
    ) {
        return serviceService.create(request);
    }

    @PutMapping("/{id}")
    public Service update(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequest request
    ) {
        return serviceService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        serviceService.delete(id);
    }
}