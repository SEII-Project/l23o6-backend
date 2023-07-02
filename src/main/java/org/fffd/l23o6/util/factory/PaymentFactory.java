package org.fffd.l23o6.util.factory;

import org.fffd.l23o6.util.strategy.payment.PaymentStrategy;

public abstract class PaymentFactory {
    public abstract PaymentStrategy createPayment();
}
