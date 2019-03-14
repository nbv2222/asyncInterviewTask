package com.ifree.async.task.amqp;

import com.ifree.async.task.amqp.configuration.AmqpConfigurationProperties;
import com.ifree.async.task.dto.Payment;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

public class AmqpService {
  private Sender sender;
  private AmqpConfigurationProperties properties;
  private MessageConverter messageConverter;

  public AmqpService(
      Sender sender, AmqpConfigurationProperties properties, MessageConverter messageConverter) {
    this.sender = sender;
    this.properties = properties;
    this.messageConverter = messageConverter;
  }

  public Mono<Void> send(Payment message) { // could be generic. Simplified it
    return sender.send(
        Mono.just(
            new OutboundMessage(
                properties.getExchange(),
                properties.getRoutingKey(),
                messageConverter.toMessage(message, new MessageProperties()).getBody())));
  }
}
