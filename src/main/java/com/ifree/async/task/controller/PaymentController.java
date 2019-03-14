package com.ifree.async.task.controller;

import com.ifree.async.task.amqp.AmqpService;
import com.ifree.async.task.dao.repository.PaymentRepository;
import com.ifree.async.task.dto.Payment;
import com.ifree.async.task.dto.PaymentStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  public Mono<ResponseEntity> createPayment(@RequestBody Payment payment) {
    return Mono.just(ResponseEntity.accepted().build()).then(process(payment));
    // todo возможно ли вернуть аццептед, до того как будет выполнен метод process
  }

  private Mono<Payment> process(Payment payment) {
    payment.setStatus(PaymentStatus.COMPLETE);
    return Mono.fromFuture(paymentRepository.save(toEntity(payment)))
        .map(Payment::fromEntity)
        .flatMap(
            dto -> {
              try {
                Thread.sleep(2000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              return amqpService.send(dto);
            })
        .thenReturn(payment);
  }

  @GetMapping("/payment")
  public Mono<Payment> getPayment(Long paymentId) {
    return Mono.fromFuture(paymentRepository.findById(paymentId))
        .map(Payment::fromEntity)
        .doOnError(DataIntegrityViolationException.class, e -> new RuntimeException());
  }
}
