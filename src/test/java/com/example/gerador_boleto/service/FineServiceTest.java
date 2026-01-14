package com.example.gerador_boleto.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testing fines Service")
public class FineServiceTest {

  @Test
  void shouldReturnNull() {
    final var futureDueDate = LocalDate.now().plusDays(1);
    final var valueinCents = new BigDecimal("1234567890");

    final var response = FineService.calculateFine(valueinCents, futureDueDate);

    assertThat(response).isNull();
  }

  @Test
  void shouldIncreaseByHalfPercent() {
    final var pastDueDate = LocalDate.now().minusDays(5);
    final var valueinCents = new BigDecimal("1000");
    final var expectedNewValue = new BigDecimal("5");

    final var response = FineService.calculateFine(valueinCents, pastDueDate);

    assertThat(response)
        .isNotNull()
        .actual()
        .equals(expectedNewValue);
  }

  @Test
  void shouldIncreaseByHalfPercent2() {
    final var pastDueDate = LocalDate.now().minusDays(10);
    final var valueinCents = new BigDecimal("1000");
    final var expectedNewValue = new BigDecimal("5");

    final var response = FineService.calculateFine(valueinCents, pastDueDate);

    assertThat(response)
        .isNotNull()
        .actual()
        .equals(expectedNewValue);
  }

  @Test
  void shouldIncreaseByOnePercent() {
    final var pastDueDate = LocalDate.now().minusDays(15);
    final var valueinCents = new BigDecimal("1000");
    final var expectedNewValue = new BigDecimal("10");

    final var response = FineService.calculateFine(valueinCents, pastDueDate);

    assertThat(response)
        .isNotNull()
        .actual()
        .equals(expectedNewValue);
  }

  @Test
  void shouldIncreaseByOnePercent2() {
    final var pastDueDate = LocalDate.of(2018, 10, 05);
    final var valueinCents = new BigDecimal("1000");
    final var expectedNewValue = new BigDecimal("10");

    final var response = FineService.calculateFine(valueinCents, pastDueDate);

    assertThat(response)
        .isNotNull()
        .actual()
        .equals(expectedNewValue);
  }

  @Test
  void shouldIncreaseByOnePercent3() {
    final var pastDueDate = LocalDate.now().minusYears(4);
    final var valueinCents = new BigDecimal("1000");
    final var expectedNewValue = new BigDecimal("10");

    final var response = FineService.calculateFine(valueinCents, pastDueDate);

    assertThat(response)
        .isNotNull()
        .actual()
        .equals(expectedNewValue);
  }
}
