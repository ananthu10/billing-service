package com.billing.billing_service.dtos;


import com.billing.billing_service.models.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceDTOTest {

    @Test
    void testFromEntity() {

        User buyer = new User();
        buyer.setId(1);

        User seller = new User();
        seller.setId(2);

        Invoice invoice = getInvoice(buyer, seller);

        InvoiceDTO dto = InvoiceDTO.fromEntity(invoice);

        assertEquals(invoice.getId(), dto.getId());
        assertEquals(invoice.getBillingId(), dto.getBillingId());
        assertEquals(invoice.getBuyer().getId(), dto.getBuyerId());
        assertEquals(invoice.getSeller().getId(), dto.getSellerId());
        assertEquals(invoice.getStatus(), dto.getStatus());
        assertEquals(invoice.getBillingLines().size(), dto.getBillingLines().size());
        assertEquals(invoice.getPaymentInformation().getShortCardNum(), dto.getPaymentInformation().getShortCardNum());
    }

    private static Invoice getInvoice(User buyer, User seller) {
        PaymentInformation paymentInfo = new PaymentInformation();
        paymentInfo.setShortCardNum("123456");

        BillingLine line1 = new BillingLine();
        line1.setId(10L);

        BillingLine line2 = new BillingLine();
        line2.setId(20L);

        Invoice invoice = new Invoice();
        invoice.setId(100L);
        invoice.setBillingId("BILL-123");
        invoice.setPaymentDueDate("2025-04-01");
        invoice.setCurrencyCode("USD");
        invoice.setInvoiceDate("2025-03-30");
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setInvoicingType("REGULAR");
        invoice.setTotalInvoiceLines(2);
        invoice.setTotalInvoiceAmount(2000.0);
        invoice.setTotalInvoiceAmountDue(500.0);
        invoice.setInvoiceLanguageCode("EN");
        invoice.setDepartureDate("2025-04-15");
        invoice.setPaymentInformation(paymentInfo);
        invoice.setBillingLines(List.of(line1, line2));
        invoice.setBuyer(buyer);
        invoice.setSeller(seller);
        return invoice;
    }

    @Test
    void testToEntity() {

        BillingLineDTO lineDTO1 = new BillingLineDTO();
        lineDTO1.setId(1L);

        BillingLineDTO lineDTO2 = new BillingLineDTO();
        lineDTO2.setId(2L);

        PaymentInformationDTO paymentDTO = new PaymentInformationDTO();

        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(101L);
        dto.setBillingId("INV-2025-001");
        dto.setPaymentDueDate("2025-04-05");
        dto.setCurrencyCode("EUR");
        dto.setInvoiceDate("2025-03-28");
        dto.setStatus(InvoiceStatus.PUBLISHED);
        dto.setInvoicingType("CREDIT");
        dto.setTotalInvoiceLines(2);
        dto.setTotalInvoiceAmount(1500.0);
        dto.setTotalInvoiceAmountDue(300.0);
        dto.setInvoiceLanguageCode("FR");
        dto.setDepartureDate("2025-04-10");
        dto.setBuyerId(11);
        dto.setSellerId(22);
        dto.setPaymentInformation(paymentDTO);
        dto.setBillingLines(List.of(lineDTO1, lineDTO2));

        Invoice invoice = dto.toEntity();

        assertEquals(dto.getId(), invoice.getId());
        assertEquals(dto.getBillingId(), invoice.getBillingId());
        assertEquals(dto.getStatus(), invoice.getStatus());
        assertEquals(dto.getBuyerId(), invoice.getBuyer().getId());
        assertEquals(dto.getSellerId(), invoice.getSeller().getId());
        assertNotNull(invoice.getPaymentInformation());
        assertEquals(2, invoice.getBillingLines().size());
    }
}
