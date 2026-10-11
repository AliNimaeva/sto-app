//package com.example.sto_app.service;
//
//import com.example.sto_app.dto.CreateOrderRequest;
//import com.example.sto_app.dto.OrderItemRequest;
//import com.example.sto_app.dto.OrderResponse;
//import com.example.sto_app.exception.ResourceNotFoundException;
//import com.example.sto_app.mapper.OrderMapper;
//import com.example.sto_app.model.Car;
//import com.example.sto_app.model.Order;
//import com.example.sto_app.model.OrderStatus;
//import com.example.sto_app.model.Part;
//import com.example.sto_app.model.Role;
//import com.example.sto_app.model.Service;
//import com.example.sto_app.model.User;
//import com.example.sto_app.repository.CarRepository;
//import com.example.sto_app.repository.OrderRepository;
//import com.example.sto_app.repository.PartRepository;
//import com.example.sto_app.repository.ServiceRepository;
//import com.example.sto_app.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class OrderServiceTest {
//
//    @Mock
//    private OrderRepository orderRepository;
//
//    @Mock
//    private CarRepository carRepository;
//
//    @Mock
//    private PartRepository partRepository;
//
//    @Mock
//    private ServiceRepository serviceRepository;
//
//    @Mock
//    private OrderMapper orderMapper;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private OrderService orderService;
//
//    private Car car;
//    private User master;
//    private User manager;
//    private Order order;
//
//    @BeforeEach
//    void setUp() {
//        car = new Car();
//        car.setId(1L);
//        car.setBrand("Toyota");
//        car.setModel("Camry");
//        car.setYear(2020);
//        car.setPlateNumber("A123BC");
//        car.setClientName("Иван");
//        car.setClientPhone("+79990000000");
//
//        master = new User();
//        master.setId(10L);
//        master.setName("Мастер Иванов");
//        master.setEmail("master@sto.ru");
//        master.setRole(Role.MASTER);
//
//        manager = new User();
//        manager.setId(20L);
//        manager.setName("Менеджер");
//        manager.setEmail("manager@sto.ru");
//        manager.setRole(Role.MANAGER);
//
//        order = new Order();
//        order.setId(100L);
//        order.setNumber("STO-12345678");
//        order.setCar(car);
//        order.setProblemDescription("Не заводится");
//        order.setStatus(OrderStatus.NEW);
//        order.setTotalCost(BigDecimal.ZERO);
//        order.setPaid(false);
//        order.setItems(new ArrayList<>());
//    }
//
//    // ============================================================
//    // CREATE ORDER
//    // ============================================================
//
//    @Test
//    void createPublicOrder_shouldCreateNewCarAndOrder() {
//        CreateOrderRequest request = createOrderRequest();
//
//        when(carRepository.findByPlateNumber("A123BC"))
//                .thenReturn(Optional.empty());
//
//        when(carRepository.save(any(Car.class)))
//                .thenReturn(car);
//
//        when(orderRepository.save(any(Order.class)))
//                .thenReturn(order);
//
//        OrderResponse response = new OrderResponse();
//        response.setId(100L);
//
//        when(orderMapper.toResponse(any(Order.class)))
//                .thenReturn(response);
//
//        OrderResponse result = orderService.createPublicOrder(request);
//
//        assertNotNull(result);
//        assertEquals(100L, result.getId());
//
//        verify(carRepository).save(any(Car.class));
//        verify(orderRepository).save(any(Order.class));
//        verify(orderMapper).toResponse(any(Order.class));
//
//        ArgumentCaptor<Order> captor =
//                ArgumentCaptor.forClass(Order.class);
//
//        verify(orderRepository).save(captor.capture());
//
//        Order savedOrder = captor.getValue();
//
//        assertEquals(car, savedOrder.getCar());
//        assertEquals("Не заводится", savedOrder.getProblemDescription());
//        assertEquals(OrderStatus.NEW, savedOrder.getStatus());
//        assertEquals(BigDecimal.ZERO, savedOrder.getTotalCost());
//        assertFalse(savedOrder.isPaid());
//        assertNotNull(savedOrder.getNumber());
//        assertTrue(savedOrder.getNumber().startsWith("STO-"));
//    }
//
//    @Test
//    void createPublicOrder_shouldUseExistingCar() {
//        CreateOrderRequest request = createOrderRequest();
//
//        when(carRepository.findByPlateNumber("A123BC"))
//                .thenReturn(Optional.of(car));
//
//        when(orderRepository.save(any(Order.class)))
//                .thenReturn(order);
//
//        when(orderMapper.toResponse(any(Order.class)))
//                .thenReturn(new OrderResponse());
//
//        orderService.createPublicOrder(request);
//
//        verify(carRepository, never()).save(any(Car.class));
//        verify(orderRepository).save(any(Order.class));
//    }
//
//    // ============================================================
//    // GET BY ID / NUMBER
//    // ============================================================
//
//    @Test
//    void getById_shouldReturnOrder() {
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        OrderResponse response = new OrderResponse();
//        response.setId(100L);
//
//        when(orderMapper.toResponse(order))
//                .thenReturn(response);
//
//        OrderResponse result = orderService.getById(100L);
//
//        assertNotNull(result);
//        assertEquals(100L, result.getId());
//
//        verify(orderRepository).findById(100L);
//        verify(orderMapper).toResponse(order);
//    }
//
//    @Test
//    void getById_shouldThrowExceptionWhenOrderNotFound() {
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.empty());
//
//        assertThrows(
//                ResourceNotFoundException.class,
//                () -> orderService.getById(100L)
//        );
//
//        verify(orderMapper, never()).toResponse(any());
//    }
//
//    @Test
//    void getByNumber_shouldReturnOrder() {
//        when(orderRepository.findByNumber("STO-12345678"))
//                .thenReturn(Optional.of(order));
//
//        OrderResponse response = new OrderResponse();
//        response.setNumber("STO-12345678");
//
//        when(orderMapper.toResponse(order))
//                .thenReturn(response);
//
//        OrderResponse result =
//                orderService.getByNumber("STO-12345678");
//
//        assertEquals("STO-12345678", result.getNumber());
//
//        verify(orderRepository).findByNumber("STO-12345678");
//    }
//
//    @Test
//    void getByNumber_shouldThrowExceptionWhenOrderNotFound() {
//        when(orderRepository.findByNumber("UNKNOWN"))
//                .thenReturn(Optional.empty());
//
//        assertThrows(
//                ResourceNotFoundException.class,
//                () -> orderService.getByNumber("UNKNOWN")
//        );
//    }
//
//    // ============================================================
//    // ADD SERVICE
//    // ============================================================
//
//    @Test
//    void addItem_shouldAddServiceAndRecalculateTotal() {
//        Service service = new Service();
//        service.setId(5L);
//        service.setName("Замена масла");
//        service.setPrice(new BigDecimal("1500.00"));
//
//        OrderItemRequest request = new OrderItemRequest();
//        request.setServiceId(5L);
//        request.setQuantity(2);
//
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        when(serviceRepository.findById(5L))
//                .thenReturn(Optional.of(service));
//
//        when(orderRepository.save(any(Order.class)))
//                .thenReturn(order);
//
//        when(orderMapper.toResponse(order))
//                .thenReturn(new OrderResponse());
//
//        orderService.addItem(100L, request);
//
//        assertEquals(1, order.getItems().size());
//        assertEquals(
//                new BigDecimal("3000.00"),
//                order.getTotalCost()
//        );
//
//        assertEquals(
//                service,
//                order.getItems().get(0).getService()
//        );
//
//        assertEquals(
//                new BigDecimal("1500.00"),
//                order.getItems().get(0).getPrice()
//        );
//
//        verify(serviceRepository).findById(5L);
//        verify(orderRepository).save(order);
//    }
//
//    @Test
//    void addItem_shouldThrowWhenServiceAndPartSpecifiedTogether() {
//        OrderItemRequest request = new OrderItemRequest();
//        request.setServiceId(5L);
//        request.setPartId(10L);
//        request.setQuantity(1);
//
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> orderService.addItem(100L, request)
//        );
//
//        verify(serviceRepository, never()).findById(any());
//        verify(partRepository, never()).findById(any());
//        verify(orderRepository, never()).save(any());
//    }
//
//    @Test
//    void addItem_shouldThrowWhenNeitherServiceNorPartSpecified() {
//        OrderItemRequest request = new OrderItemRequest();
//        request.setQuantity(1);
//
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> orderService.addItem(100L, request)
//        );
//
//        verify(serviceRepository, never()).findById(any());
//        verify(partRepository, never()).findById(any());
//        verify(orderRepository, never()).save(any());
//    }
//
//    @Test
//    void addItem_shouldThrowWhenServiceNotFound() {
//        OrderItemRequest request = new OrderItemRequest();
//        request.setServiceId(5L);
//        request.setQuantity(1);
//
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        when(serviceRepository.findById(5L))
//                .thenReturn(Optional.empty());
//
//        assertThrows(
//                ResourceNotFoundException.class,
//                () -> orderService.addItem(100L, request)
//        );
//
//        verify(orderRepository, never()).save(any());
//    }
//
//    // ============================================================
//    // ADD PART
//    // ============================================================
//
//    @Test
//    void addItem_shouldAddPartAndDecreaseStock() {
//        Part part = new Part();
//        part.setId(10L);
//        part.setName("Масляный фильтр");
//        part.setPrice(new BigDecimal("500.00"));
//        part.setQuantity(10);
//
//        OrderItemRequest request = new OrderItemRequest();
//        request.setPartId(10L);
//        request.setQuantity(3);
//
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        when(partRepository.findById(10L))
//                .thenReturn(Optional.of(part));
//
//        when(partRepository.save(part))
//                .thenReturn(part);
//
//        when(orderRepository.save(order))
//                .thenReturn(order);
//
//        when(orderMapper.toResponse(order))
//                .thenReturn(new OrderResponse());
//
//        orderService.addItem(100L, request);
//
//        assertEquals(7, part.getQuantity());
//
//        assertEquals(1, order.getItems().size());
//        assertEquals(
//                new BigDecimal("1500.00"),
//                order.getTotalCost()
//        );
//
//        verify(partRepository).save(part);
//        verify(orderRepository).save(order);
//    }
//
//    @Test
//    void addItem_shouldThrowWhenNotEnoughParts() {
//        Part part = new Part();
//        part.setId(10L);
//        part.setPrice(new BigDecimal("500.00"));
//        part.setQuantity(2);
//
//        OrderItemRequest request = new OrderItemRequest();
//        request.setPartId(10L);
//        request.setQuantity(3);
//
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        when(partRepository.findById(10L))
//                .thenReturn(Optional.of(part));
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> orderService.addItem(100L, request)
//        );
//
//        assertEquals(2, part.getQuantity());
//        assertTrue(order.getItems().isEmpty());
//
//        verify(partRepository, never()).save(any());
//        verify(orderRepository, never()).save(any());
//    }
//
//    @Test
//    void addItem_shouldThrowWhenPartNotFound() {
//        OrderItemRequest request = new OrderItemRequest();
//        request.setPartId(10L);
//        request.setQuantity(1);
//
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        when(partRepository.findById(10L))
//                .thenReturn(Optional.empty());
//
//        assertThrows(
//                ResourceNotFoundException.class,
//                () -> orderService.addItem(100L, request)
//        );
//    }
//
//    // ============================================================
//    // MY ORDERS
//    // ============================================================
//
//    @Test
//    void getMyOrders_shouldReturnMasterOrders() {
//        when(userRepository.findByEmail("master@sto.ru"))
//                .thenReturn(Optional.of(master));
//
//        when(orderRepository.findByMaster(master))
//                .thenReturn(List.of(order));
//
//        when(orderMapper.toResponse(order))
//                .thenReturn(new OrderResponse());
//
//        List<OrderResponse> result =
//                orderService.getMyOrders("master@sto.ru");
//
//        assertEquals(1, result.size());
//
//        verify(orderRepository).findByMaster(master);
//    }
//
//    @Test
//    void getMyOrders_shouldThrowWhenUserIsNotMaster() {
//        when(userRepository.findByEmail("manager@sto.ru"))
//                .thenReturn(Optional.of(manager));
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> orderService.getMyOrders("manager@sto.ru")
//        );
//
//        verify(orderRepository, never()).findByMaster(any());
//    }
//
//    @Test
//    void getMyOrders_shouldThrowWhenUserNotFound() {
//        when(userRepository.findByEmail("unknown@sto.ru"))
//                .thenReturn(Optional.empty());
//
//        assertThrows(
//                ResourceNotFoundException.class,
//                () -> orderService.getMyOrders("unknown@sto.ru")
//        );
//    }
//
//    // ============================================================
//    // ASSIGN MASTER
//    // ============================================================
//
//    @Test
//    void assignMaster_shouldAssignMasterAndChangeStatus() {
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        when(userRepository.findById(10L))
//                .thenReturn(Optional.of(master));
//
//        when(orderRepository.save(order))
//                .thenReturn(order);
//
//        when(orderMapper.toResponse(order))
//                .thenReturn(new OrderResponse());
//
//        orderService.assignMaster(100L, 10L);
//
//        assertEquals(master, order.getMaster());
//        assertEquals(OrderStatus.ASSIGNED, order.getStatus());
//
//        verify(orderRepository).save(order);
//    }
//
//    @Test
//    void assignMaster_shouldThrowWhenUserIsNotMaster() {
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        when(userRepository.findById(20L))
//                .thenReturn(Optional.of(manager));
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> orderService.assignMaster(100L, 20L)
//        );
//
//        verify(orderRepository, never()).save(any());
//    }
//
//    @Test
//    void assignMaster_shouldThrowWhenMasterNotFound() {
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        when(userRepository.findById(10L))
//                .thenReturn(Optional.empty());
//
//        assertThrows(
//                ResourceNotFoundException.class,
//                () -> orderService.assignMaster(100L, 10L)
//        );
//    }
//
//    // ============================================================
//    // STATUS TRANSITIONS
//    // ============================================================
//
//    @Test
//    void changeStatus_shouldAllowNewToAssigned() {
//        order.setStatus(OrderStatus.NEW);
//        order.setMaster(master);
//
//        changeStatusAsMaster(OrderStatus.ASSIGNED);
//
//        assertEquals(OrderStatus.ASSIGNED, order.getStatus());
//    }
//
//    @Test
//    void changeStatus_shouldAllowAssignedToAgreed() {
//        order.setStatus(OrderStatus.ASSIGNED);
//        order.setMaster(master);
//
//        changeStatusAsMaster(OrderStatus.AGREED);
//
//        assertEquals(OrderStatus.AGREED, order.getStatus());
//    }
//
//    @Test
//    void changeStatus_shouldAllowAgreedToInProgress() {
//        order.setStatus(OrderStatus.AGREED);
//        order.setMaster(master);
//
//        changeStatusAsMaster(OrderStatus.IN_PROGRESS);
//
//        assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
//    }
//
//    @Test
//    void changeStatus_shouldAllowAgreedToRejected() {
//        order.setStatus(OrderStatus.AGREED);
//        order.setMaster(master);
//
//        changeStatusAsMaster(OrderStatus.REJECTED_BY_CLIENT);
//
//        assertEquals(
//                OrderStatus.REJECTED_BY_CLIENT,
//                order.getStatus()
//        );
//    }
//
//    @Test
//    void changeStatus_shouldAllowInProgressToCompleted() {
//        order.setStatus(OrderStatus.IN_PROGRESS);
//        order.setMaster(master);
//
//        changeStatusAsMaster(OrderStatus.COMPLETED);
//
//        assertEquals(OrderStatus.COMPLETED, order.getStatus());
//    }
//
//    @Test
//    void changeStatus_shouldNotAllowCompletedToClosed() {
//        order.setStatus(OrderStatus.COMPLETED);
//        order.setMaster(master);
//
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        when(userRepository.findByEmail(master.getEmail()))
//                .thenReturn(Optional.of(master));
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> orderService.changeStatus(
//                        100L,
//                        OrderStatus.CLOSED,
//                        master.getEmail()
//                )
//        );
//
//        assertEquals(OrderStatus.COMPLETED, order.getStatus());
//        verify(orderRepository, never()).save(any());
//    }
//
//    @Test
//    void changeStatus_shouldNotAllowClosedToAnyStatus() {
//        order.setStatus(OrderStatus.CLOSED);
//        order.setMaster(master);
//
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        when(userRepository.findByEmail(master.getEmail()))
//                .thenReturn(Optional.of(master));
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> orderService.changeStatus(
//                        100L,
//                        OrderStatus.IN_PROGRESS,
//                        master.getEmail()
//                )
//        );
//
//        verify(orderRepository, never()).save(any());
//    }
//
//    @Test
//    void changeStatus_shouldNotAllowRejectedOrderToChangeStatus() {
//        order.setStatus(OrderStatus.REJECTED_BY_CLIENT);
//        order.setMaster(master);
//
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        when(userRepository.findByEmail(master.getEmail()))
//                .thenReturn(Optional.of(master));
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> orderService.changeStatus(
//                        100L,
//                        OrderStatus.IN_PROGRESS,
//                        master.getEmail()
//                )
//        );
//
//        verify(orderRepository, never()).save(any());
//    }
//
//    @Test
//    void changeStatus_shouldThrowWhenMasterChangesAnotherMastersOrder() {
//        User anotherMaster = new User();
//        anotherMaster.setId(99L);
//        anotherMaster.setRole(Role.MASTER);
//        anotherMaster.setEmail("another@sto.ru");
//
//        order.setStatus(OrderStatus.ASSIGNED);
//        order.setMaster(master);
//
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        when(userRepository.findByEmail("another@sto.ru"))
//                .thenReturn(Optional.of(anotherMaster));
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> orderService.changeStatus(
//                        100L,
//                        OrderStatus.AGREED,
//                        "another@sto.ru"
//                )
//        );
//
//        verify(orderRepository, never()).save(any());
//    }
//
//    @Test
//    void changeStatus_shouldThrowWhenMasterHasNoAssignedOrder() {
//        order.setStatus(OrderStatus.NEW);
//        order.setMaster(null);
//
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        when(userRepository.findByEmail(master.getEmail()))
//                .thenReturn(Optional.of(master));
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> orderService.changeStatus(
//                        100L,
//                        OrderStatus.ASSIGNED,
//                        master.getEmail()
//                )
//        );
//
//        verify(orderRepository, never()).save(any());
//    }
//
//    @Test
//    void changeStatus_shouldThrowWhenTransitionIsInvalid() {
//        order.setStatus(OrderStatus.NEW);
//        order.setMaster(master);
//
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        when(userRepository.findByEmail(master.getEmail()))
//                .thenReturn(Optional.of(master));
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> orderService.changeStatus(
//                        100L,
//                        OrderStatus.COMPLETED,
//                        master.getEmail()
//                )
//        );
//
//        verify(orderRepository, never()).save(any());
//    }
//
//    // ============================================================
//    // PAYMENT
//    // ============================================================
//
//    @Test
//    void payOrder_shouldMarkCompletedOrderAsPaid() {
//        order.setStatus(OrderStatus.COMPLETED);
//        order.setPaid(false);
//
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        when(orderRepository.save(order))
//                .thenReturn(order);
//
//        when(orderMapper.toResponse(order))
//                .thenReturn(new OrderResponse());
//
//        orderService.payOrder(100L);
//
//        assertTrue(order.isPaid());
//
//        verify(orderRepository).save(order);
//    }
//
//    @Test
//    void payOrder_shouldThrowWhenOrderIsNotCompleted() {
//        order.setStatus(OrderStatus.IN_PROGRESS);
//        order.setPaid(false);
//
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> orderService.payOrder(100L)
//        );
//
//        assertFalse(order.isPaid());
//
//        verify(orderRepository, never()).save(any());
//    }
//
//    @Test
//    void payOrder_shouldThrowWhenAlreadyPaid() {
//        order.setStatus(OrderStatus.COMPLETED);
//        order.setPaid(true);
//
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> orderService.payOrder(100L)
//        );
//
//        verify(orderRepository, never()).save(any());
//    }
//
//    @Test
//    void payOrder_shouldThrowWhenOrderNotFound() {
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.empty());
//
//        assertThrows(
//                ResourceNotFoundException.class,
//                () -> orderService.payOrder(100L)
//        );
//    }
//
//    // ============================================================
//    // CLOSE ORDER
//    // ============================================================
//
//    @Test
//    void closeOrder_shouldClosePaidCompletedOrder() {
//        order.setStatus(OrderStatus.COMPLETED);
//        order.setPaid(true);
//
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        when(orderRepository.save(order))
//                .thenReturn(order);
//
//        when(orderMapper.toResponse(order))
//                .thenReturn(new OrderResponse());
//
//        orderService.closeOrder(100L);
//
//        assertEquals(OrderStatus.CLOSED, order.getStatus());
//
//        verify(orderRepository).save(order);
//    }
//
//    @Test
//    void closeOrder_shouldThrowWhenOrderIsNotCompleted() {
//        order.setStatus(OrderStatus.IN_PROGRESS);
//        order.setPaid(true);
//
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> orderService.closeOrder(100L)
//        );
//
//        assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
//
//        verify(orderRepository, never()).save(any());
//    }
//
//    @Test
//    void closeOrder_shouldThrowWhenOrderIsNotPaid() {
//        order.setStatus(OrderStatus.COMPLETED);
//        order.setPaid(false);
//
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> orderService.closeOrder(100L)
//        );
//
//        assertEquals(OrderStatus.COMPLETED, order.getStatus());
//
//        verify(orderRepository, never()).save(any());
//    }
//
//    @Test
//    void closeOrder_shouldThrowWhenOrderNotFound() {
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.empty());
//
//        assertThrows(
//                ResourceNotFoundException.class,
//                () -> orderService.closeOrder(100L)
//        );
//    }
//
//    // ============================================================
//    // HELPERS
//    // ============================================================
//
//    private CreateOrderRequest createOrderRequest() {
//        CreateOrderRequest request = new CreateOrderRequest();
//
//        request.setClientName("Иван");
//        request.setClientPhone("+79990000000");
//        request.setBrand("Toyota");
//        request.setModel("Camry");
//        request.setYear(2020);
//        request.setPlateNumber("A123BC");
//        request.setProblemDescription("Не заводится");
//
//        return request;
//    }
//
//    private void changeStatusAsMaster(OrderStatus newStatus) {
//        when(orderRepository.findById(100L))
//                .thenReturn(Optional.of(order));
//
//        when(userRepository.findByEmail(master.getEmail()))
//                .thenReturn(Optional.of(master));
//
//        when(orderRepository.save(order))
//                .thenReturn(order);
//
//        when(orderMapper.toResponse(order))
//                .thenReturn(new OrderResponse());
//
//        orderService.changeStatus(
//                100L,
//                newStatus,
//                master.getEmail()
//        );
//    }
//}