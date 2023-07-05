package org.fffd.l23o6.util.strategy.payment;

import com.alipay.api.AlipayApiException;
import org.fffd.l23o6.pojo.entity.OrderEntity;
import org.fffd.l23o6.pojo.enum_.OrderStatus;

public abstract class PaymentStrategy {
    public abstract String pay(OrderEntity order) throws AlipayApiException;
    public abstract void refund(OrderEntity order) throws AlipayApiException;
    public abstract OrderStatus query(OrderEntity order) throws AlipayApiException;
}
