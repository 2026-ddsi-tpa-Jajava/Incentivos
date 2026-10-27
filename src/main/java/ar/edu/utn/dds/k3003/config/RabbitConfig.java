package ar.edu.utn.dds.k3003.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String CQRS_EVENTS_QUEUE = "cqrs.events.queue";

    @Bean
    Queue cqrsEventsQueue() {
        return new Queue(CQRS_EVENTS_QUEUE, true);
    }

    @Bean
    MessageConverter rabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
