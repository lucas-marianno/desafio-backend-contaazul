package com.example.gerador_boleto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import com.example.gerador_boleto.dto.BankSlipRequest;
import com.example.gerador_boleto.model.BankSlip;

public class BankSlipTestFactory {
  public static final LocalDate defaultDueDate = LocalDate.now().plusDays(1);
  public static final BigDecimal defaultTotalInCents = new BigDecimal("1234567890");
  public static final String defaultCustomer = "valid customer";
  public static final BankSlip.Status defaultStatus = BankSlip.Status.PENDING;

  static public BankSlipRequest provideValidBankSlipRequest() {
    return new BankSlipRequest(
        defaultDueDate,
        defaultTotalInCents,
        defaultCustomer);
  }

  static public BankSlip provideMininumValidBankSlip() {
    return BankSlip.builder()
        .customer(defaultCustomer)
        .dueDate(defaultDueDate)
        .totalInCents(defaultTotalInCents)
        .status(defaultStatus)
        .build();
  }

  static public BankSlip provideExpiredValidBankSlip() {
    return provideExpiredValidBankSlip(defaultTotalInCents);
  }
  static public BankSlip provideExpiredValidBankSlip(BigDecimal valueInCents) {
    final var pastDueDate = LocalDate.now().minusYears(5);
    return BankSlip.builder()
        .customer(defaultCustomer)
        .dueDate(pastDueDate)
        .totalInCents(valueInCents)
        .status(defaultStatus)
        .build();
  }
  static public Stream<BankSlipRequest> provideInvalidRequests() {
    return Stream.of(
        new BankSlipRequest(null, null, null),
        new BankSlipRequest(defaultDueDate, defaultTotalInCents, null),
        new BankSlipRequest(defaultDueDate, defaultTotalInCents, ""),
        new BankSlipRequest(defaultDueDate, null, defaultCustomer),
        new BankSlipRequest(null, defaultTotalInCents, defaultCustomer));
  }

}
