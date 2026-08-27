package com.ecommerce.plateform.orderservice.service;

import com.ecommerce.plateform.orderservice.client.ProductClient;
import com.ecommerce.plateform.orderservice.client.ProductInfo;
import com.ecommerce.plateform.orderservice.dto.CreateOrderRequest;
import com.ecommerce.plateform.orderservice.dto.OrderItemRequest;
import com.ecommerce.plateform.orderservice.dto.UpdateStatusRequest;
import com.ecommerce.plateform.orderservice.persistence.port.OrderPersistencePort;
import com.ecommerce.plateform.orderservice.persistence.postgres.entity.Order;
import com.ecommerce.plateform.orderservice.kafka.OrderEvent;
import com.ecommerce.plateform.orderservice.persistence.postgres.entity.OrderItem;
import com.ecommerce.plateform.orderservice.persistence.postgres.mapper.OrderMapper;
import com.ecommerce.plateform.orderservice.messaging.MessagePublisher;
import com.ecommerce.plateform.orderservice.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderPersistencePort persistencePort;

    private final ProductClient productClient;
    private final MessagePublisher producer;

    @Override
    public Order createOrder(CreateOrderRequest request) {

        Long userId = SecurityUtils.getCurrentUserId();
        String customerId = String.valueOf(userId);
        System.out.println("Customer ID: " + customerId);

        Order order = OrderMapper.newOrder(customerId);
        BigDecimal totalAmount = BigDecimal.ZERO;

        System.out.println(
                "Request items size: " +
                        request.getItems().size()
        );

        for (OrderItemRequest itemRequest : request.getItems()) {

            ProductInfo product =
                    productClient.getProduct(
                            itemRequest.getProductId()
                    );

            if (product.getStock() == null ||
                    product.getStock() < itemRequest.getQuantity()) {

                throw new IllegalStateException(
                        "Insufficient stock for "
                                + product.getName()
                                + ": only "
                                + (product.getStock() == null
                                ? 0
                                : product.getStock())
                                + " left"
                );
            }

            OrderItem orderItem = new OrderItem();

            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setProductImageUrl(product.getImageUrl());

            BigDecimal subtotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            itemRequest.getQuantity()
                                    )
                            );

            orderItem.setSubtotal(subtotal);

            order.addItem(orderItem);

            totalAmount =
                    totalAmount.add(subtotal);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder =
                persistencePort.save(order);

        // Decrease product stock
//        for (OrderItem item : savedOrder.getItems()) {
//
//            try {
//
//                productClient.decrementStock(
//                        item.getProductId(),
//                        item.getQuantity()
//                );
//
//            } catch (Exception e) {
//                log.error(
//                        "Failed to decrement stock for product {} " +
//                                "after order {}: {}",
//                        item.getProductId(),
//                        savedOrder.getOrderId(),
//                        e.getMessage(),
//                        e
//                );
//            }
//        }
        for (OrderItem item : savedOrder.getItems()) {

            System.out.println("======================================");
            System.out.println("Calling Product Service");
            System.out.println("Product ID : " + item.getProductId());
            System.out.println("Quantity   : " + item.getQuantity());
            System.out.println("======================================");

            productClient.decrementStock(
                    item.getProductId(),
                    item.getQuantity()
            );

            System.out.println("Stock decrement call completed successfully");
        }

        OrderEvent event =
                new OrderEvent(
                        savedOrder.getOrderId(),
                        savedOrder.getCustomerId(),
                        savedOrder.getStatus().name(),
                        LocalDateTime.now()
                );

        producer.sendOrderCreatedEvent(event);

        return savedOrder;
    }
//    public Order createOrder(CreateOrderRequest request) {
//
//        String customerId = String.valueOf(SecurityUtils.getCurrentUserId());
//        System.out.println("customer id :"+customerId);
//        Order order = OrderMapper.newOrder(customerId);
//        BigDecimal totalAmount = BigDecimal.ZERO;
//        System.out.println("request items size :"+request.getItems().size());
//        for (OrderItemRequest itemRequest : request.getItems()) {
//            ProductInfo product = productClient.getProduct(itemRequest.getProductId());
//            if(product.getStock()==null || product.getStock()<itemRequest.getQuantity()){
//                throw new IllegalStateException("Insufficient stock for\"" + product.getStock()+ "\": only" +(product.getStock() ==null?0:product.getStock()+" left"));
//            }
//            OrderItem orderItem = new OrderItem();
//            orderItem.setProductId(product.getId());
//            orderItem.setProductName(product.getName());
//            orderItem.setQuantity(itemRequest.getQuantity());
//            orderItem.setUnitPrice(product.getPrice());
//            orderItem.setProductImageUrl(product.getImageUrl());
//            order.addItem(orderItem);
//            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
//            System.out.println("total amount after adding item: " + totalAmount);
//            orderItem.setSubtotal(totalAmount);
//        }
//        order.setTotalAmount(totalAmount);
//
//        Order savedOrder = persistencePort.save(order);
//
//        for (OrderItem item : savedOrder.getItems()) {
//            try {
//                productClient.decrementStock(item.getProductId(), item.getQuantity());
//            } catch (Exception e) {
//                log.error("Failed to decrement stock for product {} after order {}: {}",
//                        item.getProductId(), savedOrder.getOrderId(), e.getMessage(), e);
//            }
//        }
//        OrderEvent event = new OrderEvent(
//                savedOrder.getOrderId(),
//                savedOrder.getCustomerId(),
//                savedOrder.getStatus().name(),
//                LocalDateTime.now()
//        );
//        producer.sendOrderCreatedEvent(event);
//        return savedOrder;
//    }

    @Override
    public List<Order> getAllOrders() {
       // return repository.findAll();
        return persistencePort.findAll();
    }

    @Override
    public Order getOrder(Long id) {
//        return repository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        return persistencePort.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public Order updateStatus(Long id, UpdateStatusRequest newStatus) {
        Order order = getOrder(id);

        order.setStatus(newStatus.getStatus());

      //  Order updateOrder = repository.save(order);

        Order updateOrder = persistencePort.save(order);
        OrderEvent event = new OrderEvent(
                updateOrder.getOrderId(),
                updateOrder.getCustomerId(),
                updateOrder.getStatus().name(),
                LocalDateTime.now()
        );

        producer.sendStatusUpdateEvent(event);
        return updateOrder;
    }


    @Override
    public String deleteOrder(Long id) {

        Order order = persistencePort.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        persistencePort.deleteById(id);

        return "Order deleted successfully with id: " + id;
    }

    @Override
    public List<Order> getMyOrders() {
     String customerId= String.valueOf(SecurityUtils.getCurrentUserId());
     return persistencePort.findByCustomerId(customerId);
    }
}
