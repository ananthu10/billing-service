package com.billing.billing_service.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceTest {

    private Invoice invoice;

    @BeforeEach
    void setUp() {
        invoice = new Invoice();
        invoice.setBillingLines(new ArrayList<>());
    }

    @Test
    void testSettersAndGetters() {
        invoice.setId(1L);
        invoice.setBillingId("BILL-123");
        invoice.setPaymentDueDate("2025-04-10");
        invoice.setCurrencyCode("USD");
        invoice.setInvoiceDate("2025-04-01");
        invoice.setInvoicingType("STANDARD");
        invoice.setTotalInvoiceLines(5);
        invoice.setTotalInvoiceAmount(1500.0);
        invoice.setTotalInvoiceAmountDue(500.0);
        invoice.setInvoiceLanguageCode("EN");
        invoice.setDepartureDate("2025-04-20");
        invoice.setStatus(InvoiceStatus.DRAFT);

        assertEquals(1L, invoice.getId());
        assertEquals("BILL-123", invoice.getBillingId());
        assertEquals("USD", invoice.getCurrencyCode());
        assertEquals(InvoiceStatus.DRAFT, invoice.getStatus());
    }

    @Test
    void testSetLineItems() {
        BillingLine line1 = mock(BillingLine.class);
        BillingLine line2 = mock(BillingLine.class);

        List<BillingLine> newLines = List.of(line1, line2);

        invoice.setLineItems(newLines);

        assertEquals(2, invoice.getBillingLines().size());
        verify(line1).setInvoice(invoice);
        verify(line2).setInvoice(invoice);
    }

    @Test
    void testSetLineItemsWithNull() {
        invoice.getBillingLines().add(mock(BillingLine.class));

        invoice.setLineItems(null);

        assertTrue(invoice.getBillingLines().isEmpty());
    }

    @Test
    void testRelationships() {
        User buyer = new User();
        buyer.setId(1);

        User seller = new User();
        seller.setId(2);

        PaymentInformation paymentInfo = new PaymentInformation();

        invoice.setBuyer(buyer);
        invoice.setSeller(seller);
        invoice.setPaymentInformation(paymentInfo);

        assertEquals(buyer, invoice.getBuyer());
        assertEquals(seller, invoice.getSeller());
        assertEquals(paymentInfo, invoice.getPaymentInformation());
    }

    @Test
    void testTimestampsInitiallyNull() {
        assertNull(invoice.getCreatedAt());
        assertNull(invoice.getUpdatedAt());
    }
}
