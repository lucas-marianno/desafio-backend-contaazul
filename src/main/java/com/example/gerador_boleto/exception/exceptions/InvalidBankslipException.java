package com.example.gerador_boleto.exception.exceptions;

public class InvalidBankslipException extends RuntimeException {
  public InvalidBankslipException(String message) {
    super(message);
  }

  public InvalidBankslipException() {
    super("Invalid bankslip provided.The possible reasons are:" +
        "A field of the provided bankslip was null or with invalid values");
  }
}
