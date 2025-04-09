package com.billing.billing_service.dtos;

import com.billing.billing_service.models.BillingLine;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BillingLineDTO {

    private Long id;
    private String actionType;
    private String productType;
    private String lineType;
    private BigDecimal amount;

    public static BillingLineDTO fromEntity(BillingLine billingLine) {
        BillingLineDTO dto = new BillingLineDTO();
        dto.setId(billingLine.getId());
        dto.setActionType(billingLine.getActionType());
        dto.setProductType(billingLine.getProductType());
        dto.setLineType(billingLine.getLineType());
        dto.setAmount(billingLine.getAmount());
        return dto;
    }

    public BillingLine toEntity() {
        BillingLine billingLine = new BillingLine();
        billingLine.setId(this.id);
        billingLine.setActionType(this.actionType);
        billingLine.setProductType(this.productType);
        billingLine.setLineType(this.lineType);
        billingLine.setAmount(this.amount);
        return billingLine;
    }
}