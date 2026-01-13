package com.example.gerador_boleto.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.gerador_boleto.dto.BankSlipRequest;
import com.example.gerador_boleto.exception.exceptions.BankSlipNotFoundException;
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

  public List<BankSlip> findAll() {
    return repository.findAll();
  }

  public BankSlip findById(UUID id) {
    return repository
        .findById(id)
        .orElseThrow(
            () -> new BankSlipNotFoundException("Bankslip not found with the specified id: " + id.toString()));
  }
}
