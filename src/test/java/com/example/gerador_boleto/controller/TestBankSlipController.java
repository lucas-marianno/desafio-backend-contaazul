package com.example.gerador_boleto.controller;

import static com.example.gerador_boleto.BankSlipTestFactory.provideMininumValidBankSlip;
import static com.example.gerador_boleto.BankSlipTestFactory.provideValidBankSlipRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
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
class TestBankSlipController {

  @Autowired
  RestTestClient restTestClient;

  @Autowired
  BankSlipRepository repository;

  @AfterEach
  void clearDb() {
    repository.deleteAll();
  }

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
  @MethodSource("com.example.gerador_boleto.BankSlipTestFactory#provideInvalidRequests")
  void postInvalidBankSlipShouldReturn422(final BankSlipRequest invalidRequest) {
    restTestClient.post()
        .uri("/rest/bankslips")
        .body(invalidRequest)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
  }

  @Test
  void postBankSlipShouldReturnValidBankSlip() {
    final var bs = provideValidBankSlipRequest();

    final var expectedStatus = BankSlip.Status.PENDING;

    restTestClient.post()
        .uri("/rest/bankslips")
        .body(bs)
        .exchange()
        .expectStatus().isCreated() // 201
        .expectBody()
        .jsonPath("$.total_in_cents").isEqualTo(bs.totalInCents())
        .jsonPath("$.due_date").isEqualTo(bs.dueDate().toString())
        .jsonPath("$.customer").isEqualTo(bs.customer())
        .jsonPath("$.status").isEqualTo(expectedStatus.toString())
        .jsonPath("$.id").value(id -> assertDoesNotThrow(() -> UUID.fromString(id.toString())));

    // check if the new bankslip was correctly created on database
    final var allSlips = repository.findAll();
    assertThat(allSlips).hasSize(1);

    final var dbbs = allSlips.getFirst();
    assertThat(dbbs.getTotalInCents()).isEqualTo(bs.totalInCents());
    assertThat(dbbs.getDueDate()).isEqualTo(bs.dueDate());
    assertThat(dbbs.getCustomer()).isEqualTo(bs.customer());
    assertThat(dbbs.getStatus()).isEqualTo(expectedStatus);
    assertThat(dbbs.getId()).isInstanceOf(UUID.class).isNotNull();
  }

  // Lista de boletos

  @Test
  void getBankSlipShouldReturnEmptyList() {
    restTestClient.get()
        .uri("/rest/bankslips")
        .exchange()
        .expectStatus().isOk()
        .expectBody().json("[]");
  }

  @Test
  void getBankSlipShouldReturnList() {
    final var bsA = provideMininumValidBankSlip().setCustomer("My Company A");
    final var bsB = provideMininumValidBankSlip().setCustomer("My Company B");

    final var savedBsA = repository.save(bsA);
    final var savedBsB = repository.save(bsB);

    restTestClient.get()
        .uri("/rest/bankslips")
        .exchange()
        .expectStatus().isOk()
        .expectBody(BankSlip[].class)
        .value(bsList -> {
          assertThat(bsList).hasSize(2);
          assertThat(bsList[0]).usingRecursiveComparison().isEqualTo(savedBsA);
          assertThat(bsList[1]).usingRecursiveComparison().isEqualTo(savedBsB);
          assertThat(bsList[0].getId()).isNotEqualTo(bsList[1].getId());
        });
  }

  // Ver detalhes de um boleto
  @Test
  void getBankSlipByIdShould400() {
    restTestClient.get()
        .uri("/rest/bankslips/" + "definitely-not-a-uuid")
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void getBankSlipByIdShouldReturn404() {
    final var rndUuid = UUID.randomUUID();
    restTestClient.get()
        .uri("/rest/bankslips/" + rndUuid)
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void getBankSlipByIdShouldReturnBankSlip() {
    final var savedBankSlip = repository.save(provideMininumValidBankSlip());

    restTestClient.get()
        .uri("/rest/bankslips/" + savedBankSlip.getId())
        .exchange()
        .expectStatus().isOk()
        .expectBody(BankSlip.class)
        .value(returnedBs -> {
          assertThat(returnedBs).usingRecursiveComparison().isEqualTo(savedBankSlip);
        });
  }

  // Pagar um boleto
  // Cancelar um boleto

}
