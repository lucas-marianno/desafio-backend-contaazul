package com.example.gerador_boleto.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;

import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
  @NotNull
  @JsonFormat(
    shape = JsonFormat.Shape.STRING,
    pattern = "uuuu-MM-dd",
    lenient = OptBoolean.FALSE
  )
  LocalDate paymentDate) {
}
