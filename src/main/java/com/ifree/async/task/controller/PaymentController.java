package com.ifree.async.task.controller;

import com.ifree.async.task.amqp.AmqpService;
import com.ifree.async.task.dao.repository.PaymentRepository;
import com.ifree.async.task.dto.Payment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.ifree.async.task.dto.Payment.toEntity;

@RestController
public class PaymentController {
  private AmqpService amqpService;
  private PaymentRepository paymentRepository;

  public PaymentController(AmqpService amqpService, PaymentRepository paymentRepository) {
    this.amqpService = amqpService;
    this.paymentRepository = paymentRepository;
  }

  @PostMapping("/payment")
  public Mono<Payment> process(Payment payment) throws InterruptedException {
    Thread.sleep(1000);
    return Mono.fromFuture(paymentRepository.save(toEntity(payment)))
        .map(Payment::fromEntity)
        .flatMap(payment1 -> amqpService.send(Mono.just(payment1)))
        .map(a -> payment);
  }

  @GetMapping("/payment")
  public Mono<Payment> getPayment(Long paymentId) {
    return Mono.fromFuture(paymentRepository.findById(paymentId))
        .map(Payment::fromEntity)
        .doOnError(DataIntegrityViolationException.class, e -> new RuntimeException());
  }
}
