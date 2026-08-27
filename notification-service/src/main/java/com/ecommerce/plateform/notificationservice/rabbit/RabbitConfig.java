//package com.ecommerce.plateform.notificationservice.rabbit;
//
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.TopicExchange;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.amqp.support.converter.MessageConverter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.amqp.core.Queue;
//
//import static com.ecommerce.plateform.notificationservice.rabbit.RabbitQueues.*;
//
//
//@Configuration
//public class RabbitConfig {
//
//
//    @Bean
//    public Queue orderQueue() {
//        return new Queue(ORDER_CREATED_QUEUE);
//    }
//
//    @Bean
//    public TopicExchange orderExchange() {
//        return new TopicExchange(ORDER_EXCHANGE);
//    }
//
//    @Bean
//    public Binding binding(Queue orderQueue,
//                           TopicExchange orderExchange) {
//
//        return BindingBuilder
//                .bind(orderQueue)
//                .to(orderExchange)
//                .with(ORDER_CREATED_ROUTING_KEY);
//    }
//
//    @Bean
//    public MessageConverter messageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }
//
//    @Bean
//    public Queue orderStatusQueue() {
//        return new Queue("order.status.queue");
//    }
//
//    @Bean
//    public Binding statusBinding(
//            Queue orderStatusQueue,
//            TopicExchange orderExchange) {
//
//        return BindingBuilder
//                .bind(orderStatusQueue)
//                .to(orderExchange)
//                .with(ORDER_UPDATED_ROUTING_KEY);
//}
//
//


package com.ecommerce.plateform.notificationservice.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.ecommerce.plateform.notificationservice.rabbit.RabbitQueues.*;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_CREATED_QUEUE);
    }

    @Bean
    public Queue orderUpdatedQueue() {
        return new Queue(ORDER_UPDATED_QUEUE);
    }

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Binding orderCreatedBinding(
            Queue orderQueue,
            TopicExchange orderExchange) {

        return BindingBuilder
                .bind(orderQueue)
                .to(orderExchange)
                .with(ORDER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding orderUpdatedBinding(
            Queue orderUpdatedQueue,
            TopicExchange orderExchange) {

        return BindingBuilder
                .bind(orderUpdatedQueue)
                .to(orderExchange)
                .with(ORDER_UPDATED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        return factory;
    }
}