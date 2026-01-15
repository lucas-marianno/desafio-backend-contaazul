package com.example.gerador_boleto.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.jspecify.annotations.Nullable;

public class FineService {
  // Observação: Os requisitos da api pedem que o valor do boleto seja em centavos
  // (embora o tipo é um BigDecimal). Não havia informação especifica sobre qual
  // seria o formato de retorno da multa, portanto adotei a mesma especificação do
  // valor do boleto para manter a consistencia.
  @Nullable
  public static BigDecimal calculateFine(final BigDecimal valueInCents, final LocalDate dueDate) {
    final var today = LocalDate.now();
    final var dayOffset = ChronoUnit.DAYS.between(today, dueDate);
    final var halfPercent = new BigDecimal("0.005");
    final var onePercent = new BigDecimal("0.01");

    BigDecimal fine;

    if (dayOffset >= 0) {
      return null;
    } else if (dayOffset >= -10) {
      fine = halfPercent.multiply(valueInCents);
    } else {
      fine = onePercent.multiply(valueInCents);
    }

    // Observação 3: por se tratar de um valor já em centavos, não faria sentido
    // retornar um valor com casas decimais. Assim sendo, embora não seja um
    // requisito do desafio, o valor será arredondado para remover decimais usando o
    // método `RoundingMode.HALF_EVEN` (arredondamento bancário)
    return fine.setScale(0, RoundingMode.HALF_EVEN);
  }

}
