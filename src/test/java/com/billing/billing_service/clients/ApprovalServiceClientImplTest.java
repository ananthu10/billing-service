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
        // Arrange
        PaymentInformationDTO paymentDTO = new PaymentInformationDTO(); // Add fields if needed
        when(approvalServiceConfig.getUrl()).thenReturn("http://mock-approval-service");
        when(restTemplate.postForEntity(
                eq("http://mock-approval-service"),
                eq(paymentDTO),
                eq(Boolean.class)
        )).thenReturn(ResponseEntity.ok(true));

        // Act
        boolean result = approvalServiceClient.isPaymentApproved(paymentDTO);

        // Assert
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
        PaymentInformationDTO paymentDTO = new PaymentInformationDTO();
        Throwable exception = new RuntimeException("Service down");

        boolean result = approvalServiceClient.fallbackApproval(paymentDTO, exception);

        assertFalse(result);
    }
}
