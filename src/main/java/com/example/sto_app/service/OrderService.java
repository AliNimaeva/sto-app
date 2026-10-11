package com.example.sto_app.service;

import com.example.sto_app.dto.CreateOrderRequest;
import com.example.sto_app.dto.OrderResponse;
import com.example.sto_app.dto.OrderItemRequest;
import com.example.sto_app.exception.ResourceNotFoundException;
import com.example.sto_app.mapper.OrderMapper;
import com.example.sto_app.model.Car;
import com.example.sto_app.model.Order;
import com.example.sto_app.model.OrderItem;
import com.example.sto_app.model.OrderStatus;
import com.example.sto_app.model.Part;
import com.example.sto_app.model.User;
import com.example.sto_app.model.Role;
import com.example.sto_app.model.Service;
import com.example.sto_app.repository.CarRepository;
import com.example.sto_app.repository.OrderRepository;
import com.example.sto_app.repository.PartRepository;
import com.example.sto_app.repository.ServiceRepository;
import com.example.sto_app.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CarRepository carRepository;
    private final PartRepository partRepository;
    private final ServiceRepository serviceRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;

    public OrderService(
            OrderRepository orderRepository,
            CarRepository carRepository,
            PartRepository partRepository,
            ServiceRepository serviceRepository,
            OrderMapper orderMapper,
            UserRepository userRepository
    ) {
        this.orderRepository = orderRepository;
        this.carRepository = carRepository;
        this.partRepository = partRepository;
        this.serviceRepository = serviceRepository;
        this.orderMapper = orderMapper;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponse createPublicOrder(CreateOrderRequest request) {

        Car car = carRepository.findByPlateNumber(request.getPlateNumber())
                .orElseGet(() -> {
                    Car newCar = new Car();

                    newCar.setBrand(request.getBrand());
                    newCar.setModel(request.getModel());
                    newCar.setYear(request.getYear());
                    newCar.setPlateNumber(request.getPlateNumber());
                    newCar.setClientName(request.getClientName());
                    newCar.setClientPhone(request.getClientPhone());

                    return carRepository.save(newCar);
                });

        Order order = new Order();

        order.setNumber(generateOrderNumber());
        order.setCar(car);
        order.setProblemDescription(request.getProblemDescription());
        order.setStatus(OrderStatus.NEW);
        order.setTotalCost(BigDecimal.ZERO);
        order.setPaid(false);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }

    public OrderResponse getById(Long id, String email) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Заказ не найден: " + id
                        )
                );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Пользователь не найден: " + email
                        )
                );

        // ADMIN и MANAGER могут смотреть любой заказ
        if (user.getRole() == Role.ADMIN ||
                user.getRole() == Role.MANAGER) {

            return orderMapper.toResponse(order);
        }

        // MASTER может смотреть только свой заказ
        if (user.getRole() == Role.MASTER) {

            if (order.getMaster() == null ||
                    !order.getMaster().getId().equals(user.getId())) {

                throw new IllegalArgumentException(
                        "Мастер может просматривать только свои заказы"
                );
            }

            return orderMapper.toResponse(order);
        }

        throw new IllegalArgumentException(
                "Недостаточно прав для просмотра заказа"
        );
    }

    @Transactional
    public OrderResponse updateDiagnostics(
            Long orderId,
            String diagnosticsDescription,
            String email
    ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Заказ не найден: " + orderId
                        )
                );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Пользователь не найден: " + email
                        )
                );

        // ADMIN может изменять диагностику любого заказа
        if (user.getRole() == Role.ADMIN) {
            // доступ разрешён
        }

        // MASTER может изменять диагностику только своего заказа
        else if (user.getRole() == Role.MASTER) {

            if (order.getMaster() == null ||
                    !order.getMaster().getId().equals(user.getId())) {

                throw new IllegalArgumentException(
                        "Мастер может изменять диагностику только своего заказа"
                );
            }

        } else {
            throw new IllegalArgumentException(
                    "Недостаточно прав для изменения диагностики"
            );
        }

        // Диагностику нельзя менять после закрытия или отказа клиента
        if (order.getStatus() == OrderStatus.CLOSED ||
                order.getStatus() == OrderStatus.REJECTED_BY_CLIENT) {

            throw new IllegalArgumentException(
                    "Нельзя изменить диагностику закрытого или отклонённого заказа"
            );
        }

        order.setDiagnosticsDescription(diagnosticsDescription);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }

    public OrderResponse getByNumber(String number) {
        Order order = orderRepository.findByNumber(number)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Заказ с номером " + number + " не найден"
                        )
                );

        return orderMapper.toResponse(order);
    }

    public List<OrderResponse> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    private String generateOrderNumber() {
        return "STO-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }

    @Transactional
    public OrderResponse addItem(Long orderId, OrderItemRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Заказ не найден: " + orderId
                        )
                );

        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalArgumentException(
                    "Добавлять услуги и запчасти можно только для заказа в статусе IN_PROGRESS"
            );
        }

        if (request.getServiceId() != null && request.getPartId() != null) {
            throw new IllegalArgumentException(
                    "Нельзя одновременно указать услугу и запчасть"
            );
        }

        if (request.getServiceId() == null && request.getPartId() == null) {
            throw new IllegalArgumentException(
                    "Необходимо указать услугу или запчасть"
            );
        }

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setQuantity(request.getQuantity());

        if (request.getServiceId() != null) {

            com.example.sto_app.model.Service service =
                    serviceRepository.findById(request.getServiceId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Услуга не найдена: " +
                                                    request.getServiceId()
                                    )
                            );

            item.setService(service);
            item.setPrice(service.getPrice());

        } else {

            Part part = partRepository.findById(request.getPartId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Запчасть не найдена: " +
                                            request.getPartId()
                            )
                    );

            if (part.getQuantity() < request.getQuantity()) {
                throw new IllegalArgumentException(
                        "Недостаточно запчастей на складе"
                );
            }

            item.setPart(part);
            item.setPrice(part.getPrice());

            part.setQuantity(
                    part.getQuantity() - request.getQuantity()
            );

            partRepository.save(part);
        }

        order.getItems().add(item);

        recalculateTotal(order);

        orderRepository.save(order);

        return orderMapper.toResponse(order);
    }

    private void recalculateTotal(Order order) {

        BigDecimal total = order.getItems()
                .stream()
                .map(item ->
                        item.getPrice()
                                .multiply(
                                        BigDecimal.valueOf(item.getQuantity())
                                )
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalCost(total);
    }

    public List<OrderResponse> getMyOrders(String email) {

        User master = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Пользователь не найден: " + email
                        )
                );

        if (master.getRole() != Role.MASTER) {
            throw new IllegalArgumentException(
                    "Только мастер может получать свои заказы"
            );
        }

        return orderRepository.findByMaster(master)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional
    public OrderResponse assignMaster(Long orderId, Long masterId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Заказ не найден: " + orderId
                        )
                );

        User master = userRepository.findById(masterId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Пользователь не найден: " + masterId
                        )
                );

        if (master.getRole() != Role.MASTER) {
            throw new IllegalArgumentException(
                    "Назначить на заказ можно только пользователя с ролью MASTER"
            );
        }

        List<OrderStatus> activeStatuses = List.of(
                OrderStatus.ASSIGNED,
                OrderStatus.AGREED,
                OrderStatus.IN_PROGRESS
        );

        if (orderRepository.existsByMasterAndStatusIn(master, activeStatuses)) {
            throw new IllegalArgumentException(
                    "Нельзя назначить мастеру новый заказ: у него уже есть активный заказ"
            );
        }

        order.setMaster(master);
        order.setStatus(OrderStatus.ASSIGNED);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrders(OrderStatus status, Pageable pageable) {

        System.out.println("=== GET ORDERS START ===");
        System.out.println("STATUS: " + status);
        System.out.println("PAGEABLE: " + pageable);

        Page<Order> orders;

        if (status != null) {
            System.out.println("FIND BY STATUS");
            orders = orderRepository.findByStatus(status, pageable);
        } else {
            System.out.println("FIND ALL");
            orders = orderRepository.findAll(pageable);
        }

        System.out.println("ORDERS FOUND: " + orders.getTotalElements());

        Page<OrderResponse> result = orders.map(orderMapper::toResponse);

        System.out.println("=== GET ORDERS END ===");

        return result;
    }

    @Transactional
    public OrderResponse changeStatus(
            Long orderId,
            OrderStatus newStatus,
            String email
    ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Заказ не найден: " + orderId
                        ));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Пользователь не найден: " + email
                        ));

        if (user.getRole() == Role.MASTER) {
            if (order.getMaster() == null
                    || !order.getMaster().getId().equals(user.getId())) {

                throw new IllegalArgumentException(
                        "Мастер может изменять только свои заказы"
                );
            }
        }

        OrderStatus currentStatus = order.getStatus();

        if (!isAllowedTransition(currentStatus, newStatus)) {
            throw new IllegalArgumentException(
                    "Недопустимый переход статуса: "
                            + currentStatus + " -> " + newStatus
            );
        }

        order.setStatus(newStatus);

        return orderMapper.toResponse(orderRepository.save(order));
    }

    private boolean isAllowedTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        return switch (currentStatus) {
            case NEW -> newStatus == OrderStatus.ASSIGNED;

            case ASSIGNED -> newStatus == OrderStatus.AGREED;

            case AGREED -> newStatus == OrderStatus.IN_PROGRESS
                    || newStatus == OrderStatus.REJECTED_BY_CLIENT;

            case IN_PROGRESS -> newStatus == OrderStatus.COMPLETED;

            // Закрытие выполняется только через отдельный /close,
            // который дополнительно проверяет оплату.
            case COMPLETED -> false;

            case CLOSED, REJECTED_BY_CLIENT -> false;
        };
    }

    @Transactional
    public OrderResponse payOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Заказ не найден: " + orderId
                        ));

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Оплатить можно только завершённый заказ"
            );
        }

        if (order.isPaid()) {
            throw new IllegalArgumentException(
                    "Заказ уже оплачен"
            );
        }

        order.setPaid(true);

        return orderMapper.toResponse(
                orderRepository.save(order)
        );
    }

    @Transactional
    public OrderResponse closeOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Заказ не найден: " + orderId
                        ));

        System.out.println("=== CLOSE ORDER ===");
        System.out.println("ORDER ID: " + order.getId());
        System.out.println("STATUS: " + order.getStatus());
        System.out.println("PAID: " + order.isPaid());

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Закрыть можно только завершённый заказ"
            );
        }

        if (!order.isPaid()) {
            throw new IllegalArgumentException(
                    "Нельзя закрыть неоплаченный заказ"
            );
        }

        order.setStatus(OrderStatus.CLOSED);

        return orderMapper.toResponse(
                orderRepository.save(order)
        );
    }

    @Transactional
    public OrderResponse removeItem(Long orderId, Long itemId, String email) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Заказ не найден: " + orderId
                        )
                );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Пользователь не найден: " + email
                        )
                );

        // MASTER может работать только со своим заказом
        if (user.getRole() == Role.MASTER) {

            if (order.getMaster() == null ||
                    !order.getMaster().getId().equals(user.getId())) {

                throw new IllegalArgumentException(
                        "Мастер может изменять только свои заказы"
                );
            }

        } else if (user.getRole() != Role.ADMIN) {

            throw new IllegalArgumentException(
                    "Недостаточно прав для изменения заказа"
            );
        }

        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalArgumentException(
                    "Удалять позиции можно только из заказа в статусе IN_PROGRESS"
            );
        }

        OrderItem item = order.getItems()
                .stream()
                .filter(orderItem ->
                        orderItem.getId().equals(itemId)
                )
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Позиция заказа не найдена: " + itemId
                        )
                );

        // Если это запчасть — возвращаем её на склад
        if (item.getPart() != null) {

            Part part = item.getPart();

            part.setQuantity(
                    part.getQuantity() + item.getQuantity()
            );

            partRepository.save(part);
        }

        order.getItems().remove(item);

        recalculateTotal(order);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }

    @Transactional
    public OrderResponse updateItemQuantity(
            Long orderId,
            Long itemId,
            Integer newQuantity,
            String email
    ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Заказ не найден: " + orderId));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Пользователь не найден: " + email));

        // Проверяем права
        if (user.getRole() == Role.MASTER) {
            if (order.getMaster() == null ||
                    !order.getMaster().getId().equals(user.getId())) {
                throw new IllegalArgumentException(
                        "Мастер может изменять только свои заказы");
            }
        } else if (user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException(
                    "Недостаточно прав для изменения заказа");
        }

        // Изменять позиции можно только в IN_PROGRESS
        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalArgumentException(
                    "Изменять позиции можно только из заказа в статусе IN_PROGRESS");
        }

        // Ищем позицию
        OrderItem item = order.getItems().stream()
                .filter(orderItem -> orderItem.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Позиция заказа не найдена: " + itemId));

        int oldQuantity = item.getQuantity();

        // Ничего не меняем
        if (oldQuantity == newQuantity) {
            return orderMapper.toResponse(order);
        }

        // Если это запчасть — корректируем склад
        if (item.getPart() != null) {
            Part part = item.getPart();

            int difference = newQuantity - oldQuantity;

            // Увеличиваем количество в заказе
            if (difference > 0) {

                if (part.getQuantity() < difference) {
                    throw new IllegalArgumentException(
                            "Недостаточно запчастей на складе. " +
                                    "Доступно: " + part.getQuantity());
                }

                part.setQuantity(part.getQuantity() - difference);

            } else {
                // Уменьшаем количество в заказе —
                // возвращаем запчасти на склад
                part.setQuantity(part.getQuantity() + Math.abs(difference));
            }

            partRepository.save(part);
        }

        item.setQuantity(newQuantity);

        recalculateTotal(order);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }
}