package com.ifree.async.task.amqp.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifree.async.task.amqp.AmqpService;
import com.ifree.async.task.dao.repository.PaymentRepository;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;

@Configuration
@ConditionalOnProperty("com.ifree.amqp.enabled")
@EnableConfigurationProperties(AmqpConfigurationProperties.class)
public class AmqpServiceAutoConfiguration {

  @Bean
  public AmqpService amqpService(
      AmqpConfigurationProperties properties,
      MessageConverter messageConverter,
      PaymentRepository paymentRepository)
      throws Exception {
    ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.useNio();
    connectionFactory.setUri(properties.getHost());
    connectionFactory.setPort(properties.getPort());
    connectionFactory.setUsername(properties.getUsername());
    connectionFactory.setPassword(properties.getPassword());
    connectionFactory.setVirtualHost(properties.getVirtualHost());

    SenderOptions senderOptions =
        new SenderOptions()
            .connectionFactory(connectionFactory)
            .connectionSubscriptionScheduler(Schedulers.elastic());
    Sender sender = RabbitFlux.createSender(senderOptions);

    return new AmqpService(sender, properties, messageConverter);
  }

  @Bean
  public MessageConverter messageConverter(ObjectMapper objectMapper) {
    Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter(objectMapper);

    ContentTypeDelegatingMessageConverter messageConverter =
        new ContentTypeDelegatingMessageConverter(jsonConverter);

    messageConverter.addDelegate(MediaType.APPLICATION_JSON_VALUE, jsonConverter);

    return messageConverter;
  }
}
