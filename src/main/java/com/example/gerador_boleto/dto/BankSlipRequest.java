package com.example.gerador_boleto.dto;

import java.math.BigInteger;
import java.time.LocalDate;

import com.example.gerador_boleto.model.BankSlip;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BankSlipRequest(
    // As especificações da API não exigem que seja uma data futura, mas é seria uma
    // validação interessante de se ter e que evitaria possiveis erros de data de
    // vencimento
    @FutureOrPresent(message = "Due date cannot be in the past")
    @NotNull LocalDate dueDate,
    // As especificações da API pedem que esse valor seja um BigDecimal, mas
    // isso não faz sentido visto que o valor já está em centavos
    @NotNull @Positive BigInteger totalInCents,
    @NotBlank String customer) {
  public BankSlip toEntity() {
    return BankSlip.builder()
        .dueDate(this.dueDate())
        .totalInCents(this.totalInCents())
        .customer(this.customer())
        .status(BankSlip.Status.PENDING)
        .build();
  }
}
