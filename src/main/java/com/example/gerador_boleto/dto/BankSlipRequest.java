package com.example.gerador_boleto.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BankSlipRequest(
    @NotNull LocalDate dueDate,
    @NotNull @Positive BigDecimal totalInCents,
    @NotBlank String customer) {
}
