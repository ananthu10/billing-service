package com.billing.billing_service.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payment_information")
@Getter
@Setter
public class PaymentInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "card_number_type")
    private String cardNumberType;

    @Column(name = "short_card_num")
    private String shortCardNum;

    @OneToOne(mappedBy = "paymentInformation")
    private Invoice invoice;

}