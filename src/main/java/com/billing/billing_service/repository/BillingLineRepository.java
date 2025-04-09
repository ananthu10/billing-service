package com.billing.billing_service.repository;


import com.billing.billing_service.models.BillingLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BillingLineRepository extends JpaRepository<BillingLine, Long> {

}