package br.com.fiap.bank.plataforma.presentation.controller;

import br.com.fiap.bank.plataforma.application.dto.*;
import br.com.fiap.bank.plataforma.application.service.PlataformaCreditoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/credito")
@CrossOrigin(origins = "*")
public class CreditoOperacoesNonRestfulController {

    private final PlataformaCreditoService plataformaService;

    public CreditoOperacoesNonRestfulController(PlataformaCreditoService plataformaService) {
        this.plataformaService = plataformaService;
    }

    /**
     * Endpoint para Inclusoes de solicitacao de credito via POST (Acao: solicitarCredito)
     */
    @PostMapping("/solicitarCredito")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_CLIENTE')")
    public ResponseEntity<SolicitacaoCreditoResponseDTO> solicitarCredito(@RequestBody SolicitarCreditoRequest request) {
        return ResponseEntity.ok(plataformaService.solicitarCredito(request));
    }

    /**
     * Endpoint para Consultas de status via POST com corpo da requisicao (Acao: consultarStatusCredito)
     */
    @PostMapping("/consultarStatusCredito")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_CLIENTE') or hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<SolicitacaoCreditoResponseDTO>> consultarStatusCredito(@RequestBody ConsultarStatusRequest request) {
        return ResponseEntity.ok(plataformaService.consultarStatusPorCpf(request));
    }

    /**
     * Endpoint para Busca por ID via POST (Acao: buscarSolicitacaoPorId)
     */
    @PostMapping("/buscarSolicitacaoPorId")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_CLIENTE') or hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<SolicitacaoCreditoResponseDTO> buscarSolicitacaoPorId(@RequestBody BuscarSolicitacaoRequest request) {
        return ResponseEntity.ok(plataformaService.buscarPorId(request));
    }

    /**
     * Endpoint para Listagem de todas as solicitacoes via POST (Acao: listarTodasSolicitacoes)
     */
    @PostMapping("/listarTodasSolicitacoes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<List<SolicitacaoCreditoResponseDTO>> listarTodasSolicitacoes() {
        return ResponseEntity.ok(plataformaService.listarTodasSolicitacoes());
    }

    /**
     * Endpoint para Atualizacao/Aprovacao via POST (Acao: aprovarSolicitacao)
     */
    @PostMapping("/aprovarSolicitacao")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<SolicitacaoCreditoResponseDTO> aprovarSolicitacao(@RequestBody AprovarSolicitacaoRequest request) {
        return ResponseEntity.ok(plataformaService.aprovarSolicitacao(request));
    }

    /**
     * Endpoint para Atualizacao/Reprovacao via POST (Acao: reprovarSolicitacao)
     */
    @PostMapping("/reprovarSolicitacao")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<SolicitacaoCreditoResponseDTO> reprovarSolicitacao(@RequestBody ReprovarSolicitacaoRequest request) {
        return ResponseEntity.ok(plataformaService.reprovarSolicitacao(request));
    }

    /**
     * Endpoint para Consulta ao Core Bancario via POST (Acao: consultarClienteCore)
     */
    @PostMapping("/consultarClienteCore")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_CLIENTE') or hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<Map<String, Object>> consultarClienteCore(@RequestBody ConsultarStatusRequest request) {
        return ResponseEntity.ok(plataformaService.consultarContaCliente(request.cpfCliente()));
    }
}
