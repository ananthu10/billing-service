package com.billing.billing_service.clients;

import com.billing.billing_service.config.ApprovalServiceConfig;
import com.billing.billing_service.dtos.PaymentInformationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApprovalServiceClientImplTest {

    private RestTemplate restTemplate;
    private ApprovalServiceConfig approvalServiceConfig;
    private ApprovalServiceClientImpl approvalServiceClient;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        approvalServiceConfig = mock(ApprovalServiceConfig.class);
        approvalServiceClient = new ApprovalServiceClientImpl(restTemplate, approvalServiceConfig);
    }

    @Test
    void testIsPaymentApproved_ReturnsTrue() {

        PaymentInformationDTO paymentDTO = new PaymentInformationDTO(); // Add fields if needed
        when(approvalServiceConfig.getUrl()).thenReturn("http://mock-approval-service");
        when(restTemplate.postForEntity(
                "http://mock-approval-service",
                paymentDTO, Boolean.class
        )).thenReturn(ResponseEntity.ok(true));

        boolean result = approvalServiceClient.isPaymentApproved(paymentDTO);

        assertTrue(result);
    }

    @Test
    void testIsPaymentApproved_ReturnsFalse() {
        PaymentInformationDTO paymentDTO = new PaymentInformationDTO();
        when(approvalServiceConfig.getUrl()).thenReturn("http://mock-approval-service");
        when(restTemplate.postForEntity(
                anyString(),
                eq(paymentDTO),
                eq(Boolean.class)
        )).thenReturn(ResponseEntity.ok(false));

        boolean result = approvalServiceClient.isPaymentApproved(paymentDTO);

        assertFalse(result);
    }

    @Test
    void testIsPaymentApproved_NullResponseBody() {
        PaymentInformationDTO paymentDTO = new PaymentInformationDTO();
        when(approvalServiceConfig.getUrl()).thenReturn("http://mock-approval-service");
        when(restTemplate.postForEntity(
                anyString(),
                eq(paymentDTO),
                eq(Boolean.class)
        )).thenReturn(ResponseEntity.ok(null));

        boolean result = approvalServiceClient.isPaymentApproved(paymentDTO);

        assertFalse(result);
    }

    @Test
    void testFallbackApproval_ReturnsFalseAndLogsError() {

        Throwable exception = new RuntimeException("Service down");
        boolean result = approvalServiceClient.fallbackApproval(exception);

        assertFalse(result);
    }
}
