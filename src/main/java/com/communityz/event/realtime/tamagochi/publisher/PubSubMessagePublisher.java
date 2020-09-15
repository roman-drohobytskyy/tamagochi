package com.communityz.event.realtime.tamagochi.publisher;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "pubSubOutputChannel")
public interface PubSubMessagePublisher {

    void publish(String value);
}
