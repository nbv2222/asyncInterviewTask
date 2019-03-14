package com.ifree.async.task.dao.repository;

import com.ifree.async.task.dao.entity.Payment;
import org.springframework.data.repository.Repository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

public interface PaymentRepository extends Repository<Payment, Long> {

  @Async
  @Transactional
  CompletableFuture<Payment> save(Payment payment);

  @Async
  CompletableFuture<Payment> findById(Long id);
}
