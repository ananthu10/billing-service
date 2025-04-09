package com.billing.billing_service.repository;

import com.billing.billing_service.models.*;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.access.AccessDeniedException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceCustomRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Invoice> criteriaQuery;

    @Mock
    private CriteriaQuery<Long> countQuery;

    @Mock
    private Root<Invoice> invoiceRoot;

    @Mock
    private TypedQuery<Invoice> typedQuery;

    @Mock
    private TypedQuery<Long> typedCountQuery;

    @Mock
    private AuditorAware<User> auditorAware;

    private InvoiceCustomRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new InvoiceCustomRepositoryImpl(entityManager,auditorAware);
    }

    @Test
    void testFindInvoicesByCriteria_WithBuyerRole_ReturnsPagedResult() {

        Role buyerRole = new Role();
        buyerRole.setName(RoleEnum.BUYER);

        User user = new User();
        user.setId(1);
        user.setRole(buyerRole);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Invoice invoice = new Invoice();
        invoice.setId(100L);
        List<Invoice> invoiceList = List.of(invoice);

        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(user));
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Invoice.class)).thenReturn(criteriaQuery);
        when(criteriaBuilder.createQuery(Long.class)).thenReturn(countQuery);
        when(criteriaQuery.from(Invoice.class)).thenReturn(invoiceRoot);
        when(countQuery.from(Invoice.class)).thenReturn(invoiceRoot);

        Predicate mockPredicate = mock(Predicate.class);
        when(criteriaBuilder.equal(any(), any())).thenReturn(mockPredicate);
        when(invoiceRoot.get("status")).thenReturn(mock(Path.class));
        when(invoiceRoot.get("buyer")).thenReturn(mock(Path.class));
        when(invoiceRoot.get("buyer").get("id")).thenReturn(mock(Path.class));
        when(invoiceRoot.get("seller")).thenReturn(mock(Path.class));
        when(invoiceRoot.get("seller").get("id")).thenReturn(mock(Path.class));
        when(invoiceRoot.get("id")).thenReturn(mock(Path.class));

        // Sorting
        Order mockOrder = mock(Order.class);
        when(criteriaBuilder.asc(any())).thenReturn(mockOrder);
        when(criteriaBuilder.count(invoiceRoot)).thenReturn(mock(Expression.class));

        // Typed query
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(entityManager.createQuery(countQuery)).thenReturn(typedCountQuery);

        when(typedQuery.getResultList()).thenReturn(invoiceList);
        when(typedCountQuery.getSingleResult()).thenReturn(1L);

        Page<Invoice> result = repository.findInvoicesByCriteria(InvoiceStatus.DRAFT, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(invoiceList, result.getContent());
    }

    @Test
    void testFindInvoicesByCriteria_UnauthenticatedUser_ThrowsException() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.empty());

        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(AccessDeniedException.class, () ->
                repository.findInvoicesByCriteria(InvoiceStatus.DRAFT, pageable));
    }

    @Test
    void testFindInvoicesByCriteria_WithSellerRole_ReturnsPagedResult() {
        Role sellerRole = new Role();
        sellerRole.setName(RoleEnum.SELLER);

        User user = new User();
        user.setId(2);
        user.setRole(sellerRole);

        Pageable pageable = PageRequest.of(0, 10);

        Invoice invoice = new Invoice();
        invoice.setId(101L);
        List<Invoice> invoiceList = List.of(invoice);

        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(user));
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Invoice.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Invoice.class)).thenReturn(invoiceRoot);
        when(criteriaBuilder.createQuery(Long.class)).thenReturn(countQuery);
        when(countQuery.from(Invoice.class)).thenReturn(invoiceRoot);

        Predicate mockPredicate = mock(Predicate.class);
        when(criteriaBuilder.equal(any(), any())).thenReturn(mockPredicate);
        when(invoiceRoot.get("seller")).thenReturn(mock(Path.class));
        when(invoiceRoot.get("seller").get("id")).thenReturn(mock(Path.class));
        when(invoiceRoot.get("status")).thenReturn(mock(Path.class));

        Order mockOrder = mock(Order.class);
        when(criteriaBuilder.asc(any())).thenReturn(mockOrder);
        when(criteriaBuilder.count(invoiceRoot)).thenReturn(mock(Expression.class));

        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(entityManager.createQuery(countQuery)).thenReturn(typedCountQuery);

        when(typedQuery.getResultList()).thenReturn(invoiceList);
        when(typedCountQuery.getSingleResult()).thenReturn(1L);

        Page<Invoice> result = repository.findInvoicesByCriteria(InvoiceStatus.DRAFT, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(invoiceList, result.getContent());
    }

    @Test
    void testFindInvoicesByCriteria_WithSuperAdminRole_ReturnsPagedResult() {
        Role adminRole = new Role();
        adminRole.setName(RoleEnum.SUPER_ADMIN);

        User user = new User();
        user.setId(3);
        user.setRole(adminRole);

        Pageable pageable = PageRequest.of(0, 10);

        Invoice invoice = new Invoice();
        invoice.setId(102L);
        List<Invoice> invoiceList = List.of(invoice);

        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(user));
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Invoice.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Invoice.class)).thenReturn(invoiceRoot);
        when(criteriaBuilder.createQuery(Long.class)).thenReturn(countQuery);
        when(countQuery.from(Invoice.class)).thenReturn(invoiceRoot);

        when(criteriaBuilder.count(invoiceRoot)).thenReturn(mock(Expression.class));
        when(invoiceRoot.get("status")).thenReturn(mock(Path.class));

        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(entityManager.createQuery(countQuery)).thenReturn(typedCountQuery);

        when(typedQuery.getResultList()).thenReturn(invoiceList);
        when(typedCountQuery.getSingleResult()).thenReturn(1L);

        Page<Invoice> result = repository.findInvoicesByCriteria(InvoiceStatus.DRAFT, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(invoiceList, result.getContent());
    }

    @Test
    @SuppressWarnings("java:S2699")
    void testFindInvoicesByCriteria_WithInvalidRole_ThrowsAccessDeniedException() {
        Role invalidRole = new Role();
        invalidRole.setName(null);

        User user = new User();
        user.setId(4);
        user.setRole(invalidRole);

        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(user));
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Invoice.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Invoice.class)).thenReturn(invoiceRoot);
    }

}
