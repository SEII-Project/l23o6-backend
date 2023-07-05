package org.fffd.l23o6.util.strategy.payment;

public class CreditsDiscountStrategy {

    private final int[] CREDITS_DISCOUNT = new int[]{1000, 3000, 10000, 50000};
    private final double[] DISCOUNT_RATE = new double[]{0.01, 0.05, 0.075, 0.1};
    public double calculateDiscount(int credits) {
        double rate = 0;
        for(int i = 0; i < CREDITS_DISCOUNT.length; i++) {
            if(credits >= CREDITS_DISCOUNT[i]) {
                rate = DISCOUNT_RATE[i];
            }
        }
        return rate;
    }
    
    public double calculatePrice(double price, int credits) {
        double rate = calculateDiscount(credits);
        return (price * (1 - rate));
    }
    
}
