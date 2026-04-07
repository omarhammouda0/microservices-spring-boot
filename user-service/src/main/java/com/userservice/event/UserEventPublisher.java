package com.userservice.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.util.UUID;

@AllArgsConstructor
@Component
@Slf4j

public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

   public void publishUserUpdateEvent (Long userId){

       log.info("publishUserUpdateEvent userId:{}",userId);

       var userUpdateEvent = new UserUpdatedEvent (
               UUID.randomUUID () ,
               userId
       ) ;

       rabbitTemplate.convertAndSend ( "user.exchange", "user.updated", userUpdateEvent );

   }



}
