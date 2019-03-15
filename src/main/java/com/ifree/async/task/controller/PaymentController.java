package com.ifree.async.task.controller;

import com.ifree.async.task.amqp.AmqpService;
import com.ifree.async.task.dao.repository.PaymentRepository;
import com.ifree.async.task.dto.Payment;
import com.ifree.async.task.dto.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.concurrent.Executors;

@Slf4j
@RestController
public class PaymentController {
  private AmqpService amqpService;
  private PaymentRepository paymentRepository;

  public PaymentController(AmqpService amqpService, PaymentRepository paymentRepository) {
    this.amqpService = amqpService;
    this.paymentRepository = paymentRepository;
  }

  @PostMapping("/payment")
  public Mono<ResponseEntity> createPayment(@RequestBody @Valid Payment payment) {
    Executors.newSingleThreadExecutor().execute(() -> process(payment));
    return Mono.just(ResponseEntity.accepted().build());
  }

  private void process(Payment payment) {
    payment.setStatus(PaymentStatus.COMPLETE);
    Mono.just(payment)
        .flatMap(
            dto -> {
              try {
                Thread.sleep(5000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              return amqpService.send(dto);
            })
        .then(Mono.just(payment))
        .map(Payment::toEntity)
        .flatMap(entity -> Mono.fromFuture(paymentRepository.save(entity)))
        .doOnError(DataIntegrityViolationException.class, e -> log.info("Database Error", e))
        .doOnError(Exception.class, e -> log.info("Some error occurs", e))
        // possible fill the chain next
        // could be ExceptionHandler -> @RestControllerAdvice
        .block();
  }

  @GetMapping("/payment")
  public Mono<ResponseEntity<Payment>> getPayment(Long paymentId) {
    return Mono.fromFuture(paymentRepository.findById(paymentId))
        .map(Payment::fromEntity)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.badRequest().build());
  }
}
