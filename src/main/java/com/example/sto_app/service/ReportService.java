package com.example.sto_app.service;

import com.example.sto_app.dto.OrderReportResponse;
import com.example.sto_app.model.Order;
import com.example.sto_app.model.OrderStatus;
import com.example.sto_app.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.sto_app.dto.MasterReportResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.time.LocalDateTime;


@Service
public class ReportService {

    private final OrderRepository orderRepository;

    public ReportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public OrderReportResponse getOrderReport() {

        OrderReportResponse response = new OrderReportResponse();

        long totalOrders = orderRepository.count();

        response.setTotalOrders(totalOrders);

        response.setNewOrders(
                orderRepository.countByStatus(OrderStatus.NEW)
        );

        response.setAssignedOrders(
                orderRepository.countByStatus(OrderStatus.ASSIGNED)
        );

        response.setAgreedOrders(
                orderRepository.countByStatus(OrderStatus.AGREED)
        );

        response.setInProgressOrders(
                orderRepository.countByStatus(OrderStatus.IN_PROGRESS)
        );

        response.setCompletedOrders(
                orderRepository.countByStatus(OrderStatus.COMPLETED)
        );

        response.setClosedOrders(
                orderRepository.countByStatus(OrderStatus.CLOSED)
        );

        response.setRejectedOrders(
                orderRepository.countByStatus(
                        OrderStatus.REJECTED_BY_CLIENT
                )
        );

        response.setPaidOrders(
                orderRepository.countByPaidTrue()
        );

        BigDecimal totalRevenue = orderRepository.findAll()
                .stream()
                .filter(Order::isPaid)
                .map(Order::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.setTotalRevenue(totalRevenue);

        Map<String, Long> ordersByStatus = Arrays.stream(OrderStatus.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        status -> orderRepository.countByStatus(status)
                ));

        response.setOrdersByStatus(ordersByStatus);

        return response;
    }

    @Transactional(readOnly = true)
    public OrderReportResponse getOrderReport(
            LocalDate from,
            LocalDate to
    ) {
        if (from == null || to == null) {
            throw new IllegalArgumentException(
                    "Параметры from и to обязательны"
            );
        }

        if (from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "Дата from не может быть позже даты to"
            );
        }

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();

        OrderReportResponse response = new OrderReportResponse();

        response.setTotalOrders(
                orderRepository
                        .countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                                fromDateTime,
                                toDateTime
                        )
        );

        response.setNewOrders(
                countByStatus(
                        OrderStatus.NEW,
                        fromDateTime,
                        toDateTime
                )
        );

        response.setAssignedOrders(
                countByStatus(
                        OrderStatus.ASSIGNED,
                        fromDateTime,
                        toDateTime
                )
        );

        response.setAgreedOrders(
                countByStatus(
                        OrderStatus.AGREED,
                        fromDateTime,
                        toDateTime
                )
        );

        response.setInProgressOrders(
                countByStatus(
                        OrderStatus.IN_PROGRESS,
                        fromDateTime,
                        toDateTime
                )
        );

        response.setCompletedOrders(
                countByStatus(
                        OrderStatus.COMPLETED,
                        fromDateTime,
                        toDateTime
                )
        );

        response.setClosedOrders(
                countByStatus(
                        OrderStatus.CLOSED,
                        fromDateTime,
                        toDateTime
                )
        );

        response.setRejectedOrders(
                countByStatus(
                        OrderStatus.REJECTED_BY_CLIENT,
                        fromDateTime,
                        toDateTime
                )
        );

        response.setPaidOrders(
                orderRepository
                        .countByPaidTrueAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                                fromDateTime,
                                toDateTime
                        )
        );

        BigDecimal totalRevenue = orderRepository
                .findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                        fromDateTime,
                        toDateTime
                )
                .stream()
                .filter(Order::isPaid)
                .map(Order::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.setTotalRevenue(totalRevenue);

        Map<String, Long> ordersByStatus =
                Arrays.stream(OrderStatus.values())
                        .collect(Collectors.toMap(
                                Enum::name,
                                status -> countByStatus(
                                        status,
                                        fromDateTime,
                                        toDateTime
                                )
                        ));

        response.setOrdersByStatus(ordersByStatus);

        return response;
    }

    private long countByStatus(
            OrderStatus status,
            LocalDateTime from,
            LocalDateTime to
    ) {
        return orderRepository
                .countByStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                        status,
                        from,
                        to
                );
    }

    @Transactional(readOnly = true)
    public List<MasterReportResponse> getMasterReport(
            LocalDate from,
            LocalDate to
    ) {
        if (from == null || to == null) {
            throw new IllegalArgumentException(
                    "Параметры from и to обязательны"
            );
        }

        if (from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "Дата from не может быть позже даты to"
            );
        }

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();

        List<Order> orders =
                orderRepository.findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                        fromDateTime,
                        toDateTime
                );

        Map<Long, MasterReportResponse> report = new LinkedHashMap<>();

        for (Order order : orders) {

            if (order.getMaster() == null) {
                continue;
            }

            Long masterId = order.getMaster().getId();

            MasterReportResponse masterReport =
                    report.computeIfAbsent(
                            masterId,
                            id -> new MasterReportResponse(
                                    masterId,
                                    order.getMaster().getName(),
                                    0,
                                    0,
                                    BigDecimal.ZERO
                            )
                    );

            masterReport.setTotalOrders(
                    masterReport.getTotalOrders() + 1
            );

            if (order.getStatus() == OrderStatus.COMPLETED) {
                masterReport.setCompletedOrders(
                        masterReport.getCompletedOrders() + 1
                );
            }

            if (order.isPaid()) {
                masterReport.setTotalRevenue(
                        masterReport.getTotalRevenue()
                                .add(order.getTotalCost())
                );
            }
        }

        return report.values().stream().toList();
    }
}