package com.billing.billing_service.service;

import com.billing.billing_service.clients.ApprovalServiceClient;
import com.billing.billing_service.dtos.InvoiceDTO;
import com.billing.billing_service.dtos.PaymentInformationDTO;
import com.billing.billing_service.exceptions.InvoiceAlreadyPublishedException;
import com.billing.billing_service.exceptions.InvoiceNotApprovedException;
import com.billing.billing_service.exceptions.InvoiceNotFoundException;
import com.billing.billing_service.models.*;
import com.billing.billing_service.repository.InvoiceRepository;
import com.billing.billing_service.repository.UserRepository;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final ApprovalServiceClient approvalServiceClient;
    private final AuditorAware<User> auditorAware;

    private  static  final String USER_NOT_AUTHENTICATED = "User not authenticated";

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              UserRepository userRepository, ApprovalServiceClient approvalServiceClient,
                              AuditorAware<User> auditorAware) {
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.approvalServiceClient = approvalServiceClient;
        this.auditorAware = auditorAware;
    }

    @Transactional
    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {

        User currentUser = auditorAware.getCurrentAuditor()
                .orElseThrow(() -> new AccessDeniedException(USER_NOT_AUTHENTICATED));
        if (currentUser.getRole().getName().equals(RoleEnum.BUYER)) {
            throw new AccessDeniedException("Only sellers and super admins can create invoices");
        }

        User buyer = userRepository.findById(Math.toIntExact(invoiceDTO.getBuyerId()))
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        User seller = userRepository.findById(Math.toIntExact(invoiceDTO.getSellerId()))
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        Invoice invoice = invoiceDTO.toEntity();
        invoice.setBuyer(buyer);
        invoice.setSeller(seller);
        invoice.setStatus(invoiceDTO.getStatus() != null ? invoiceDTO.getStatus() : InvoiceStatus.DRAFT);

        calculateInvoiceTotals(invoice);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return InvoiceDTO.fromEntity(savedInvoice);
    }

    @Transactional(readOnly = true)
    public InvoiceDTO getInvoiceById(Long id) {
        User user = auditorAware.getCurrentAuditor()
                .orElseThrow(() -> new AccessDeniedException(USER_NOT_AUTHENTICATED));

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        String role = String.valueOf(user.getRole().getName());

        if (!switch (role) {
            case "SUPER_ADMIN" -> true;
            case "SELLER" -> invoice.getSeller().getId().equals(user.getId());
            case "BUYER" -> invoice.getBuyer().getId().equals(user.getId());
            default -> false;
        }) {
            throw new AccessDeniedException("Access denied");
        }

        return InvoiceDTO.fromEntity(invoice);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceDTO> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable).map(InvoiceDTO::fromEntity);
    }

    public InvoiceDTO updateInvoice(Long id, InvoiceDTO invoiceDTO) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(id));

        authorizeBuyerOrSuperAdmin(invoice);

        Invoice updatedInvoice = invoiceDTO.toEntity();

        invoice.setBillingId(updatedInvoice.getBillingId());
        invoice.setPaymentDueDate(updatedInvoice.getPaymentDueDate());
        invoice.setCurrencyCode(updatedInvoice.getCurrencyCode());
        invoice.setInvoiceDate(updatedInvoice.getInvoiceDate());
        invoice.setInvoicingType(updatedInvoice.getInvoicingType());
        invoice.setInvoiceLanguageCode(updatedInvoice.getInvoiceLanguageCode());
        invoice.setDepartureDate(updatedInvoice.getDepartureDate());
        invoice.setPaymentInformation(updatedInvoice.getPaymentInformation());

        List<BillingLine> existingLines = invoice.getBillingLines();
        List<BillingLine> updatedLines = updatedInvoice.getBillingLines();

        Set<Long> updatedIds = updatedLines.stream()
                .map(BillingLine::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<BillingLine> toRemove = existingLines.stream()
                .filter(line -> line.getId() != null && !updatedIds.contains(line.getId()))
                .toList();

        existingLines.removeAll(toRemove);

        for (BillingLine updatedLine : updatedLines) {
            updatedLine.setInvoice(invoice);

            if (updatedLine.getId() == null) {
                existingLines.add(updatedLine);
            } else {
                for (BillingLine existingLine : existingLines) {
                    if (existingLine.getId().equals(updatedLine.getId())) {
                        existingLine.setActionType(updatedLine.getActionType());
                        existingLine.setProductType(updatedLine.getProductType());
                        existingLine.setLineType(updatedLine.getLineType());
                        existingLine.setAmount(updatedLine.getAmount());
                        break;
                    }
                }
            }
        }

        calculateInvoiceTotals(invoice);
        Invoice savedInvoice = invoiceRepository.save(invoice);

        return InvoiceDTO.fromEntity(savedInvoice);
    }


    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(id));
        authorizeBuyerOrSuperAdmin(invoice);
        invoiceRepository.deleteById(id);
    }

    public InvoiceDTO publishInvoice(Long id) {

        User currentUser = auditorAware.getCurrentAuditor()
                .orElseThrow(() -> new AccessDeniedException(USER_NOT_AUTHENTICATED));
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(id));
        String role = currentUser.getRole().getName().name();

        if (!switch (role) {
            case "SUPER_ADMIN" -> true;
            case "SELLER" -> invoice.getSeller().getId().equals(currentUser.getId());
            default -> false;
        }) {
            throw new AccessDeniedException("Only Super Admin or the Seller who created this invoice can publish it.");
        }

        if (InvoiceStatus.PUBLISHED.equals(invoice.getStatus())) {
            throw new InvoiceAlreadyPublishedException("Invoice with ID " + id + " is already published.");
        }

        boolean isApproved = approvalServiceClient.isPaymentApproved(PaymentInformationDTO.fromEntity(invoice.getPaymentInformation()));
        if (!isApproved) {
            throw new InvoiceNotApprovedException("Invoice cannot be published as the payment was not approved.");
        }
        invoice.setStatus(InvoiceStatus.PUBLISHED);
        Invoice savedInvoice = invoiceRepository.save(invoice);

        return InvoiceDTO.fromEntity(savedInvoice);
    }


    private void calculateInvoiceTotals(Invoice invoice) {

        List<com.billing.billing_service.models.BillingLine> billingLines = invoice.getBillingLines();
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalItems = 0;

        for (com.billing.billing_service.models.BillingLine line : billingLines) {
            totalAmount = totalAmount.add(line.getAmount());
            totalItems++;
        }

        invoice.setTotalInvoiceAmount(totalAmount.doubleValue());
        invoice.setTotalInvoiceLines(totalItems);
    }

    public Page<InvoiceDTO> getInvoicesByCriteria(InvoiceStatus status, Pageable pageable) {
        Page<Invoice> invoicePage = invoiceRepository.findInvoicesByCriteria(status, pageable);

        return invoicePage.map(InvoiceDTO::fromEntity);
    }

    public void authorizeBuyerOrSuperAdmin(Invoice invoice) {

        User currentUser = auditorAware.getCurrentAuditor()
                .orElseThrow(() -> new AccessDeniedException(USER_NOT_AUTHENTICATED));
        boolean isSuperAdmin = currentUser.getRole().getName().equals(RoleEnum.SUPER_ADMIN);
        boolean isSeller = invoice.getSeller().getId().equals(currentUser.getId());

        if (!isSuperAdmin && !isSeller) {
            throw new AccessDeniedException("Only the corresponding buyer or a super admin can perform this operation.");
        }
    }
}
