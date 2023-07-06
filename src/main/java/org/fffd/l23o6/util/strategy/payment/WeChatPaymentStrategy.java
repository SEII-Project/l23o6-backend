package org.fffd.l23o6.util.strategy.payment;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import jakarta.validation.constraints.NotNull;
import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.pojo.entity.OrderEntity;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.enum_.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.UUID;

public class WeChatPaymentStrategy extends PaymentStrategy{
    @Override
    public String pay(OrderEntity order, UserEntity user) {
        double deposit = user.getDeposit();
        
        if (deposit >= order.getPrice()) {
            long timeStamp = System.currentTimeMillis();
            byte[] timeStampBytes = ByteBuffer.allocate(Long.BYTES).putLong(timeStamp).array();
            UUID uuid = UUID.nameUUIDFromBytes(timeStampBytes);
            order.setTradeId(uuid.toString());
            
            user.setDeposit(deposit - order.getPrice());
            order.setStatus(OrderStatus.PAID);
        } else {
            throw new BizException(BizError.DEPOSIT_NOT_ENOUGH);
        }
        
        return null;
    }
    
    @Override
    public void refund(OrderEntity order, UserEntity user) {
        user.setDeposit(user.getDeposit() + order.getPrice());
        order.setStatus(OrderStatus.REFUNDED);
    }
    
    @Override
    public OrderStatus query(final OrderEntity order) throws AlipayApiException {
        return order.getStatus();
    }
}
