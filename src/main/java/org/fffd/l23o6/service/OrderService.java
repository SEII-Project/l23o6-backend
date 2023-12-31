package org.fffd.l23o6.service;

import java.util.List;

import com.alipay.api.AlipayApiException;
import org.fffd.l23o6.pojo.enum_.PaymentType;
import org.fffd.l23o6.pojo.vo.order.OrderVO;

public interface OrderService {
    Long createOrder(String username, Long trainId, Long fromStationId, Long toStationId, String seatType, Long seatNumber, double price);
    List<OrderVO> listOrders(String username);
    OrderVO getOrder(Long id) throws AlipayApiException;
    void cancelOrder(Long id) throws AlipayApiException;
    String payOrder(Long id, PaymentType paymentType) throws AlipayApiException;
    void refundOrder(Long id) throws AlipayApiException;
}
