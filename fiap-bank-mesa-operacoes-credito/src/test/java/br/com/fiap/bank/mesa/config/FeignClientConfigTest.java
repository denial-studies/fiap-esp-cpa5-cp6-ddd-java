package br.com.fiap.bank.mesa.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FeignClientConfigTest {

    private final FeignClientConfig config = new FeignClientConfig();

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void devePropagarCabecalhoAuthorizationPresente() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-teste-12345");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        RequestInterceptor interceptor = config.bearerTokenRequestInterceptor();
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        Collection<String> authHeaders = template.headers().get("Authorization");
        assertNotNull(authHeaders);
        assertTrue(authHeaders.contains("Bearer token-teste-12345"));
    }
}
