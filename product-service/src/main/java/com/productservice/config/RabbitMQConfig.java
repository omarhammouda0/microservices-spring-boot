package com.productservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange ("order.exchange");
    }

    @Bean
    public Queue orderQueue() {
        return new Queue("order.created.queue", true, false, false,
                Map.of("x-dead-letter-exchange", "order.dlx",
                        "x-dead-letter-routing-key", "order.failed"));
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with("order.created");
    }

    @Bean
    public TopicExchange deadOrderExchange() {
        return new TopicExchange ("order.dlx");
    }

    @Bean
    public Queue deadOrderQueue() {
        return new Queue("order.failed.queue");
    }

    @Bean
    public Binding deadOrderBinding() {
        return BindingBuilder.bind(deadOrderQueue()).to(deadOrderExchange()).with("order.failed");
    }


    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }


    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory ,
            Jackson2JsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory ( );
        factory.setConnectionFactory ( connectionFactory );
        factory.setMessageConverter ( messageConverter );
        factory.setDefaultRequeueRejected ( false );

        RetryOperationsInterceptor retryInterceptor = RetryInterceptorBuilder.stateless ( )
                .maxAttempts ( 5 )
                .backOffOptions ( 500 , 2.0 , 10000 )
                .build ( );

        factory.setAdviceChain ( retryInterceptor );

        return factory;
    }

}