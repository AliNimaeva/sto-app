package com.example.sto_app.dto;

import com.example.sto_app.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class ChangeOrderStatusRequest {

    @NotNull
    private OrderStatus status;

    public ChangeOrderStatusRequest() {
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}