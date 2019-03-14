package com.ifree.async.task.amqp.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Validated
@ConfigurationProperties("com.ifree.amqp")
public class AmqpConfigurationProperties {
  @NotBlank private String exchange;

  @NotBlank private String routingKey;

  @NotBlank private String host;

  @NotNull private Integer port;

  @NotBlank private String username;

  @NotBlank private String password;

  @NotBlank private String virtualHost;
}
