package org.fffd.l23o6.pojo.vo.order;

import org.fffd.l23o6.pojo.enum_.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.fffd.l23o6.pojo.enum_.PaymentType;

@Data
@Schema(description = "订单状态修改")
public class PatchOrderRequest {

    @Schema(description = "订单状态", required = true)
    @NotNull
    private OrderStatus status;

    @Schema(description = "支付方式")
    private PaymentType paymentType;
}
