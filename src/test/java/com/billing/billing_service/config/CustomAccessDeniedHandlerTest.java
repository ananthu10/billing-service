package com.billing.billing_service.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomAccessDeniedHandlerTest {

    private CustomAccessDeniedHandler handler;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private AccessDeniedException accessDeniedException;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        handler = new CustomAccessDeniedHandler();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        accessDeniedException = new AccessDeniedException("Access is denied");
        responseWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testHandle_ShouldReturn403WithErrorMessage() throws Exception {
        handler.handle(request, response, accessDeniedException);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType("application/json");

        response.getWriter().flush();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseJson = mapper.readValue(responseWriter.toString(), Map.class);

        assertThat(responseJson).containsKeys("error", "message", "timestamp", "status");
        assertThat(responseJson.get("error")).isEqualTo("Forbidden");
        assertThat(responseJson.get("message")).isEqualTo("You do not have permission to access this resource.");
        assertThat(responseJson.get("status")).isEqualTo(403);
    }
}
