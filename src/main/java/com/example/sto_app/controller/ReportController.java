package com.example.sto_app.controller;

import com.example.sto_app.dto.OrderReportResponse;
import com.example.sto_app.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.sto_app.dto.MasterReportResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/orders")
    public OrderReportResponse getOrderReport(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return reportService.getOrderReport(from, to);
    }

    @GetMapping("/masters")
    public List<MasterReportResponse> getMasterReport(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return reportService.getMasterReport(from, to);
    }
}