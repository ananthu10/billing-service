package com.billing.billing_service.repository;

import com.billing.billing_service.models.Invoice;
import com.billing.billing_service.models.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvoiceCustomRepository {
    Page<Invoice> findInvoicesByCriteria(InvoiceStatus status, Pageable pageable);
}