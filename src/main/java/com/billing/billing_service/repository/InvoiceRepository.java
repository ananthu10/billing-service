package com.billing.billing_service.repository;

import com.billing.billing_service.models.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>,InvoiceCustomRepository {
    Page<Invoice> findAll(Pageable pageable);

}