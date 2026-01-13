package com.example.gerador_boleto.exception.exceptions;

public class BankSlipNotFoundException extends RuntimeException {

  public BankSlipNotFoundException() {
    super();
  }

  public BankSlipNotFoundException(String message) {
    super(message);
  }
}
