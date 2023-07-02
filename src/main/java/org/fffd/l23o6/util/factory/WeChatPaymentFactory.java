package org.fffd.l23o6.util.factory;

import org.fffd.l23o6.util.strategy.payment.PaymentStrategy;
import org.fffd.l23o6.util.strategy.payment.WeChatPaymentStrategy;

public class WeChatPaymentFactory extends PaymentFactory {
    @Override
    public PaymentStrategy createPayment() {
        return new WeChatPaymentStrategy();
    }
}
