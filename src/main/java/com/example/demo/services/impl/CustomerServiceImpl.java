package com.example.demo.services.impl;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.services.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

  private CustomerRepository repository;

  public Flux<Customer> getCustomers() {
    return repository.findAll();
  }

  public Mono<Customer> getCustomer(Long id) {
    return repository.findById(id);
  }
}