package org.fffd.l23o6.util.strategy.payment;

import org.fffd.l23o6.pojo.entity.OrderEntity;

public class WeChatPaymentStrategy extends PaymentStrategy{
    @Override
    public String pay(OrderEntity order) {
        return null;
    }
    
    @Override
    public void refund(OrderEntity order) {
    
    }
}
