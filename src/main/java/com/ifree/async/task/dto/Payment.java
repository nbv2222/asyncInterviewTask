package com.ifree.async.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
  @NotNull private Long paymentId;

  @NotBlank private String account;

  @NotNull private BigInteger amount;

  private PaymentStatus status;

  public static com.ifree.async.task.dao.entity.Payment toEntity(Payment payment) {
    return com.ifree.async.task.dao.entity.Payment.builder()
        .account(payment.getAccount())
        .amount(payment.getAmount())
        .id(payment.getPaymentId())
        .status(payment.getStatus())
        .build();
  }

  public static Payment fromEntity(com.ifree.async.task.dao.entity.Payment payment) {
    return Payment.builder()
        .account(payment.getAccount())
        .amount(payment.getAmount())
        .paymentId(payment.getId())
        .status(payment.getStatus())
        .build();
  }
}
