package org.fffd.l23o6.util.factory;

import org.fffd.l23o6.util.strategy.payment.AlipayPaymentStrategy;
import org.fffd.l23o6.util.strategy.payment.PaymentStrategy;

public class AlipayPaymentFactory extends PaymentFactory{
    @Override
    public PaymentStrategy createPayment() {
        return new AlipayPaymentStrategy();
    }
}
