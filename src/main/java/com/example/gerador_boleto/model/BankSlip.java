package com.example.gerador_boleto.model;

import java.math.BigInteger;
import java.time.LocalDate;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Table
public record BankSlip(
    @Id Integer uuid,
    @NotNull LocalDate dueDate,
    @NotNull BigInteger totalInCents,
    @NotBlank String customer,
    @NotNull Status status) {

  public enum Status {
    PENDING, PAID, CANCELED
  }
}
