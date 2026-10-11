package com.example.sto_app.service;

import com.example.sto_app.dto.ServiceRequest;
import com.example.sto_app.exception.ResourceNotFoundException;
import com.example.sto_app.model.Service;
import com.example.sto_app.repository.ServiceRepository;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<Service> getAll() {
        return serviceRepository.findAll();
    }

    public Service getById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Услуга не найдена: " + id
                        )
                );
    }

    public Service create(ServiceRequest request) {
        Service service = new Service();

        service.setName(request.getName());
        service.setPrice(request.getPrice());
        service.setDescription(request.getDescription());

        return serviceRepository.save(service);
    }

    public Service update(Long id, ServiceRequest request) {
        Service service = getById(id);

        service.setName(request.getName());
        service.setPrice(request.getPrice());
        service.setDescription(request.getDescription());

        return serviceRepository.save(service);
    }

    public void delete(Long id) {
        Service service = getById(id);
        serviceRepository.delete(service);
    }
}