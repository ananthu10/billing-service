package com.billing.billing_service.dtos;

import com.billing.billing_service.models.BillingLine;
import com.billing.billing_service.models.PaymentInformation;
import com.billing.billing_service.models.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentInformationDTO {

    private Long id;
    private PaymentMethod paymentMethod;
    private String cardNumberType;
    private String shortCardNum;

    public static PaymentInformationDTO fromEntity(PaymentInformation paymentInfo) {
        PaymentInformationDTO dto = new PaymentInformationDTO();
        dto.setId(paymentInfo.getId());
        dto.setPaymentMethod(paymentInfo.getPaymentMethod());
        dto.setCardNumberType(paymentInfo.getCardNumberType());
        dto.setShortCardNum(paymentInfo.getShortCardNum());
        return dto;
    }

    public PaymentInformation toEntity() {
        PaymentInformation paymentInfo = new PaymentInformation();
        paymentInfo.setId(this.id);
        paymentInfo.setPaymentMethod(this.paymentMethod);
        paymentInfo.setCardNumberType(this.cardNumberType);
        paymentInfo.setShortCardNum(this.shortCardNum);
        return paymentInfo;
    }
}