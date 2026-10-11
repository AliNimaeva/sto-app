package com.example.sto_app.mapper;

import com.example.sto_app.dto.AdditionalWorkResponse;
import com.example.sto_app.model.AdditionalWork;
import org.springframework.stereotype.Component;

@Component
public class AdditionalWorkMapper {

    public AdditionalWorkResponse toResponse(AdditionalWork work) {
        if (work == null) {
            return null;
        }

        AdditionalWorkResponse response = new AdditionalWorkResponse();

        response.setId(work.getId());

        if (work.getOrder() != null) {
            response.setOrderId(work.getOrder().getId());
        }

        response.setDescription(work.getDescription());
        response.setPrice(work.getPrice());

        if (work.getStatus() != null) {
            response.setStatus(work.getStatus().name());
        }

        response.setCreatedAt(work.getCreatedAt());

        if (work.getCreatedBy() != null) {
            response.setCreatedById(work.getCreatedBy().getId());
            response.setCreatedByName(work.getCreatedBy().getName());
        }

        return response;
    }
}