package org.fffd.l23o6.util.strategy.payment;

import com.alipay.api.AlipayApiException;
import org.fffd.l23o6.pojo.entity.OrderEntity;

public abstract class PaymentStrategy {
    public abstract void pay(OrderEntity order) throws AlipayApiException;
    public abstract void refund(OrderEntity order) throws AlipayApiException;
}
