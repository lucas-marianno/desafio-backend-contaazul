package com.example.gerador_boleto.exception.exceptions;

public class MissingParameterException extends RuntimeException {
  public MissingParameterException() {
    super("At least one search parameter must be provided.");
  }

  public MissingParameterException(String paramName) {
    super("The parameter '" + paramName + "' is missing");
  }
}
