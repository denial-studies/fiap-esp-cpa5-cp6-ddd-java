package br.com.fiap.bank.plataforma.presentation.controller;

import br.com.fiap.bank.plataforma.application.dto.AprovarSolicitacaoRequest;
import br.com.fiap.bank.plataforma.application.dto.ReprovarSolicitacaoRequest;
import br.com.fiap.bank.plataforma.application.dto.SolicitacaoCreditoResponseDTO;
import br.com.fiap.bank.plataforma.application.dto.SolicitarCreditoRequest;
import br.com.fiap.bank.plataforma.application.service.PlataformaCreditoService;
import br.com.fiap.bank.plataforma.presentation.dto.StatusUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/solicitacoes")
@CrossOrigin(origins = "*")
public class SolicitacaoCreditoController {

    private final PlataformaCreditoService plataformaService;

    public SolicitacaoCreditoController(PlataformaCreditoService plataformaService) {
        this.plataformaService = plataformaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SCOPE_ROLE_CLIENTE')")
    public SolicitacaoCreditoResponseDTO criarSolicitacao(@Valid @RequestBody SolicitarCreditoRequest request) {
        return plataformaService.solicitarCredito(request);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        if (request != null && "APROVADO".equalsIgnoreCase(request.status())) {
            String parecer = request.parecer() != null ? request.parecer() : request.motivo();
            plataformaService.aprovarSolicitacao(id, parecer);
        } else {
            String motivo = (request != null && request.motivo() != null)
                    ? request.motivo()
                    : (request != null ? request.parecer() : "Reprovado por analise cadastral");
            plataformaService.reprovarSolicitacao(id, motivo);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public List<SolicitacaoCreditoResponseDTO> listarTodas() {
        return plataformaService.listarTodasSolicitacoes();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_CLIENTE') or hasRole('ADMIN') or hasRole('OPERADOR')")
    public SolicitacaoCreditoResponseDTO buscarPorId(@PathVariable Long id) {
        return plataformaService.buscarPorId(id);
    }

    @GetMapping("/cliente/{cpf}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_CLIENTE') or hasRole('ADMIN') or hasRole('OPERADOR')")
    public List<SolicitacaoCreditoResponseDTO> buscarPorCpfCliente(@PathVariable String cpf) {
        return plataformaService.consultarStatusPorCpf(cpf);
    }

    @PatchMapping("/{id}/aprovar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public SolicitacaoCreditoResponseDTO aprovarSolicitacao(@PathVariable Long id,
                                                           @RequestBody(required = false) AprovarSolicitacaoRequest request) {
        String parecer = (request != null) ? request.parecerOperador() : null;
        return plataformaService.aprovarSolicitacao(id, parecer);
    }

    @PatchMapping("/{id}/reprovar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public SolicitacaoCreditoResponseDTO reprovarSolicitacao(@PathVariable Long id,
                                                            @RequestBody(required = false) ReprovarSolicitacaoRequest request) {
        String motivo = (request != null) ? request.motivoReprovacao() : null;
        return plataformaService.reprovarSolicitacao(id, motivo);
    }
}
