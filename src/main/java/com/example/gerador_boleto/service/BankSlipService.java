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
    return repository.save(BankSlip.builder()
        .dueDate(request.dueDate())
        .totalInCents(request.totalInCents())
        .customer(request.customer())
        .status(BankSlip.Status.PENDING)
        .build());
  }

  public List<BankSlip> findAll() {
    return repository.findAll();
  }

  public BankSlip findById(UUID id) {
    final var found = repository.findById(id);
    if (found.isPresent()) {
      final var bs = found.get();
      final var fine = FineService.calculateFine(
          bs.getTotalInCents(),
          bs.getDueDate());

      return fine == null ? bs : bs.setFine(fine);
    } else {
      throw new BankSlipNotFoundException(
          "Bankslip not found with the specified id: "
              + id.toString());
    }
  }
}
