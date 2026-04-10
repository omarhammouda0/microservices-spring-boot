package com.productervice.event;

import com.productervice.dto.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
public class UserEventConsumer {

    private final RedisTemplate<String, UserResponseDTO> redisTemplate;

    @RabbitListener(queues = "user.updated.queue")

    public void handleUserUpdate(UserUpdatedEvent userUpdatedEvent) {

        var userId = userUpdatedEvent.userId ( );

        try {

            redisTemplate.delete ( "user:" + userId );
            log.info ( "Evicting Redis cache for user:{}" , userId );

        } catch (Exception e) {
            log.warn ( "Error while deleting user:{} from Redis" , userId );
        }


    }

}
