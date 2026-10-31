package br.com.fiap.bank.plataforma.presentation.controller;

import br.com.fiap.bank.plataforma.PlataformaCreditoApplication;
import br.com.fiap.bank.plataforma.presentation.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = PlataformaCreditoApplication.class)
@TestPropertySource(properties = {
        "jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
        "jwt.expiration-seconds=7200",
        "jwt.issuer=fiap-bank-plataforma-credito"
})
class SolicitacaoCreditoSecurityTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TokenService tokenService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void deveRetornar401SemToken() throws Exception {
        mockMvc.perform(get("/api/v1/solicitacoes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar403ParaClienteNaListagemGeral() throws Exception {
        String tokenCliente = tokenService.gerarToken("12345678900", "12345-6", List.of("CLIENTE"));

        mockMvc.perform(get("/api/v1/solicitacoes")
                        .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirListagemGeralParaOperador() throws Exception {
        String tokenOperador = tokenService.gerarToken("98765432100", "0001-9", List.of("OPERADOR"));

        mockMvc.perform(get("/api/v1/solicitacoes")
                        .header("Authorization", "Bearer " + tokenOperador))
                .andExpect(status().isOk());
    }

    @Test
    void devePermitirCriarSolicitacaoComTokenCliente() throws Exception {
        String tokenCliente = tokenService.gerarToken("12345678900", "12345-6", List.of("CLIENTE"));

        String json = """
                {
                    "cpfCliente": "12345678900",
                    "nomeCliente": "Ana Silva",
                    "valorSolicitado": 1000.00,
                    "quantidadeParcelas": 12,
                    "rendaMensal": 5000.00,
                    "tipoCredito": "EMPRESTIMO_PESSOAL"
                }
                """;

        mockMvc.perform(post("/api/v1/solicitacoes")
                        .header("Authorization", "Bearer " + tokenCliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void deveBloquearAtualizacaoStatusParaCliente() throws Exception {
        String tokenCliente = tokenService.gerarToken("12345678900", "12345-6", List.of("CLIENTE"));

        String json = """
                {
                    "status": "APROVADO",
                    "parecer": "Tentativa de alteracao indevida"
                }
                """;

        mockMvc.perform(put("/api/v1/solicitacoes/1/status")
                        .header("Authorization", "Bearer " + tokenCliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirAtualizacaoStatusParaOperador() throws Exception {
        String tokenOperador = tokenService.gerarToken("98765432100", "0001-9", List.of("OPERADOR"));

        String json = """
                {
                    "status": "APROVADO",
                    "parecer": "Proposta aprovada pelo analista"
                }
                """;

        mockMvc.perform(put("/api/v1/solicitacoes/1/status")
                        .header("Authorization", "Bearer " + tokenOperador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}
