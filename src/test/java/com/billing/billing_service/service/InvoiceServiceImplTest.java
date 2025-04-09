package com.billing.billing_service.service;

import com.billing.billing_service.clients.ApprovalServiceClient;
import com.billing.billing_service.dtos.InvoiceDTO;
import com.billing.billing_service.dtos.PaymentInformationDTO;
import com.billing.billing_service.exceptions.InvoiceAlreadyPublishedException;
import com.billing.billing_service.exceptions.InvoiceNotApprovedException;
import com.billing.billing_service.models.*;
import com.billing.billing_service.repository.InvoiceRepository;
import com.billing.billing_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceServiceImplTest {

    @Mock private InvoiceRepository invoiceRepository;
    @Mock private UserRepository userRepository;
    @Mock private ApprovalServiceClient approvalServiceClient;
    @Mock private AuditorAware<User> auditorAware;
    @InjectMocks private InvoiceServiceImpl invoiceService;

    private User superAdmin;
    private User seller;
    private User buyer;
    private Invoice invoice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Role superAdminRole = new Role().setName(RoleEnum.SUPER_ADMIN);
        Role sellerRole = new Role().setName(RoleEnum.SELLER);
        Role buyerRole = new Role().setName(RoleEnum.BUYER);

        superAdmin = new User().setId(1).setFullName("admin").setRole(superAdminRole);
        seller = new User().setId(2).setFullName("seller").setRole(sellerRole);
        buyer = new User().setId(3).setFullName("buyer").setRole(buyerRole);

        invoice = new Invoice();
        invoice.setId(1L);
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setSeller(seller);
        invoice.setBuyer(buyer);
        invoice.setPaymentInformation(new PaymentInformation());
        BillingLine billingLine= new BillingLine();
        billingLine.setId(1L);
        billingLine.setActionType("ACTION");
        billingLine.setLineType("TYPE");
        billingLine.setLineType("LINE");
        billingLine.setAmount( new BigDecimal("100.00"));

        invoice.setBillingLines(List.of( billingLine));
    }

    @Test
    void testCreateInvoice_AsSuperAdmin_Success() {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setBuyerId(buyer.getId());
        dto.setSellerId(seller.getId());
        dto.setBillingLines(new ArrayList<>());

        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(superAdmin));
        when(userRepository.findById(dto.getBuyerId().intValue())).thenReturn(Optional.of(buyer));
        when(userRepository.findById(dto.getSellerId().intValue())).thenReturn(Optional.of(seller));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        InvoiceDTO result = invoiceService.createInvoice(dto);
        assertNotNull(result);
        assertEquals(invoice.getId(), result.getId());
    }

    @Test
    void testCreateInvoice_AsBuyer_ThrowsAccessDenied() {
        InvoiceDTO dto = new InvoiceDTO();
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(buyer));

        assertThrows(AccessDeniedException.class, () -> invoiceService.createInvoice(dto));
    }

    @Test
    void testGetInvoiceById_AsSeller_Success() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(seller));
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        InvoiceDTO dto = invoiceService.getInvoiceById(1L);
        assertEquals(invoice.getId(), dto.getId());
    }

    @Test
    void testGetInvoiceById_AsWrongBuyer_ThrowsAccessDenied() {
        User randomUser = new User().setRole(new Role().setName(RoleEnum.BUYER)).setFullName("intruder").setId(9);
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(randomUser));
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        assertThrows(AccessDeniedException.class, () -> invoiceService.getInvoiceById(1L));
    }

    @Test
    void testPublishInvoice_NotApproved_ThrowsInvoiceNotApprovedException() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(seller));
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(approvalServiceClient.isPaymentApproved(any(PaymentInformationDTO.class))).thenReturn(false);

        assertThrows(InvoiceNotApprovedException.class, () -> invoiceService.publishInvoice(1L));
    }

    @Test
    void testPublishInvoice_AlreadyPublished_ThrowsException() {
        invoice.setStatus(InvoiceStatus.PUBLISHED);

        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(seller));
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        assertThrows(InvoiceAlreadyPublishedException.class, () -> invoiceService.publishInvoice(1L));
    }

    @Test
    void testPublishInvoice_AsSeller_Approved_Success() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(seller));
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(approvalServiceClient.isPaymentApproved(any())).thenReturn(true);
        when(invoiceRepository.save(any())).thenReturn(invoice);

        InvoiceDTO dto = invoiceService.publishInvoice(1L);
        assertEquals(invoice.getId(), dto.getId());
        assertEquals(InvoiceStatus.PUBLISHED, invoice.getStatus());
    }

    @Test
    void testGetAllInvoices_Success() {
        Page<Invoice> page = new PageImpl<>(List.of(invoice));
        when(invoiceRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<InvoiceDTO> result = invoiceService.getAllInvoices(Pageable.unpaged());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testDeleteInvoice_Success() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(superAdmin));

        assertDoesNotThrow(() -> invoiceService.deleteInvoice(1L));
        verify(invoiceRepository, times(1)).deleteById(1L);
    }


    @Test
    void testGetInvoicesByCriteria_ReturnsPage() {
        Page<Invoice> page = new PageImpl<>(List.of(invoice));
        when(invoiceRepository.findInvoicesByCriteria(InvoiceStatus.DRAFT, Pageable.unpaged())).thenReturn(page);

        Page<InvoiceDTO> result = invoiceService.getInvoicesByCriteria(InvoiceStatus.DRAFT, Pageable.unpaged());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testUpdateInvoice_ByBuyer_Success() {

        User buyerUser = new User();
        buyerUser.setId(1);
        buyerUser.setRole(new Role().setName(RoleEnum.BUYER));

        User sellerUser = new User();
        sellerUser.setId(1);
        sellerUser.setRole(new Role().setName(RoleEnum.SELLER));

        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(buyer));

        BillingLine existingLine = new BillingLine();
        existingLine.setId(1L);
        existingLine.setActionType("OLD_ACTION");
        existingLine.setLineType("OLD_TYPE");
        existingLine.setLineType("OLD_LINE");
        existingLine.setAmount( new BigDecimal("50.00"));



        invoice.setBillingLines(new ArrayList<>(List.of(existingLine)));
        invoice.setSeller(sellerUser);
        invoice.setBuyer(buyerUser);
        when(invoiceRepository.findById(invoice.getId())).thenReturn(Optional.of(invoice));
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(buyerUser));

        BillingLine updatedLine = new BillingLine();
        updatedLine.setId(1L);
        updatedLine.setActionType("NEW_ACTION");
        updatedLine.setLineType("NEW_TYPE");
        updatedLine.setLineType("NEW_LINE");
        updatedLine.setAmount( new BigDecimal("100.00"));

        BillingLine newLine = new BillingLine();
        newLine.setId(null);
        newLine.setActionType("NEW");
        newLine.setLineType("TYPE");
        newLine.setLineType("ADDED");
        newLine.setAmount( new BigDecimal("25.00"));

        Invoice updatedInvoiceEntity = new Invoice();
        updatedInvoiceEntity.setBillingId("BILL123");
        updatedInvoiceEntity.setPaymentDueDate(String.valueOf(LocalDate.now().plusDays(10)));
        updatedInvoiceEntity.setCurrencyCode("USD");
        updatedInvoiceEntity.setInvoiceDate(String.valueOf(LocalDate.now()));
        updatedInvoiceEntity.setInvoicingType("TYPE_A");
        updatedInvoiceEntity.setInvoiceLanguageCode("EN");
        updatedInvoiceEntity.setDepartureDate(String.valueOf(LocalDate.now().plusDays(2)));
        updatedInvoiceEntity.setPaymentInformation(new PaymentInformation());

        updatedInvoiceEntity.setBillingLines(new ArrayList<>(List.of(updatedLine, newLine)));

        InvoiceDTO invoiceDTO = mock(InvoiceDTO.class);
        when(invoiceDTO.toEntity()).thenReturn(updatedInvoiceEntity);

        when(invoiceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        InvoiceDTO result = invoiceService.updateInvoice(invoice.getId(), invoiceDTO);

        assertNotNull(result);
        verify(invoiceRepository).save(any(Invoice.class));
        assertEquals(2, invoice.getBillingLines().size());

        BillingLine firstLine = invoice.getBillingLines().get(0);
        assertEquals("NEW_ACTION", firstLine.getActionType());
    }

    @Test
    void testAuthorizeBuyerOrSuperAdminThrowsAccessDeniedException() {

        Invoice invoiceEntity = new Invoice();
        User sellerUser = new User();
        sellerUser.setId(3);
        invoiceEntity.setSeller(sellerUser);

        User unauthorizedUser = new User();
        unauthorizedUser.setId(1);
        unauthorizedUser.setRole(new Role().setName(RoleEnum.BUYER));

        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(unauthorizedUser));

        assertThrows(AccessDeniedException.class, () -> {
            invoiceService.authorizeBuyerOrSuperAdmin(invoiceEntity);
        });
    }


}

