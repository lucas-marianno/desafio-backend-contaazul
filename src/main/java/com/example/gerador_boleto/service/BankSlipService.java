package com.example.gerador_boleto.service;

import org.springframework.stereotype.Service;

import com.example.gerador_boleto.dto.BankSlipRequest;
import com.example.gerador_boleto.model.BankSlip;
import com.example.gerador_boleto.repository.BankSlipRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankSlipService {

  private final BankSlipRepository repository;

  public BankSlip createBankSlip(BankSlipRequest request) {
    BankSlip bankSlip = request.toEntity();
    return repository.save(bankSlip);
  }

}
