package com.billing.billing_service.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "invoices")
@Getter
@Setter
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "billing_id", unique = true, nullable = false)
    private String billingId;

    @Column(name = "payment_due_date")
    private String paymentDueDate;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "invoice_date")
    private String invoiceDate;

    @Column(name = "invoicing_type")
    private String invoicingType;

    @Column(name = "total_invoice_lines")
    private Integer totalInvoiceLines;

    @Column(name = "total_invoice_amount")
    private Double totalInvoiceAmount;

    @Column(name = "total_invoice_amount_due")
    private Double totalInvoiceAmountDue;

    @Column(name = "invoice_language_code")
    private String invoiceLanguageCode;

    @Column(name = "departure_date")
    private String departureDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_information_id")
    private PaymentInformation paymentInformation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<BillingLine> billingLines;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    public void setLineItems(List<BillingLine> newItems) {
        this.billingLines.clear();
        if (newItems != null) {
            for (BillingLine item : newItems) {
                item.setInvoice(this);
                this.billingLines.add(item);
            }
        }
    }
}