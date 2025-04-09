package com.billing.billing_service.dtos;

import com.billing.billing_service.models.BillingLine;
import com.billing.billing_service.models.Invoice;
import com.billing.billing_service.models.InvoiceStatus;
import com.billing.billing_service.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InvoiceDTO {

    private Long id;
    private Integer buyerId;
    private Integer sellerId;
    private String billingId;
    private String paymentDueDate;
    private String currencyCode;
    private String invoiceDate;
    private InvoiceStatus status;
    private String invoicingType;
    private Integer totalInvoiceLines;
    private Double totalInvoiceAmount;
    private Double totalInvoiceAmountDue;
    private String invoiceLanguageCode;
    private String departureDate;
    private PaymentInformationDTO paymentInformation;
    private List<BillingLineDTO> billingLines;

    public static InvoiceDTO fromEntity(Invoice invoice) {

        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setBuyerId(invoice.getBuyer().getId());
        dto.setSellerId(invoice.getSeller().getId());
        dto.setStatus(invoice.getStatus());
        dto.setBillingId(invoice.getBillingId());
        dto.setPaymentDueDate(invoice.getPaymentDueDate());
        dto.setCurrencyCode(invoice.getCurrencyCode());
        dto.setInvoiceDate(invoice.getInvoiceDate());
        dto.setInvoicingType(invoice.getInvoicingType());
        dto.setTotalInvoiceLines(invoice.getTotalInvoiceLines());
        dto.setTotalInvoiceAmount(invoice.getTotalInvoiceAmount());
        dto.setTotalInvoiceAmountDue(invoice.getTotalInvoiceAmountDue());
        dto.setInvoiceLanguageCode(invoice.getInvoiceLanguageCode());
        dto.setDepartureDate(invoice.getDepartureDate());

        if (invoice.getPaymentInformation() != null) {
            dto.setPaymentInformation(PaymentInformationDTO.fromEntity(invoice.getPaymentInformation()));
        }

        if (invoice.getBillingLines() != null) {
            dto.setBillingLines(invoice.getBillingLines().stream().map(BillingLineDTO::fromEntity).toList());
        }

        return dto;
    }

    public Invoice toEntity() {

        Invoice invoice = new Invoice();
        invoice.setId(this.id);
        invoice.setBillingId(this.billingId);
        invoice.setPaymentDueDate(this.paymentDueDate);
        invoice.setCurrencyCode(this.currencyCode);
        invoice.setStatus(this.getStatus());
        invoice.setInvoiceDate(this.invoiceDate);
        invoice.setInvoicingType(this.invoicingType);
        invoice.setInvoiceLanguageCode(this.invoiceLanguageCode);
        invoice.setDepartureDate(this.departureDate);
        invoice.setBuyer(new User().setId(this.buyerId));
        invoice.setSeller(new User().setId(this.sellerId));

        if (this.paymentInformation != null) {
            invoice.setPaymentInformation(this.paymentInformation.toEntity());
        }

        if (this.getBillingLines() != null) {
            List<BillingLine> lines = new ArrayList<>();
            for (BillingLineDTO dto : this.getBillingLines()) {
                BillingLine line = dto.toEntity();
                line.setInvoice(invoice); // set bidirectional relationship
                lines.add(line);
            }
            invoice.setBillingLines(lines);
        }

        return invoice;
    }
}