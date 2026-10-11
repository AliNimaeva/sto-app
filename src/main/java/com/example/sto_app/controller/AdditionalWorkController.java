package com.example.sto_app.controller;

import com.example.sto_app.dto.AdditionalWorkRequest;
import com.example.sto_app.dto.AdditionalWorkResponse;
import com.example.sto_app.service.AdditionalWorkService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class AdditionalWorkController {

    private final AdditionalWorkService additionalWorkService;

    public AdditionalWorkController(
            AdditionalWorkService additionalWorkService
    ) {
        this.additionalWorkService = additionalWorkService;
    }

    @PostMapping("/{orderId}/additional-works")
    @ResponseStatus(HttpStatus.CREATED)
    public AdditionalWorkResponse create(
            @PathVariable Long orderId,
            @Valid @RequestBody AdditionalWorkRequest request,
            Authentication authentication
    ) {
        return additionalWorkService.create(
                orderId,
                request,
                authentication.getName()
        );
    }

    @GetMapping("/{orderId}/additional-works")
    public List<AdditionalWorkResponse> getByOrder(
            @PathVariable Long orderId
    ) {
        return additionalWorkService.getByOrder(orderId);
    }

    @PutMapping("/additional-works/{workId}/approval")
    public AdditionalWorkResponse approve(
            @PathVariable Long workId,
            @RequestParam boolean approved
    ) {
        return additionalWorkService.approve(workId, approved);
    }
}