package com.billing.billing_service.controllers;


import com.billing.billing_service.dtos.InvoiceDTO;
import com.billing.billing_service.models.InvoiceStatus;
import com.billing.billing_service.service.InvoiceServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final InvoiceServiceImpl invoiceService;

    public InvoiceController(InvoiceServiceImpl invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    @PostAuthorize("hasAnyRole('SUPER_ADMIN','SELLER')")
    public ResponseEntity<InvoiceDTO> createInvoice(@RequestBody InvoiceDTO invoiceDTO) {
        return new ResponseEntity<>(invoiceService.createInvoice(invoiceDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping
    public ResponseEntity<Page<InvoiceDTO>> getAllInvoices(
            @RequestParam(required = false) String status,
            @PageableDefault(page = 0, size = 10, sort = "invoiceDate", direction = Sort.Direction.DESC) Pageable pageable) {

        InvoiceStatus invoiceStatus = null;

        if (status != null && !status.isBlank()) {
            try {
                invoiceStatus = InvoiceStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ex) {
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.ok(invoiceService.getInvoicesByCriteria(invoiceStatus, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDTO> updateInvoice(@PathVariable Long id, @RequestBody InvoiceDTO invoiceDTO) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, invoiceDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SELLER')")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SELLER')")
    public ResponseEntity<InvoiceDTO> publishInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.publishInvoice(id));
    }

    @GetMapping("/list")
    public ResponseEntity<Page<InvoiceDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(invoiceService.getAllInvoices(pageable));
    }
}
