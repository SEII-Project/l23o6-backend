package org.fffd.l23o6.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.alipay.api.AlipayApiException;
import org.fffd.l23o6.dao.OrderDao;
import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.enum_.OrderStatus;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.pojo.entity.OrderEntity;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.enum_.PaymentType;
import org.fffd.l23o6.pojo.vo.order.OrderVO;
import org.fffd.l23o6.service.OrderService;
import org.fffd.l23o6.util.factory.AlipayPaymentFactory;
import org.fffd.l23o6.util.factory.PaymentFactory;
import org.fffd.l23o6.util.factory.WeChatPaymentFactory;
import org.fffd.l23o6.util.strategy.payment.CreditsDiscountStrategy;
import org.fffd.l23o6.util.strategy.payment.PaymentStrategy;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;
import org.springframework.stereotype.Service;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderDao orderDao;
    private final UserDao userDao;
    private final TrainDao trainDao;
    private final RouteDao routeDao;
    private final long ONE_DAY = 24 * 60 * 60 * 1000;
    private final CreditsDiscountStrategy creditsDiscountStrategy = new CreditsDiscountStrategy();
    
    /**
     *
     * @param username
     * @param trainId
     * @param fromStationId
     * @param toStationId
     * @param seatType
     * @param seatNumber
     * @param price: 打折后的价格
     * @return
     */
    public Long createOrder(String username, Long trainId, Long fromStationId, Long toStationId, String seatType,
            Long seatNumber, double price) {
        Long userId = userDao.findByUsername(username).getId();
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startStationIndex = route.getStationIds().indexOf(fromStationId);
        int endStationIndex = route.getStationIds().indexOf(toStationId);
        String seat = null;
        switch (train.getTrainType()) {
            case HIGH_SPEED:
                seat = GSeriesSeatStrategy.INSTANCE.allocSeat(startStationIndex, endStationIndex,
                        GSeriesSeatStrategy.GSeriesSeatType.fromString(seatType), train.getSeats());
                break;
            case NORMAL_SPEED:
                seat = KSeriesSeatStrategy.INSTANCE.allocSeat(startStationIndex, endStationIndex,
                        KSeriesSeatStrategy.KSeriesSeatType.fromString(seatType), train.getSeats());
                break;
        }
        if (seat == null) {
            throw new BizException(BizError.OUT_OF_SEAT);
        }
        price = creditsDiscountStrategy.calculatePrice(price, userDao.findByUsername(username).getCredits());
        OrderEntity order = OrderEntity.builder().trainId(trainId).userId(userId).seat(seat).price(price).discount(creditsDiscountStrategy.calculateDiscount(userDao.findByUsername(username).getCredits())).
                paymentType(PaymentType.ALIPAY_PAY).status(OrderStatus.PENDING_PAYMENT).arrivalStationId(toStationId).departureStationId(fromStationId)
                .build();
        train.setUpdatedAt(null);// force it to update
        trainDao.save(train);
        orderDao.save(order);
        return order.getId();
    }
    
    /**
     *
     * @param username
     * @return
     */
    public List<OrderVO> listOrders(String username) {
        Long userId = userDao.findByUsername(username).getId();
        List<OrderEntity> orders = orderDao.findByUserId(userId);
        orders.sort((o1,o2)-> o2.getId().compareTo(o1.getId()));
        return orders.stream().map(order -> {
            TrainEntity train = trainDao.findById(order.getTrainId()).get();
            RouteEntity route = routeDao.findById(train.getRouteId()).get();
            int startIndex = route.getStationIds().indexOf(order.getDepartureStationId());
            int endIndex = route.getStationIds().indexOf(order.getArrivalStationId());
            if (compareTime(order.getUpdatedAt(), ONE_DAY) && order.getStatus().equals(OrderStatus.PAID))
                order.setStatus(OrderStatus.COMPLETED);
            try {
                return OrderVO.builder().id(order.getId()).trainId(order.getTrainId())
                        .seat(order.getSeat()).status(getOrderStatus(order).getText())
                        .createdAt(order.getCreatedAt())
                        .startStationId(order.getDepartureStationId())
                        .endStationId(order.getArrivalStationId())
                        .departureTime(train.getDepartureTimes().get(startIndex))
                        .arrivalTime(train.getArrivalTimes().get(endIndex))
                        .price(order.getPrice())
                        .discount(order.getDiscount())
                        .status(order.getStatus().getText())
                        .build();
            } catch (AlipayApiException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
    
    /**
     *
     * @param id
     * @return
     * @throws AlipayApiException
     */
    public OrderVO getOrder(Long id) throws AlipayApiException {
        OrderEntity order = orderDao.findById(id).get();
        TrainEntity train = trainDao.findById(order.getTrainId()).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startIndex = route.getStationIds().indexOf(order.getDepartureStationId());
        int endIndex = route.getStationIds().indexOf(order.getArrivalStationId());
        return OrderVO.builder().id(order.getId()).trainId(order.getTrainId())
                .seat(order.getSeat()).status(getOrderStatus(order).getText())
                .createdAt(order.getCreatedAt())
                .startStationId(order.getDepartureStationId())
                .endStationId(order.getArrivalStationId())
                .departureTime(train.getDepartureTimes().get(startIndex))
                .arrivalTime(train.getArrivalTimes().get(endIndex))
                .price(order.getPrice())
                .discount(order.getDiscount())
                .status(order.getStatus().getText())
                .build();
    }
    
    /**
     *
     * @param time
     * @param index
     * @return
     */
    private boolean compareTime(Date time, long index) {
        Date present = new Date();
        long diff = time.getTime() - present.getTime();
        return diff > index;
    }
    
    /**
     *
     * @param orderEntity
     * @return
     * @throws AlipayApiException
     */
    public OrderStatus getOrderStatus(OrderEntity orderEntity) throws AlipayApiException {
        return choosePayment(orderEntity.getPaymentType()).query(orderEntity);
    }

    public void cancelOrder(Long id) throws AlipayApiException {
        OrderEntity order = orderDao.findById(id).get();
        UserEntity user = userDao.findById(order.getUserId()).get();

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        orderDao.save(order);
    }
    
    /**
     *
     * @param id
     * @param paymentType: 微信支付/支付宝支付
     * @return
     * @throws AlipayApiException
     */
    public String payOrder(Long id, PaymentType paymentType) throws AlipayApiException {
        OrderEntity order = orderDao.findById(id).get();
        UserEntity user = userDao.findById(order.getUserId()).get();
        order.setPaymentType(paymentType);

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }
        
        String responseBody = choosePayment(paymentType).pay(order, user);
        user.setCredits((int) Math.floor(user.getCredits() + order.getPrice()));
        
        orderDao.save(order);
        userDao.save(user);
        
        return responseBody;
    }
    
    /**
     *
     * @param id
     * @throws AlipayApiException
     */
    @Override
    public void refundOrder(final Long id) throws AlipayApiException {
        OrderEntity order = orderDao.findById(id).get();
        UserEntity user = userDao.findById(order.getUserId()).get();
        
        if (order.getStatus() != OrderStatus.PAID) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }
        
        choosePayment(order.getPaymentType()).refund(order, user);
        user.setCredits((int) Math.floor(user.getCredits() - order.getPrice()));
        order.setStatus(OrderStatus.REFUNDED);
        orderDao.save(order);
        userDao.save(user);
    }
    
    /**
     *
     * @param type
     * @return
     */
    private PaymentStrategy choosePayment(PaymentType type) {
        PaymentFactory paymentFactory = null;
        if (type == PaymentType.ALIPAY_PAY) {
            paymentFactory = new AlipayPaymentFactory();
        } else if (type == PaymentType.WECHAT_PAY) {
            paymentFactory = new WeChatPaymentFactory();
        }
        assert paymentFactory != null;
        return paymentFactory.createPayment();
    }
}
