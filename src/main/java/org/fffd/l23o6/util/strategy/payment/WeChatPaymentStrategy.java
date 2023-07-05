package org.fffd.l23o6.util.strategy.payment;

import com.alipay.api.AlipayApiException;
import org.fffd.l23o6.pojo.entity.OrderEntity;
import org.fffd.l23o6.pojo.enum_.OrderStatus;

public class WeChatPaymentStrategy extends PaymentStrategy{
    @Override
    public String pay(OrderEntity order) {
        return null;
    }
    
    @Override
    public void refund(OrderEntity order) {
    
    }
    
    @Override
    public OrderStatus query(final OrderEntity order) throws AlipayApiException {
        return null;
    }
}
