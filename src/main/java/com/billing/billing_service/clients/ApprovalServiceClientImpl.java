package com.billing.billing_service.clients;

import com.billing.billing_service.config.ApprovalServiceConfig;
import com.billing.billing_service.dtos.PaymentInformationDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ApprovalServiceClientImpl implements ApprovalServiceClient {

    private static final Logger logger = LogManager.getLogger(ApprovalServiceClientImpl.class);

    private final RestTemplate restTemplate;
    private final ApprovalServiceConfig approvalServiceConfig;

    public ApprovalServiceClientImpl(RestTemplate restTemplate, ApprovalServiceConfig approvalServiceConfig) {
        this.restTemplate = restTemplate;
        this.approvalServiceConfig = approvalServiceConfig;
    }


    @CircuitBreaker(name = "approvalService", fallbackMethod = "fallbackApproval")
    @Override
    public boolean isPaymentApproved(PaymentInformationDTO paymentInformationDTO) {
        String approvalServiceUrl = approvalServiceConfig.getUrl();
        ResponseEntity<Boolean> response = restTemplate.postForEntity(
                approvalServiceUrl,
                paymentInformationDTO,
                Boolean.class
        );
        return response.getBody() != null && response.getBody();
    }

    public boolean fallbackApproval(Throwable t) {
        logger.error("Approval Service failed: {}", t.getMessage(), t);
        return false;
    }
}