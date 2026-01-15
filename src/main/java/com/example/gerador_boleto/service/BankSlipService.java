package com.example.gerador_boleto.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.gerador_boleto.dto.BankSlipResponse;
import com.example.gerador_boleto.dto.BankSlipRequest;
import com.example.gerador_boleto.exception.exceptions.BankSlipNotFoundException;
import com.example.gerador_boleto.model.BankSlip;
import com.example.gerador_boleto.repository.BankSlipRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankSlipService {

  private final BankSlipRepository repository;

  public BankSlipResponse createBankSlip(BankSlipRequest request) {
    final var saved = repository.save(BankSlip.builder()
        .dueDate(request.dueDate())
        .totalInCents(request.totalInCents())
        .customer(request.customer())
        .status(BankSlip.Status.PENDING)
        .build());

    return BankSlipResponse.fromBankSlip(saved);
  }

  public List<BankSlipResponse> findAll() {
    return repository
        .findAll()
        .stream()
        .map(b -> BankSlipResponse.fromBankSlip(b))
        .toList();
  }

  private BankSlip findById(UUID id) {
    final var found = repository.findById(id);
    if (!found.isPresent())
      throw new BankSlipNotFoundException("Bankslip not found with the specified id");

    return found.get();
  }

  public BankSlipResponse getDetails(UUID id) {
    final var bs = findById(id);

    if (bs.getStatus() != BankSlip.Status.PENDING)
      return BankSlipResponse.fromBankSlip(bs);

    final var fine = FineService.calculateFine(
        bs.getTotalInCents(),
        bs.getDueDate());

    return BankSlipResponse.fromBankSlip(bs, fine);
  }

  public void payBankSlip(UUID id, LocalDate paymentDate) {
    final var bankSlip = findById(id);

    if (bankSlip.getStatus() != BankSlip.Status.PENDING)
      throw new IllegalStateException("Only PENDING bank slips can be paid.");

    bankSlip
        .setPaymentDate(paymentDate)
        .setStatus(BankSlip.Status.PAID);
    repository.save(bankSlip);
  }

  public void cancelBankSlip(UUID id) {
    final var bankSlip = findById(id);

    if (bankSlip.getStatus() != BankSlip.Status.PENDING)
      throw new IllegalStateException("Only PENDING bank slips can be canceled.");

    bankSlip.setStatus(BankSlip.Status.CANCELED);
    repository.save(bankSlip);
  }
}
