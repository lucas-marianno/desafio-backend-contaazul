package com.example.gerador_boleto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.gerador_boleto.dto.BankSlipRequest;
import com.example.gerador_boleto.model.BankSlip;
import com.example.gerador_boleto.service.BankSlipService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest")
public class BankSlipController {

  final BankSlipService service;

  @PostMapping("/bankslips")
  ResponseEntity<BankSlip> createBankSlip(@RequestBody @Valid final BankSlipRequest request) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(service.createBankSlip(request));
  }

  @GetMapping("/bankslips")
  ResponseEntity<List<BankSlip>> getAllBankSlip(){
    return ResponseEntity
            .ok(service.findAll());
  }
}
