//package com.ecommerce.plateform.notificationservice.rabbit;
//
//import com.ecommerce.plateform.notificationservice.kafka.OrderEvent;
//import com.ecommerce.plateform.notificationservice.service.NotificationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Service;
//
//import static com.ecommerce.plateform.notificationservice.rabbit.RabbitQueues.*;
//
//@Service
//@RequiredArgsConstructor
//public class OrderRabbitConsume {
//
//    private final NotificationService notificationService;
//
//    @RabbitListener(queues = ORDER_CREATED_QUEUE)
//    public void consume(OrderEvent event){
//        System.out.println("RabbitMQ Message Received");
//        System.out.println(event);
//
//        notificationService.createNotification(event);
//    }
//    @RabbitListener(queues = ORDER_EXCHANGE)
//    public void consumeStatusUpdate(OrderEvent event) {
//
//        System.out.println("Rabbit Received Status Update");
//        System.out.println(event);
//
//        notificationService.createNotification(event);
//    }
//}


package com.ecommerce.plateform.notificationservice.rabbit;
import com.ecommerce.plateform.notificationservice.kafka.OrderEvent;
import com.ecommerce.plateform.notificationservice.messaging.MessageConsumer;
import com.ecommerce.plateform.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import static com.ecommerce.plateform.notificationservice.rabbit.RabbitQueues.*;

@Service
@RequiredArgsConstructor
@Profile("rabbitmq")
public class OrderRabbitConsumer implements MessageConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = ORDER_CREATED_QUEUE)
    public void consumeOrderCreated(OrderEvent event) {

        System.out.println("Received Create Event");
        System.out.println(event);

        notificationService.createNotification(event);
    }

    @RabbitListener(queues = ORDER_UPDATED_QUEUE)
    public void consumeOrderUpdated(OrderEvent event) {

        System.out.println("Received Update Event");
        System.out.println(event);

        notificationService.createNotification(event);
    }
}
