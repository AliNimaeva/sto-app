package com.example.sto_app.mapper;

import com.example.sto_app.dto.OrderItemResponse;
import com.example.sto_app.model.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {

    public OrderItemResponse toResponse(OrderItem item) {
        if (item == null) {
            return null;
        }

        OrderItemResponse response = new OrderItemResponse();

        response.setId(item.getId());
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice());

        if (item.getService() != null) {
            response.setServiceId(item.getService().getId());
            response.setServiceName(item.getService().getName());
        }

        if (item.getPart() != null) {
            response.setPartId(item.getPart().getId());
            response.setPartName(item.getPart().getName());
        }

        return response;
    }
}