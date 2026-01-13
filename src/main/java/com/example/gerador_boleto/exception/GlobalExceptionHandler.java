package com.example.gerador_boleto.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.gerador_boleto.exception.exceptions.BankSlipNotFoundException;
import com.example.gerador_boleto.exception.exceptions.InvalidBankslipException;
import com.example.gerador_boleto.exception.exceptions.MissingParameterException;
import com.example.gerador_boleto.exception.exceptions.NotImplementedException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  // custom exceptions
  @ExceptionHandler(NotImplementedException.class)
  ProblemDetail handleNotImplemented(final NotImplementedException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_IMPLEMENTED, e.getMessage());
  }

  @ExceptionHandler(InvalidBankslipException.class)
  ProblemDetail handleInvalidBankslipt(final InvalidBankslipException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, e.getMessage());
  }

  @ExceptionHandler(BankSlipNotFoundException.class)
  ProblemDetail handleBankSlipNotFound(final BankSlipNotFoundException e){
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
  }

  // spring exceptions
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ProblemDetail handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, e.getFieldError().toString());
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  ProblemDetail handleNoParameters(final MissingServletRequestParameterException e) {
    final MissingParameterException newE = new MissingParameterException(e.getParameterName());

    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, newE.getMessage());
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ProblemDetail handleInvalidPostRequest(final HttpMessageNotReadableException e) {
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST,
        e.getMostSpecificCause().getMessage());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  ProblemDetail handleInvalidPostRequest(final DataIntegrityViolationException e) {
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST,
        e.getMostSpecificCause().getMessage());
  }
}
