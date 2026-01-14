package com.example.gerador_boleto.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.jspecify.annotations.Nullable;

public class FineService {
  @Nullable
  public static BigDecimal calculateFine(BigDecimal valueInCents, LocalDate dueDate) {
    final var today = LocalDate.now();
    final var dayOffset = ChronoUnit.DAYS.between(today, dueDate);
    final var halfPercent = new BigDecimal("0.005");
    final var onePercent = new BigDecimal("0.01");

    if (dayOffset >= 0) {
      return null;
    } else if (dayOffset >= -10) {
      return halfPercent.multiply(valueInCents);
    } else {
      return onePercent.multiply(valueInCents);
    }
  }

  @Override
  public String toString() {
    return "FineService []";
  }

}
