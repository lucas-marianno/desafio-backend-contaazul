package com.example.gerador_boleto;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.stream.Stream;

import com.example.gerador_boleto.dto.BankSlipRequest;
import com.example.gerador_boleto.model.BankSlip;

public class BankSlipTestFactory {
  public static final LocalDate defaultDueDate = LocalDate.now().plusDays(1);
  public static final BigInteger defaultTotalInCents = new BigInteger("1234567890");
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

  static public Stream<BankSlipRequest> provideInvalidRequests() {
    final var invalidDueDate = LocalDate.of(2010, 1, 1);

    return Stream.of(
        new BankSlipRequest(null, null, null),
        new BankSlipRequest(invalidDueDate, defaultTotalInCents, null),
        new BankSlipRequest(invalidDueDate, defaultTotalInCents, ""),
        new BankSlipRequest(invalidDueDate, null, defaultCustomer),
        new BankSlipRequest(null, defaultTotalInCents, defaultCustomer),
        new BankSlipRequest(invalidDueDate, defaultTotalInCents, defaultCustomer));
  }

}
