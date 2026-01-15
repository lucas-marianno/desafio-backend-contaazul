package com.example.gerador_boleto.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.example.gerador_boleto.model.BankSlip;
import com.example.gerador_boleto.model.BankSlip.Status;

public record BankSlipResponse(
    UUID id,
    LocalDate dueDate,
    LocalDate paymentDate,
    BigDecimal totalInCents,
    String customer,
    Status status,
    BigDecimal fine) {

  public static BankSlipResponse fromBankSlip(BankSlip bankSlip, BigDecimal fineInCents) {
    return new BankSlipResponse(
        bankSlip.getId(),
        bankSlip.getDueDate(),
        bankSlip.getPaymentDate(),
        bankSlip.getTotalInCents(),
        bankSlip.getCustomer(),
        bankSlip.getStatus(),
        fineInCents);
  }

  public static BankSlipResponse fromBankSlip(BankSlip bankSlip) {
    return fromBankSlip(bankSlip, null);
  }
}
