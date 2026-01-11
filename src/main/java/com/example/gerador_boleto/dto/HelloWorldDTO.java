package com.example.gerador_boleto.dto;

import jakarta.validation.constraints.NotBlank;

public record HelloWorldDTO(
    @NotBlank String userName) {
}
