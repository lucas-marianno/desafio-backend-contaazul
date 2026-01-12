package com.example.gerador_boleto.model;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@ToString
@Table(name = "bank_slip")
public class BankSlip {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  UUID id;

  @Column(nullable = false)
  LocalDate dueDate;

  @Column(nullable = false)
  BigInteger totalInCents;

  @Column(nullable = false)
  String customer;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  Status status;

  public enum Status {
    PENDING, PAID, CANCELED
  }
}
