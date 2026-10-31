package br.com.fiap.bank.plataforma.presentation.service;

import br.com.fiap.bank.plataforma.presentation.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {SecurityConfig.class, TokenService.class})
@TestPropertySource(properties = {
        "jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
        "jwt.expiration-seconds=7200",
        "jwt.issuer=fiap-bank-plataforma-credito"
})
class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Test
    void deveGerarTokenClienteValido() {
        String cpf = "12345678900";
        String conta = "12345-6";
        List<String> roles = List.of("CLIENTE");

        String token = tokenService.gerarToken(cpf, conta, roles);
        assertNotNull(token);
        assertFalse(token.isBlank());

        Jwt jwt = jwtDecoder.decode(token);
        assertEquals(cpf, jwt.getSubject());
        assertEquals("fiap-bank-plataforma-credito", jwt.getClaimAsString("iss"));
        assertEquals(cpf, jwt.getClaimAsString("cpf"));
        assertEquals(conta, jwt.getClaimAsString("conta"));
        assertEquals("ROLE_CLIENTE", jwt.getClaimAsString("scope"));
        assertTrue(jwt.getClaimAsStringList("roles").contains("CLIENTE"));
        assertNotNull(jwt.getExpiresAt());
    }

    @Test
    void deveGerarTokenOperadorValido() {
        String cpf = "98765432100";
        String conta = "0001-9";
        List<String> roles = List.of("ADMIN", "OPERADOR");

        String token = tokenService.gerarToken(cpf, conta, roles);
        assertNotNull(token);

        Jwt jwt = jwtDecoder.decode(token);
        assertEquals(cpf, jwt.getSubject());
        List<String> tokenRoles = jwt.getClaimAsStringList("roles");
        assertTrue(tokenRoles.contains("ADMIN"));
        assertTrue(tokenRoles.contains("OPERADOR"));
    }
}
