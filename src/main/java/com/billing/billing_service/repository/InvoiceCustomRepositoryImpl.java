package com.billing.billing_service.repository;

import com.billing.billing_service.models.Invoice;
import com.billing.billing_service.models.InvoiceStatus;
import com.billing.billing_service.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public class InvoiceCustomRepositoryImpl implements InvoiceCustomRepository {

    private final EntityManager entityManager;
    private final AuditorAware<User> auditorAware;

    public InvoiceCustomRepositoryImpl(EntityManager entityManager, AuditorAware<User> auditorAware) {
        this.entityManager = entityManager;
        this.auditorAware = auditorAware;
    }


    @Override
    public Page<Invoice> findInvoicesByCriteria(InvoiceStatus status, Pageable pageable) {

        User currentUser = auditorAware.getCurrentAuditor()
                .orElseThrow(() -> new AccessDeniedException("User not authenticated"));

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Invoice> cq = cb.createQuery(Invoice.class);
        Root<Invoice> root = cq.from(Invoice.class);

        List<Predicate> predicates = buildPredicates(cb, root, status, currentUser);
        cq.where(predicates.toArray(new Predicate[0]));

        if (pageable.getSort().isSorted()) {
            List<Order> orders = pageable.getSort().stream()
                    .map(order -> order.isAscending()
                            ? cb.asc(root.get(order.getProperty()))
                            : cb.desc(root.get(order.getProperty())))
                    .toList();
            cq.orderBy(orders);
        }

        TypedQuery<Invoice> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Invoice> resultList = query.getResultList();
        Long total = countTotalRecords(cb, status, currentUser);

        return new PageImpl<>(resultList, pageable, total);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Invoice> root, InvoiceStatus status, User currentUser) {
        List<Predicate> predicates = new ArrayList<>();

        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }

        String role = currentUser.getRole().getName().toString();
        switch (role) {
            case "BUYER" -> predicates.add(cb.equal(root.get("buyer").get("id"), currentUser.getId()));
            case "SELLER" -> predicates.add(cb.equal(root.get("seller").get("id"), currentUser.getId()));
            case "SUPER_ADMIN" -> { /* No additional filtering */ }
            default -> throw new AccessDeniedException("Unauthorized role: " + role);
        }

        return predicates;
    }

    private Long countTotalRecords(CriteriaBuilder cb, InvoiceStatus status, User currentUser) {

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Invoice> countRoot = countQuery.from(Invoice.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> countPredicates = buildPredicates(cb, countRoot, status, currentUser);
        countQuery.where(countPredicates.toArray(new Predicate[0]));

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
