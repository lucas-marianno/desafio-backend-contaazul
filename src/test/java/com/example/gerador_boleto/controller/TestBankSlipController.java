package com.example.gerador_boleto.controller;

import static com.example.gerador_boleto.BankSlipTestFactory.provideExpiredValidBankSlip;
import static com.example.gerador_boleto.BankSlipTestFactory.provideMininumValidBankSlip;
import static com.example.gerador_boleto.BankSlipTestFactory.provideValidBankSlipRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.example.gerador_boleto.BankSlipTestFactory;
import com.example.gerador_boleto.dto.BankSlipResponse;
import com.example.gerador_boleto.dto.BankSlipRequest;
import com.example.gerador_boleto.model.BankSlip;
import com.example.gerador_boleto.repository.BankSlipRepository;
import com.example.gerador_boleto.service.FineService;

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

  @Test
  void getBankSlipByIdShouldReturnBankSlip2() {
    final var value = new BigDecimal("99000");
    final var savedBankSlip = repository.save(provideExpiredValidBankSlip(value));
    final var fine = FineService.calculateFine(value, savedBankSlip.getDueDate());

    restTestClient.get()
        .uri("/rest/bankslips/" + savedBankSlip.getId())
        .exchange()
        .expectStatus().isOk()
        .expectBody(BankSlip.class)
        .value(returnedBs -> {
          assertThat(returnedBs)
              .usingRecursiveComparison()
              .isEqualTo(
                  BankSlipResponse.fromBankSlip(
                      savedBankSlip,
                      fine));
        });
  }

  // Pagar um boleto

  @ParameterizedTest
  @MethodSource("com.example.gerador_boleto.BankSlipTestFactory#provideInvalidPayRequestJsonBody")
  void payBankSlipWithInvalidBodyShouldReturn4xx(String jsonBody) {
    final String id = UUID.randomUUID().toString();
    final String uri = "/rest/bankslips/%s/payments".formatted(id);
    restTestClient.post()
        .uri(uri)
        .contentType(MediaType.APPLICATION_JSON)
        .body(jsonBody)
        .exchange()
        .expectStatus().is4xxClientError();
  }

  @Test
  void payBankSlipWithInvalidIdShouldReturn400() {
    final String id = "invalid-uuid";
    restTestClient.post()
        .uri("/rest/bankslips/%s/payments".formatted(id))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void payBankSlipWithRandomIdShouldReturn404() {
    final String id = UUID.randomUUID().toString();
    final String uri = "/rest/bankslips/%s/payments".formatted(id);
    final String body = BankSlipTestFactory.provideValidPayRequestJsonBody();

    restTestClient.post()
        .uri(uri)
        .contentType(MediaType.APPLICATION_JSON)
        .body(body)
        .exchange()
        .expectStatus().is4xxClientError();
  }

  @Test
  void payBankSlipShouldReturn204AndUpdateDataBase() {
    final var savedBankSlip = repository.save(provideMininumValidBankSlip());

    final String id = savedBankSlip.getId().toString();
    final String uri = "/rest/bankslips/%s/payments".formatted(id);
    final String body = BankSlipTestFactory.provideValidPayRequestJsonBody();

    restTestClient.post()
        .uri(uri)
        .contentType(MediaType.APPLICATION_JSON)
        .body(body)
        .exchange()
        .expectStatus().isNoContent();

    final var found = repository.findById(savedBankSlip.getId());

    assertThat(found).isPresent();
    final var foundBs = found.get();
    assertThat(foundBs.getStatus()).isEqualTo(BankSlip.Status.PAID);
    assertThat(foundBs.getPaymentDate()).isNotNull();
  }
  // Cancelar um boleto

  @Test
  void cancelBankSlipWithInvalidIdShouldReturn400() {
    final String id = "invalid-uuid";
    final String uri = "/rest/bankslips/" + id;

    restTestClient.delete()
        .uri(uri)
        .exchange()
        .expectStatus().isBadRequest();

  }

  @Test
  void cancelBankSlipWithRandomIdShouldReturn404() {
    final String id = UUID.randomUUID().toString();
    final String uri = "/rest/bankslips/" + id;

    restTestClient.delete()
        .uri(uri)
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  void cancelBankSlipWithValidIdShouldReturn204AndUpdateDataBase() {
    final var bs = repository.save(provideMininumValidBankSlip());

    final String id = bs.getId().toString();
    final String uri = "/rest/bankslips/" + id;

    restTestClient.delete()
        .uri(uri)
        .exchange()
        .expectStatus().isNoContent();

    final var found = repository.findById(bs.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getStatus())
        .isEqualTo(BankSlip.Status.CANCELED);
  }

}
