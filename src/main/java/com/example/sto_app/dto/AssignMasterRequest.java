package com.example.sto_app.dto;

import jakarta.validation.constraints.NotNull;

public class AssignMasterRequest {

    @NotNull
    private Long masterId;

    public AssignMasterRequest() {
    }

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }
}