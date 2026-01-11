package com.example.gerador_boleto.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gerador_boleto.dto.BankSlipRequest;
import com.example.gerador_boleto.dto.HelloWorldDTO;
import com.example.gerador_boleto.model.BankSlip;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/rest")
public class BankSlipController {

  @SuppressWarnings("deprecation")
  @GetMapping("/hello")
  public ResponseEntity<Map<String, String>> helloWorld(
      @RequestBody(required = false) @Valid HelloWorldDTO helloWorldDTO) {

    var bodyBuilder = ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT);

    return helloWorldDTO == null ? bodyBuilder.build()
        : bodyBuilder.body(Map.of(
            "response", "Hello, " + helloWorldDTO.userName()));
  }

  @PostMapping("/bankslips")
  ResponseEntity<BankSlip> createBoleto(@RequestBody @Valid BankSlipRequest boleto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(boleto.toEntity());
  }
}
