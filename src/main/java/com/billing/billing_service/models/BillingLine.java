package com.billing.billing_service.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "billing_lines")
@Getter
@Setter
public class BillingLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "product_type")
    private String productType;

    @Column(name = "line_type")
    private String lineType;

    @Column(name = "amount")
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

}