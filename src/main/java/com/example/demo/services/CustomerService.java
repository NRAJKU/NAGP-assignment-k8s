package com.example.demo.services;

import com.example.demo.entity.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
  Flux<Customer> getCustomers();
  Mono<Customer> getCustomer(Long id);
}
