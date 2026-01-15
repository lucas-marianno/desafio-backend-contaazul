package com.example.gerador_boleto.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gerador_boleto.dto.BankSlipRequest;
import com.example.gerador_boleto.dto.PaymentRequest;
import com.example.gerador_boleto.dto.BankSlipResponse;
import com.example.gerador_boleto.service.BankSlipService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest")
public class BankSlipController {

  final BankSlipService service;

  @PostMapping("/bankslips")
  ResponseEntity<BankSlipResponse> createBankSlip(
      @RequestBody @Valid final BankSlipRequest request) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(service.createBankSlip(request));
  }

  @GetMapping("/bankslips")
  ResponseEntity<List<BankSlipResponse>> getAllBankSlip() {
    return ResponseEntity.ok(service.findAll());
  }

  @GetMapping("/bankslips/{id}")
  ResponseEntity<BankSlipResponse> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(service.getDetails(id));
  }

  @PostMapping("/bankslips/{id}/payments")
  ResponseEntity<Void> payBankSlip(
      @PathVariable UUID id,
      @Valid @RequestBody PaymentRequest paymentRequest) {

    service.payBankSlip(id, paymentRequest.paymentDate());
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/bankslips/{id}")
  ResponseEntity<Void> cancelBankSlip(@PathVariable UUID id) {
    service.cancelBankSlip(id);
    return ResponseEntity.noContent().build();
  }
}
