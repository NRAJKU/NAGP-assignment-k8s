package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    properties = {
        "spring.flyway.enabled=false",
        "DB_HOST=localhost",
        "DB_PORT=5432",
        "DB_NAME=customerdb",
        "DB_USERNAME=appuser",
        "DB_PASSWORD=password"
    }
)
class DemoApplicationTests {

  @Test
  void contextLoads() {
  }
}