package com.example.demo.controller;

import com.example.demo.entity.Customer;
import com.example.demo.services.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/customers")
@AllArgsConstructor
public class CustomerController {

  private CustomerService service;

  @GetMapping
  public Flux<Customer> getCustomers() {
    return service.getCustomers();
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<Customer>> getCustomer(@PathVariable Long id) {
    return service.getCustomer(id)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }
}
