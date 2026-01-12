package com.example.gerador_boleto.repository;

import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import com.example.gerador_boleto.model.BankSlip;

@Repository
public interface BankSlipRepository extends ListCrudRepository<BankSlip, UUID> {
}
