package br.com.fiap.bank.plataforma.presentation.controller;

import br.com.fiap.bank.plataforma.presentation.dto.AuthResponseDTO;
import br.com.fiap.bank.plataforma.presentation.dto.LoginRequest;
import br.com.fiap.bank.plataforma.presentation.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping({"/login", "/token"})
    public ResponseEntity<AuthResponseDTO> autenticar(@RequestBody LoginRequest request) {
        String cpf = request.cpf() != null ? request.cpf() : "12345678900";
        String conta = request.conta() != null ? request.conta() : "12345-6";
        List<String> roles = (request.roles() != null && !request.roles().isEmpty())
                ? request.roles()
                : List.of("CLIENTE");

        String token = tokenService.gerarToken(cpf, conta, roles);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
