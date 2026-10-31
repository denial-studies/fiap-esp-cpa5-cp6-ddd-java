package br.com.fiap.bank.plataforma.presentation.dto;

import java.util.List;

public record LoginRequest(
        String cpf,
        String conta,
        List<String> roles
) {
}
