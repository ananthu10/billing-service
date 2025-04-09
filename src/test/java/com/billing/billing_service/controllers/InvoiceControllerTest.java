package com.billing.billing_service.controllers;

import com.billing.billing_service.dtos.InvoiceDTO;
import com.billing.billing_service.models.InvoiceStatus;
import com.billing.billing_service.service.InvoiceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceControllerTest {

    @Mock
    private InvoiceServiceImpl invoiceService;

    @InjectMocks
    private InvoiceController invoiceController;

    private InvoiceDTO mockInvoice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockInvoice = new InvoiceDTO();
        mockInvoice.setId(1L);
        mockInvoice.setBillingId("INV-001");
    }

    @Test
    void createInvoice_ShouldReturnCreatedInvoice() {
        when(invoiceService.createInvoice(any())).thenReturn(mockInvoice);

        ResponseEntity<InvoiceDTO> response = invoiceController.createInvoice(mockInvoice);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("INV-001", response.getBody().getBillingId());
    }

    @Test
    void getInvoiceById_ShouldReturnInvoice() {
        when(invoiceService.getInvoiceById(1L)).thenReturn(mockInvoice);

        ResponseEntity<InvoiceDTO> response = invoiceController.getInvoiceById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("INV-001", response.getBody().getBillingId());
    }

    @Test
    void getAllInvoices_WithValidStatus_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<InvoiceDTO> page = new PageImpl<>(Collections.singletonList(mockInvoice));
        when(invoiceService.getInvoicesByCriteria(InvoiceStatus.DRAFT, pageable)).thenReturn(page);

        ResponseEntity<Page<InvoiceDTO>> response = invoiceController.getAllInvoices("DRAFT", pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void getAllInvoices_WithInvalidStatus_ShouldReturnBadRequest() {
        Pageable pageable = PageRequest.of(0, 10);
        ResponseEntity<Page<InvoiceDTO>> response = invoiceController.getAllInvoices("invalid_status", pageable);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getAllInvoices_WithoutStatus_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<InvoiceDTO> page = new PageImpl<>(Collections.singletonList(mockInvoice));
        when(invoiceService.getInvoicesByCriteria(null, pageable)).thenReturn(page);

        ResponseEntity<Page<InvoiceDTO>> response = invoiceController.getAllInvoices(null, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void updateInvoice_ShouldReturnUpdatedInvoice() {
        when(invoiceService.updateInvoice(eq(1L), any())).thenReturn(mockInvoice);

        ResponseEntity<InvoiceDTO> response = invoiceController.updateInvoice(1L, mockInvoice);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("INV-001", response.getBody().getBillingId());
    }

    @Test
    void deleteInvoice_ShouldReturnNoContent() {
        doNothing().when(invoiceService).deleteInvoice(1L);

        ResponseEntity<Void> response = invoiceController.deleteInvoice(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void publishInvoice_ShouldReturnPublishedInvoice() {
        when(invoiceService.publishInvoice(1L)).thenReturn(mockInvoice);

        ResponseEntity<InvoiceDTO> response = invoiceController.publishInvoice(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("INV-001", response.getBody().getBillingId());
    }

    @Test
    void getAll_ShouldReturnAllInvoicesPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<InvoiceDTO> page = new PageImpl<>(Collections.singletonList(mockInvoice));
        when(invoiceService.getAllInvoices(pageable)).thenReturn(page);

        ResponseEntity<Page<InvoiceDTO>> response = invoiceController.getAll(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
    }
}
