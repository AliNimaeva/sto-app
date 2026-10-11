package com.example.sto_app.dto;

import java.math.BigDecimal;

public class MasterReportResponse {

    private Long masterId;
    private String masterName;

    private long totalOrders;
    private long completedOrders;

    private BigDecimal totalRevenue;

    public MasterReportResponse() {
    }

    public MasterReportResponse(
            Long masterId,
            String masterName,
            long totalOrders,
            long completedOrders,
            BigDecimal totalRevenue
    ) {
        this.masterId = masterId;
        this.masterName = masterName;
        this.totalOrders = totalOrders;
        this.completedOrders = completedOrders;
        this.totalRevenue = totalRevenue;
    }

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(long completedOrders) {
        this.completedOrders = completedOrders;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}