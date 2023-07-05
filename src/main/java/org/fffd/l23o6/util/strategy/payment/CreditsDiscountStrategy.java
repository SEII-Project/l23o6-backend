package org.fffd.l23o6.util.strategy.payment;

import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.pojo.entity.OrderEntity;
import org.fffd.l23o6.pojo.entity.UserEntity;

import java.util.HashMap;
import java.util.Map;

public class CreditsDiscountStrategy {
    private UserDao userDao;
    private static final Map<Integer, Double> DISCOUNT_RATES = new HashMap<Integer, Double>() {{
        put(1000, 0.001);
        put(3000, 0.0015);
        put(10000, 0.002);
        put(50000, 0.0025);
    }};
    public double calculateDiscount(int credits) {
        double rate = 0;
        for (Map.Entry<Integer, Double> entry : DISCOUNT_RATES.entrySet()) {
            int threshold = entry.getKey();
            if (credits >= threshold) {
                rate = entry.getValue();
            } else break;
        }
        
        return rate;
    }
    
    public double calculatePrice(double price, int credits) {
        double rate = calculateDiscount(credits);
        return (price * (1 - rate));
    }
    
}
