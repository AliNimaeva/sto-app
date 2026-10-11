package com.example.sto_app.controller;

import com.example.sto_app.dto.*;
import com.example.sto_app.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.example.sto_app.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return orderService.createPublicOrder(request);
    }

    @GetMapping
    public Page<OrderResponse> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return orderService.getOrders(status, pageable);
    }

    @GetMapping("/my")
    public List<OrderResponse> getMyOrders(
            Authentication authentication
    ) {
        return orderService.getMyOrders(authentication.getName());
    }

    @GetMapping("/number/{number}")
    public OrderResponse getOrderByNumber(
            @PathVariable String number
    ) {
        return orderService.getByNumber(number);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return orderService.getById(
                id,
                authentication.getName()
        );
    }

    @PostMapping("/{id}/items")
    public OrderResponse addItem(
            @PathVariable Long id,
            @Valid @RequestBody OrderItemRequest request
    ) {
        return orderService.addItem(id, request);
    }

    @PutMapping("/{id}/assign")
    public OrderResponse assignMaster(
            @PathVariable Long id,
            @Valid @RequestBody AssignMasterRequest request
    ) {
        return orderService.assignMaster(id, request.getMasterId());
    }

    @PutMapping("/{id}/status")
    public OrderResponse changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeOrderStatusRequest request,
            Authentication authentication
    ) {
        return orderService.changeStatus(
                id,
                request.getStatus(),
                authentication.getName()
        );
    }

    @PutMapping("/{id}/pay")
    public OrderResponse payOrder(@PathVariable Long id) {
        return orderService.payOrder(id);
    }

    @PutMapping("/{id}/close")
    public OrderResponse closeOrder(@PathVariable Long id) {
        return orderService.closeOrder(id);
    }

    @PutMapping("/{id}/diagnostics")
    public OrderResponse updateDiagnostics(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDiagnosticsRequest request,
            Authentication authentication
    ) {
        return orderService.updateDiagnostics(
                id,
                request.getDiagnosticsDescription(),
                authentication.getName()
        );
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public OrderResponse removeItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            Authentication authentication
    ) {
        return orderService.removeItem(
                orderId,
                itemId,
                authentication.getName()
        );
    }

    @PutMapping("/{orderId}/items/{itemId}")
    public OrderResponse updateItemQuantity(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateOrderItemRequest request,
            Authentication authentication
    ) {
        return orderService.updateItemQuantity(
                orderId,
                itemId,
                request.getQuantity(),
                authentication.getName()
        );
    }
}