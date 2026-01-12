package com.example.gerador_boleto.controller;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.example.gerador_boleto.dto.BankSlipRequest;
import com.example.gerador_boleto.model.BankSlip;
import com.example.gerador_boleto.repository.BankSlipRepository;

@SpringBootTest
@AutoConfigureRestTestClient
class TestBankSlipControler {

  @Autowired
  RestTestClient restTestClient;

  @Autowired
  BankSlipRepository repository;

  // Criar boleto
  @Test
  void postBankSlipWithoutBodyShouldReturn400() {
    restTestClient.post()
        .uri("/rest/bankslips")
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.BAD_REQUEST); // 400
  }

  @ParameterizedTest
  @MethodSource("provideInvalidRequests")
  @DisplayName("Should reject payloads with 422")
  void postInvalidBankSlipShouldReturn422(BankSlipRequest invalidRequest) {
    restTestClient.post()
        .uri("/rest/bankslips")
        .body(invalidRequest)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
  }

  static Stream<BankSlipRequest> provideInvalidRequests() {
    final var totalInCents = new BigInteger("1234567890");
    final var dueDate = LocalDate.of(2010, 1, 1);
    final var customer = "My Company";

    return Stream.of(
        new BankSlipRequest(null, null, null),
        new BankSlipRequest(dueDate, totalInCents, null),
        new BankSlipRequest(dueDate, totalInCents, ""),
        new BankSlipRequest(dueDate, null, customer),
        new BankSlipRequest(null, totalInCents, customer));
  }

  @Test
  void postBankSlipShouldReturnValidBankSlip() {
    final var totalInCents = new BigInteger("1234567890");
    final var dueDate = LocalDate.now().plusDays(1);
    final var customer = "My Company";
    final var bs = new BankSlipRequest(dueDate, totalInCents, customer);

    final var expectedStatus = BankSlip.Status.PENDING;

    restTestClient.post()
        .uri("/rest/bankslips")
        .body(bs)
        .exchange()
        .expectStatus().isCreated() // 201
        .expectBody()
        .jsonPath("$.total_in_cents").isEqualTo(totalInCents)
        .jsonPath("$.due_date").isEqualTo(dueDate.toString())
        .jsonPath("$.customer").isEqualTo(customer)
        .jsonPath("$.status").isEqualTo(expectedStatus.toString())
        .jsonPath("$.id").value(id -> {
          assertDoesNotThrow(() -> UUID.fromString(id.toString()));
        });

    // check if the new bankslip was correctly created on database
    final var allSlips = repository.findAll();
    assertThat(allSlips).hasSize(1);
    final var dbbs = allSlips.get(0);
    assertThat(dbbs.getTotalInCents()).isEqualTo(totalInCents);
    assertThat(dbbs.getDueDate()).isEqualTo(dueDate);
    assertThat(dbbs.getCustomer()).isEqualTo(customer);
    assertThat(dbbs.getStatus()).isEqualTo(expectedStatus);
    assertThat(dbbs.getId()).isInstanceOf(UUID.class).isNotNull();
  }

  // Lista de boletos
  // Ver detalhes de um boleto
  // Pagar um boleto
  // Cancelar um boleto

}
