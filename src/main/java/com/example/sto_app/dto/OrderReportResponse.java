package com.example.sto_app.dto;

import java.math.BigDecimal;
import java.util.Map;

public class OrderReportResponse {

    private long totalOrders;
    private long newOrders;
    private long assignedOrders;
    private long agreedOrders;
    private long inProgressOrders;
    private long completedOrders;
    private long closedOrders;
    private long rejectedOrders;

    private long paidOrders;
    private BigDecimal totalRevenue;

    private Map<String, Long> ordersByStatus;

    public OrderReportResponse() {
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getNewOrders() {
        return newOrders;
    }

    public void setNewOrders(long newOrders) {
        this.newOrders = newOrders;
    }

    public long getAssignedOrders() {
        return assignedOrders;
    }

    public void setAssignedOrders(long assignedOrders) {
        this.assignedOrders = assignedOrders;
    }

    public long getAgreedOrders() {
        return agreedOrders;
    }

    public void setAgreedOrders(long agreedOrders) {
        this.agreedOrders = agreedOrders;
    }

    public long getInProgressOrders() {
        return inProgressOrders;
    }

    public void setInProgressOrders(long inProgressOrders) {
        this.inProgressOrders = inProgressOrders;
    }

    public long getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(long completedOrders) {
        this.completedOrders = completedOrders;
    }

    public long getClosedOrders() {
        return closedOrders;
    }

    public void setClosedOrders(long closedOrders) {
        this.closedOrders = closedOrders;
    }

    public long getRejectedOrders() {
        return rejectedOrders;
    }

    public void setRejectedOrders(long rejectedOrders) {
        this.rejectedOrders = rejectedOrders;
    }

    public long getPaidOrders() {
        return paidOrders;
    }

    public void setPaidOrders(long paidOrders) {
        this.paidOrders = paidOrders;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Map<String, Long> getOrdersByStatus() {
        return ordersByStatus;
    }

    public void setOrdersByStatus(Map<String, Long> ordersByStatus) {
        this.ordersByStatus = ordersByStatus;
    }
}