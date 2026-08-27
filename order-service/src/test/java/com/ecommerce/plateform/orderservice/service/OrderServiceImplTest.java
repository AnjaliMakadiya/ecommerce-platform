package com.ecommerce.plateform.orderservice.service;

import com.ecommerce.plateform.orderservice.dto.CreateOrderRequest;
import com.ecommerce.plateform.orderservice.dto.UpdateStatusRequest;
import com.ecommerce.plateform.orderservice.enums.OrderStatus;
import com.ecommerce.plateform.orderservice.kafka.KafkaProducer;
import com.ecommerce.plateform.orderservice.kafka.OrderEvent;
import com.ecommerce.plateform.orderservice.messaging.MessagePublisher;
import com.ecommerce.plateform.orderservice.persistence.port.OrderPersistencePort;
import com.ecommerce.plateform.orderservice.persistence.postgres.entity.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class OrderServiceImplTest {

    @Mock
//    private OrderRepository repository;
    private OrderPersistencePort persistencePort;

    @Mock
    private MessagePublisher producer;

    @InjectMocks
    private OrderServiceImpl orderService;


//    @Test
//    void shouldCreateOrderSuccessfully() {
//
//        // Arrange
//        CreateOrderRequest request = new CreateOrderRequest();
////        request.setCustomerId("CUST101");
////
//        Order savedOrder = new Order();
//        savedOrder.setOrderId(1L);
//        savedOrder.setCustomerId("CUST101");
//        savedOrder.setStatus(OrderStatus.CREATED);
//
//        when(persistencePort.save(any(Order.class))).thenReturn(savedOrder);
//
//        // Act
//        Order result = orderService.createOrder(request);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1L, result.getOrderId());
//        assertEquals("CUST101", result.getCustomerId());
//        assertEquals(OrderStatus.CREATED, result.getStatus());
//
//        verify(persistencePort).save(any(Order.class));
//
//        ArgumentCaptor<OrderEvent> eventCaptor =
//                ArgumentCaptor.forClass(OrderEvent.class);
//
//        verify(producer).sendOrderCreatedEvent(eventCaptor.capture());
//
//        OrderEvent event = eventCaptor.getValue();
//
//        assertEquals(1L, event.getOrderId());
//        assertEquals("CUST101", event.getCustomerId());
//        assertEquals("CREATED", event.getStatus());
//        assertNotNull(event.getTimestamp());
//    }

    @Test
    void shouldReturnAllOrders() {

        Order order = new Order();
        order.setOrderId(1L);

        when(persistencePort.findAll()).thenReturn(List.of(order));

        List<Order> orders = orderService.getAllOrders();

        assertEquals(1, orders.size());

        verify(persistencePort).findAll();
    }

    @Test
    void shouldReturnOrderById() {

        Order order = new Order();
        order.setOrderId(1L);

        when(persistencePort.findById(1L))
                .thenReturn(Optional.of(order));

        Order result = orderService.getOrder(1L);

        assertEquals(1L, result.getOrderId());

        verify(persistencePort).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {

        when(persistencePort.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.getOrder(1L));

        assertEquals("Order not found", exception.getMessage());

        verify(persistencePort).findById(1L);
    }

    @Test
    void shouldUpdateOrderStatus() {

        Order order = new Order();
        order.setOrderId(1L);
        order.setCustomerId("CUST101");
        order.setStatus(OrderStatus.CREATED);

        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setStatus(OrderStatus.SHIPPED);

        when(persistencePort.findById(1L))
                .thenReturn(Optional.of(order));

        when(persistencePort.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.updateStatus(1L, request);

        assertEquals(OrderStatus.SHIPPED, result.getStatus());

        ArgumentCaptor<OrderEvent> eventCaptor =
                ArgumentCaptor.forClass(OrderEvent.class);

        verify(producer).sendStatusUpdateEvent(eventCaptor.capture());

        OrderEvent event = eventCaptor.getValue();

        assertEquals(1L, event.getOrderId());
        assertEquals("CUST101", event.getCustomerId());
        assertEquals("SHIPPED", event.getStatus());

        verify(persistencePort).save(any(Order.class));
    }

    @Test
    void shouldDeleteOrderSuccessfully() {

        Order order = new Order();
        order.setOrderId(1L);

        when(persistencePort.findById(1L))
                .thenReturn(Optional.of(order));

        String result = orderService.deleteOrder(1L);

        assertEquals(
                "Order deleted successfully with id: 1",
                result);

        verify(persistencePort).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingOrder() {

        when(persistencePort.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.deleteOrder(1L));

        assertEquals("Order not found", exception.getMessage());

        verify(persistencePort, never()).deleteById(anyLong());
    }
}
