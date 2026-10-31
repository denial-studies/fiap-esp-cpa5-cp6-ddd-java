package br.com.fiap.bank.plataforma.presentation.dto;

public record StatusUpdateRequest(
        String status,
        String parecer,
        String motivo
) {
}

