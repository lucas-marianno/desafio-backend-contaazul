package com.example.gerador_boleto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.example.gerador_boleto.controller.BankSlipController;
import com.example.gerador_boleto.dto.HelloWorldDTO;

@SpringBootTest
@AutoConfigureRestTestClient
public class SanityCheckTest {

  @Autowired
  private BankSlipController controller;

  @Autowired
  RestTestClient restTestClient;

  @Test
  void sanityCheck() {
    assertThat(controller).isNotNull();
  }

  @SuppressWarnings("deprecation")
  @Test
  void testHelloWithEmptyBody() {
    var response = restTestClient.get().uri("/rest/hello").exchange();

    response.expectStatus().isEqualTo(HttpStatus.I_AM_A_TEAPOT);
    response.expectBody().isEmpty();
  }

  @SuppressWarnings("deprecation")
  @Test
  void testHelloWithName() {
    HelloWorldDTO dto = new HelloWorldDTO("lusca");
    var response = restTestClient.method(HttpMethod.GET).uri("/rest/hello").body(dto).exchange();

    response.expectStatus().isEqualTo(HttpStatus.I_AM_A_TEAPOT);
    response.expectBody().jsonPath("$.response").isEqualTo("Hello, lusca");
  }
}
