package com.billing.billing_service.clients;

import com.billing.billing_service.dtos.PaymentInformationDTO;

public interface ApprovalServiceClient {
    boolean isPaymentApproved(PaymentInformationDTO paymentInformationDTO);
}