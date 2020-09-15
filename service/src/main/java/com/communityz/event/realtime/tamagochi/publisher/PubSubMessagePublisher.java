package com.communityz.event.realtime.tamagochi.publisher;

import org.springframework.integration.annotation.MessagingGateway;

/**
 * interface for sending a message to Pub/Sub.
 */

@MessagingGateway(defaultRequestChannel = "pubSubOutputChannel")
public interface PubSubMessagePublisher {

    void publish(String value);
}
