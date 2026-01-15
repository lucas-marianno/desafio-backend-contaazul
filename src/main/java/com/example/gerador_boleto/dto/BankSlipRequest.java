package com.example.gerador_boleto.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BankSlipRequest(
    @NotNull
    @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "uuuu-MM-dd",
      lenient = OptBoolean.FALSE)
    LocalDate dueDate,
    // Os requisitos da API pedem que o tipo de `totalInCents` seja `BigDecimal`.
    // Entretanto, deveria ser `BigInteger`, visto que o valor já está em centavos e
    // isso pode causar confusão para o usuário
    @NotNull @Positive BigDecimal totalInCents,
    @NotBlank String customer) {
}
