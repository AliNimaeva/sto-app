package com.example.sto_app.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateOrderItemRequest {

    @NotNull(message = "Количество не может быть пустым")
    @Min(value = 1, message = "Количество должно быть не меньше 1")
    private Integer quantity;

    public UpdateOrderItemRequest() {
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}