package com.example.sto_app.service;

import com.example.sto_app.dto.AdditionalWorkRequest;
import com.example.sto_app.dto.AdditionalWorkResponse;
import com.example.sto_app.mapper.AdditionalWorkMapper;
import com.example.sto_app.model.AdditionalWork;
import com.example.sto_app.model.AdditionalWorkStatus;
import com.example.sto_app.model.Order;
import com.example.sto_app.model.OrderStatus;
import com.example.sto_app.model.Role;
import com.example.sto_app.model.User;
import com.example.sto_app.repository.AdditionalWorkRepository;
import com.example.sto_app.repository.OrderRepository;
import com.example.sto_app.repository.UserRepository;
import com.example.sto_app.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdditionalWorkService {

    private final AdditionalWorkRepository additionalWorkRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AdditionalWorkMapper additionalWorkMapper;

    public AdditionalWorkService(
            AdditionalWorkRepository additionalWorkRepository,
            OrderRepository orderRepository,
            UserRepository userRepository,
            AdditionalWorkMapper additionalWorkMapper
    ) {
        this.additionalWorkRepository = additionalWorkRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.additionalWorkMapper = additionalWorkMapper;
    }

    @Transactional
    public AdditionalWorkResponse create(
            Long orderId,
            AdditionalWorkRequest request,
            String email
    ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Заказ не найден: " + orderId
                        ));

        User master = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Пользователь не найден: " + email
                        ));

        if (master.getRole() != Role.MASTER) {
            throw new IllegalArgumentException(
                    "Только мастер может создавать дополнительные работы"
            );
        }

        if (order.getMaster() == null
                || !order.getMaster().getId().equals(master.getId())) {
            throw new IllegalArgumentException(
                    "Мастер может добавлять работы только в свой заказ"
            );
        }

        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalArgumentException(
                    "Дополнительную работу можно добавить только для заказа в статусе IN_PROGRESS"
            );
        }

        AdditionalWork work = new AdditionalWork();

        work.setOrder(order);
        work.setDescription(request.getDescription());
        work.setPrice(request.getPrice());
        work.setStatus(AdditionalWorkStatus.PENDING);
        work.setCreatedBy(master);

        AdditionalWork saved = additionalWorkRepository.save(work);

        return additionalWorkMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AdditionalWorkResponse> getByOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException(
                    "Заказ не найден: " + orderId
            );
        }

        return additionalWorkRepository.findByOrderId(orderId)
                .stream()
                .map(additionalWorkMapper::toResponse)
                .toList();
    }

    @Transactional
    public AdditionalWorkResponse approve(
            Long workId,
            boolean approved
    ) {
        AdditionalWork work = additionalWorkRepository.findById(workId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Дополнительная работа не найдена: " + workId
                        ));

        if (work.getStatus() != AdditionalWorkStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Можно согласовать только дополнительную работу со статусом PENDING"
            );
        }

        if (approved) {
            work.setStatus(AdditionalWorkStatus.APPROVED);

            Order order = work.getOrder();

            order.setTotalCost(
                    order.getTotalCost().add(work.getPrice())
            );

            orderRepository.save(order);

        } else {
            work.setStatus(AdditionalWorkStatus.REJECTED);
        }

        AdditionalWork saved = additionalWorkRepository.save(work);

        return additionalWorkMapper.toResponse(saved);
    }
}