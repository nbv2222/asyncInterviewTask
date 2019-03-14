package com.ifree.async.task.dao.entity;

import com.ifree.async.task.dto.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.math.BigInteger;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
  @Id private Long id;

  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  private String account;

  private BigInteger amount;
}
