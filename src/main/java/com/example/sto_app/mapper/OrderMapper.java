package com.example.sto_app.mapper;

import com.example.sto_app.dto.OrderResponse;
import com.example.sto_app.model.Order;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    public OrderMapper(OrderItemMapper orderItemMapper) {
        this.orderItemMapper = orderItemMapper;
    }

    public OrderResponse toResponse(Order order) {
        if (order == null) {
            return null;
        }

        OrderResponse response = new OrderResponse();

        response.setId(order.getId());
        response.setNumber(order.getNumber());

        if (order.getCar() != null) {
            response.setCarId(order.getCar().getId());
            response.setBrand(order.getCar().getBrand());
            response.setModel(order.getCar().getModel());
            response.setYear(order.getCar().getYear());
            response.setPlateNumber(order.getCar().getPlateNumber());
            response.setClientName(order.getCar().getClientName());
            response.setClientPhone(order.getCar().getClientPhone());
        }

        response.setProblemDescription(order.getProblemDescription());
        response.setDiagnosticsDescription(order.getDiagnosticsDescription());

        if (order.getStatus() != null) {
            response.setStatus(order.getStatus().name());
        }

        response.setCreatedAt(order.getCreatedAt());

        if (order.getMaster() != null) {
            response.setMasterId(order.getMaster().getId());
            response.setMasterName(order.getMaster().getName());
        }

        response.setTotalCost(order.getTotalCost());
        response.setPaid(order.isPaid());

        response.setItems(
                order.getItems()
                        .stream()
                        .map(orderItemMapper::toResponse)
                        .collect(Collectors.toList())
        );

        return response;
    }
}