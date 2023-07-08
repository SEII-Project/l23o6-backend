package org.fffd.l23o6;

import org.fffd.l23o6.dao.*;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.pojo.entity.OrderEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.enum_.OrderStatus;
import org.fffd.l23o6.pojo.enum_.PaymentType;
import org.fffd.l23o6.pojo.enum_.TrainStatus;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.enum_.UserType;
import org.fffd.l23o6.service.impl.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
public class OrderServiceImplTests {
    @Autowired
    OrderDao orderDao;
    @Autowired
    UserDao userDao;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    TrainDao trainDao;
    @Autowired
    TrainServiceImpl trainService;
    @Autowired
    RouteDao routeDao;
    @Autowired
    RouteServiceImpl routeService;
    @Autowired
    StationServiceImpl stationService;
    @Autowired
    StationDao stationDao;

    @Test
    void createOrderTest1() {
        if (userService.findByUserName("xsswsx") != null) userDao.delete(userService.findByUserName("xsswsx"));
        userService.register("xsswsx","123456Aa","宋毅恒","32050120020925527X","15050196910","身份证", UserType.USER);
        assert userDao.findByUsername("xsswsx").getName().equals("宋毅恒");
        
        if (null == stationDao.findByName("1")) stationService.addStation("1");
        if (null == stationDao.findByName("2")) stationService.addStation("2");
        if (null == stationDao.findByName("3")) stationService.addStation("3");

        if (null == routeDao.findByName("京南线")) routeService.addRoute("京南线", new ArrayList<>(Arrays.asList(stationDao.findByName("1").getId(), stationDao.findByName("2").getId(), stationDao.findByName("3").getId())));
        long routeID = -1L;
        for (int i = 0 ; i < routeService.listRoutes().size(); i++){
            if (routeService.listRoutes().get(i).getName().equals("京南线")){
                routeID = routeService.listRoutes().get(i).getId();
                break;
            }
        }
        assert routeID != -1L;
        assert routeDao.existsById(routeID);
        if (null == trainDao.findByName("G0001")) {
            trainService.addTrain("G0001",routeID,TrainType.HIGH_SPEED,"2023-7-8",
                    new ArrayList<>(Arrays.asList(
                            new Date(2023,Calendar.JULY,8,20,0),
                            new Date(2023,Calendar.JULY,8,21,0),
                            new Date(2023,Calendar.JULY,8,22,0))),
                    new ArrayList<>(Arrays.asList(
                            new Date(2023,Calendar.JULY,7,20,0),
                            new Date(2023,Calendar.JULY,8,21,10),
                            new Date(2023,Calendar.JULY,8,22,0))),
                    new ArrayList<>(Arrays.asList(TrainStatus.ON_TIME,TrainStatus.ON_TIME,TrainStatus.DELAY))
            );
        }

        Long trainID = -1L;
        for (TrainEntity train: trainDao.findAll()) {
            if (routeService.getRoute(train.getRouteId()).getName().equals("京南线")) {
                trainID = train.getId();
                break;
            }
        }
        OrderServiceImpl impl = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        Long orderID = impl.createOrder("xsswsx", trainID,stationDao.findByName("1").getId(),stationDao.findByName("3").getId(),"一等座",1L,50);
        assert orderID != null;
    }

    @Test
    void createOrderTest2() {
        if (userService.findByUserName("xsswsx") != null) userDao.delete(userService.findByUserName("xsswsx"));
        userService.register("xsswsx","123456Aa","宋毅恒","32050120020925527X","15050196910","身份证", UserType.USER);
        assert userDao.findByUsername("xsswsx").getName().equals("宋毅恒");
        
        if (null == stationDao.findByName("1")) stationService.addStation("1");
        if (null == stationDao.findByName("6")) stationService.addStation("6");
        if (null == stationDao.findByName("9")) stationService.addStation("9");
        
        if (null == routeDao.findByName("京广线")) routeService.addRoute("京广线",new ArrayList<>(Arrays.asList(stationDao.findByName("1").getId(), stationDao.findByName("6").getId(), stationDao.findByName("9").getId())));
        Long routeID = -1L;
        for (int i = 0 ; i < routeService.listRoutes().size(); i++){
            if (routeService.listRoutes().get(i).getName().equals("京广线")){
                routeID = routeService.listRoutes().get(i).getId();
                break;
            }
        }
        assert routeID != -1L;
        assert routeDao.existsById(routeID);
        if (null == trainDao.findByName("G0002")) {
            trainService.addTrain("G0002",routeID,TrainType.HIGH_SPEED,"2023-7-8",
                    new ArrayList<>(Arrays.asList(
                            new Date(2023,Calendar.JULY,8,20,0),
                            new Date(2023,Calendar.JULY,8,21,0),
                            new Date(2023,Calendar.JULY,8,22,0))),
                    new ArrayList<>(Arrays.asList(
                            new Date(2023,Calendar.JULY,7,20,0),
                            new Date(2023,Calendar.JULY,8,21,10),
                            new Date(2023,Calendar.JULY,8,22,0))),
                    new ArrayList<>(Arrays.asList(TrainStatus.ON_TIME,TrainStatus.ON_TIME,TrainStatus.DELAY))
            );
        }
        Long trainID = -1L;
        for (TrainEntity train: trainDao.findAll()) {
            if (routeService.getRoute(train.getRouteId()).getName().equals("京广线")) {
                trainID = train.getId();
                break;
            }
        }
        OrderServiceImpl impl = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        Long orderID = impl.createOrder("xsswsx",trainID,stationDao.findByName("1").getId(), stationDao.findByName("9").getId(),"无座",3L,10);
        assert orderID != null;
    }

    @Test
    void createOrderTest3() {
        if (userService.findByUserName("xsswsx") != null) userDao.delete(userService.findByUserName("xsswsx"));
        userService.register("xsswsx","123456Aa","宋毅恒","32050120020925527X","15050196910","身份证", UserType.USER);
        assert userDao.findByUsername("xsswsx").getName().equals("宋毅恒");
        
        if (null == stationDao.findByName("1")) stationService.addStation("1");
        if (null == stationDao.findByName("5")) stationService.addStation("5");
        if (null == stationDao.findByName("7")) stationService.addStation("7");
        
        if (null == routeDao.findByName("京海线")) routeService.addRoute("京海线",new ArrayList<>(Arrays.asList(stationDao.findByName("1").getId(),stationDao.findByName("5").getId(),stationDao.findByName("7").getId())));
        Long routeID = -1L;
        for (int i = 0 ; i < routeService.listRoutes().size(); i++){
            if (routeService.listRoutes().get(i).getName().equals("京海线")){
                routeID = routeService.listRoutes().get(i).getId();
                break;
            }
        }
        assert routeID != -1L;
        assert routeDao.existsById(routeID);
        if (null == trainDao.findByName("G0003")) {
            trainService.addTrain("G0003",routeID,TrainType.HIGH_SPEED,"2023-7-8",
                    new ArrayList<>(Arrays.asList(
                            new Date(2023,Calendar.JULY,8,20,0),
                            new Date(2023,Calendar.JULY,8,21,0),
                            new Date(2023,Calendar.JULY,8,22,0))),
                    new ArrayList<>(Arrays.asList(
                            new Date(2023,Calendar.JULY,7,20,0),
                            new Date(2023,Calendar.JULY,8,21,10),
                            new Date(2023,Calendar.JULY,8,22,0))),
                    new ArrayList<>(Arrays.asList(TrainStatus.ON_TIME,TrainStatus.ON_TIME,TrainStatus.DELAY))
            );
        }
        Long trainID = -1L;
        for (TrainEntity train: trainDao.findAll()) {
            if (routeService.getRoute(train.getRouteId()).getName().equals("京海线")) {
                trainID = train.getId();
                break;
            }
        }
        assert trainID != -1L;
        assert trainDao.existsById(trainID);
        OrderServiceImpl impl = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        Long orderID = impl.createOrder("xsswsx",trainID,stationDao.findByName("1").getId(),stationDao.findByName("5").getId(),"商务座",3L,100);
        assert orderID != null;
    }

    @Test
    void createOrderTest4() {
        if (userService.findByUserName("xsswsx") != null) userDao.delete(userService.findByUserName("xsswsx"));
        userService.register("xsswsx","123456Aa","宋毅恒","32050120020925527X","15050196910","身份证", UserType.USER);
        assert userDao.findByUsername("xsswsx").getName().equals("宋毅恒");
        
        if (null == stationDao.findByName("10")) stationService.addStation("10");
        if (null == stationDao.findByName("15")) stationService.addStation("15");
        if (null == stationDao.findByName("17")) stationService.addStation("17");
        
        if (null == routeDao.findByName("新粤线")) routeService.addRoute("新粤线",new ArrayList<>(Arrays.asList(stationDao.findByName("10").getId(),stationDao.findByName("15").getId(),stationDao.findByName("17").getId())));
        Long routeID = -1L;
        for (int i = 0 ; i < routeService.listRoutes().size(); i++){
            if (routeService.listRoutes().get(i).getName().equals("新粤线")){
                routeID = routeService.listRoutes().get(i).getId();
                break;
            }
        }
        assert routeID != -1L;
        assert routeDao.existsById(routeID);
        if (null == trainDao.findByName("G0004")) {
            trainService.addTrain("G0004",routeID,TrainType.HIGH_SPEED,"2023-7-8",
                    new ArrayList<>(Arrays.asList(
                            new Date(2023,Calendar.JULY,8,20,0),
                            new Date(2023,Calendar.JULY,8,21,0),
                            new Date(2023,Calendar.JULY,8,22,0))),
                    new ArrayList<>(Arrays.asList(
                            new Date(2023,Calendar.JULY,7,20,0),
                            new Date(2023,Calendar.JULY,8,21,10),
                            new Date(2023,Calendar.JULY,8,22,0))),
                    new ArrayList<>(Arrays.asList(TrainStatus.ON_TIME,TrainStatus.ON_TIME,TrainStatus.DELAY))
            );
        }
        Long trainID = trainDao.findAll().stream().filter(train -> routeService.getRoute(train.getRouteId()).getName().equals("新粤线")).findFirst().map(TrainEntity::getId).orElse(-1L);
        OrderServiceImpl impl = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        assertThrows(Exception.class, () -> {
            impl.createOrder("xsswsx", trainID, stationDao.findByName("1").getId(), stationDao.findByName("9").getId(), "商务座", 1L, 100);
        });
    }

    @Test
    void listOrdersTest(){
        if (userService.findByUserName("xsswsx") != null) userDao.delete(userService.findByUserName("xsswsx"));
        userService.register("xsswsx","123456Aa","宋毅恒","32050120020925527X","15050196910","身份证", UserType.USER);
        assert userDao.findByUsername("xsswsx").getName().equals("宋毅恒");

        Long userID = userService.findByUserName("xsswsx").getId();

        OrderEntity entity1 = new OrderEntity();
        entity1.setUserId(userID);
        entity1.setTrainId(1L);
        entity1.setDepartureStationId(1L);
        entity1.setArrivalStationId(2L);
        entity1.setStatus(OrderStatus.CANCELLED);
        entity1.setSeat("商务座");
        entity1.setPrice(100);
        entity1.setDiscount(0);
        entity1.setPaymentType(PaymentType.WECHAT_PAY);

        OrderEntity entity2 = new OrderEntity();
        entity2.setUserId(userID);
        entity2.setTrainId(2L);
        entity2.setDepartureStationId(4L);
        entity2.setArrivalStationId(5L);
        entity2.setStatus(OrderStatus.PAID);
        entity2.setSeat("一等座");
        entity2.setPrice(50);
        entity2.setDiscount(0);
        entity2.setPaymentType(PaymentType.WECHAT_PAY);

        OrderEntity entity3 = new OrderEntity();
        entity3.setUserId(userID);
        entity3.setTrainId(3L);
        entity3.setDepartureStationId(1L);
        entity3.setArrivalStationId(7L);
        entity3.setStatus(OrderStatus.PENDING_PAYMENT);
        entity3.setSeat("二等座");
        entity3.setPrice(30);
        entity3.setDiscount(0);
        entity3.setPaymentType(PaymentType.WECHAT_PAY);

        orderDao.save(entity1);
        orderDao.save(entity2);
        orderDao.save(entity3);

        OrderServiceImpl orderService = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        orderService.listOrders("xsswsx");
    }

    @Test
    void cancelOrderTest(){
        if (userService.findByUserName("xsswsx") != null) userDao.delete(userService.findByUserName("xsswsx"));
        userService.register("xsswsx","123456Aa","宋毅恒","32050120020925527X","15050196910","身份证", UserType.USER);
        assert userDao.findByUsername("xsswsx").getName().equals("宋毅恒");

        Long userID = userService.findByUserName("xsswsx").getId();
        OrderEntity entity = new OrderEntity();
        entity.setUserId(userID);
        entity.setTrainId(4L);
        entity.setDepartureStationId(8L);
        entity.setArrivalStationId(9L);
        entity.setStatus(OrderStatus.PENDING_PAYMENT);
        entity.setSeat("商务座");
        entity.setPrice(100);
        entity.setDiscount(0);
        entity.setPaymentType(PaymentType.WECHAT_PAY);
        entity.setTradeId("1000");
        orderDao.save(entity);

        OrderServiceImpl orderService = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        try{
            orderService.cancelOrder(orderDao.findByTradeId("1000").getId());
        } catch (Exception e){
            System.err.println("cancelOrder throws AlipayApiException");
        }
        assert orderDao.findByTradeId("1000").getStatus().equals(OrderStatus.CANCELLED);
    }

    @Test
    void cancelOrderTest2(){
        if (userService.findByUserName("xsswsx") != null) userDao.delete(userService.findByUserName("xsswsx"));
        userService.register("xsswsx","123456Aa","宋毅恒","32050120020925527X","15050196910","身份证", UserType.USER);
        userDao.findByUsername("xsswsx").getName().equals("宋毅恒");

        Long userID = userService.findByUserName("xsswsx").getId();
        OrderEntity entity = new OrderEntity();
        entity.setUserId(userID);
        entity.setTrainId(3L);
        entity.setDepartureStationId(6L);
        entity.setArrivalStationId(7L);
        entity.setStatus(OrderStatus.PAID);
        entity.setSeat("商务座");
        entity.setPrice(100);
        entity.setDiscount(0);
        entity.setPaymentType(PaymentType.WECHAT_PAY);
        entity.setTradeId("2000");

        OrderServiceImpl orderService = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        
        assertThrows(Exception.class, () -> {
            orderService.cancelOrder(orderDao.findByTradeId("1000").getId());
        });
        assert entity.getStatus().equals(OrderStatus.PAID);
    }

    @Test
    void refundOrderTest(){
        if (userService.findByUserName("xsswsx") != null) userDao.delete(userService.findByUserName("xsswsx"));
        userService.register("xsswsx","123456Aa","宋毅恒","32050120020925527X","15050196910","身份证", UserType.USER);
        assert userDao.findByUsername("xsswsx").getName().equals("宋毅恒");

        Long userID = userService.findByUserName("xsswsx").getId();
        OrderEntity entity = new OrderEntity();
        entity.setUserId(userID);
        entity.setTrainId(2L);
        entity.setDepartureStationId(4L);
        entity.setArrivalStationId(5L);
        entity.setStatus(OrderStatus.PAID);
        entity.setSeat("商务座");
        entity.setPrice(100);
        entity.setDiscount(0);
        entity.setPaymentType(PaymentType.WECHAT_PAY);
        entity.setTradeId("3000");
        orderDao.save(entity);

        OrderServiceImpl orderService = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        try{
            orderService.refundOrder(orderDao.findByTradeId("3000").getId());
        }catch (Exception e){
            System.err.println("refundOrder throws AlipayApiException");
        }
        assert orderDao.findByTradeId("3000").getStatus().equals(OrderStatus.REFUNDED);
    }

    @Test
    void refundOrderTest2(){
        if (userService.findByUserName("xsswsx") != null) userDao.delete(userService.findByUserName("xsswsx"));
        userService.register("xsswsx","123456Aa","宋毅恒","32050120020925527X","15050196910","身份证", UserType.USER);
        assert userDao.findByUsername("xsswsx").getName().equals("宋毅恒");

        Long userID = userService.findByUserName("xsswsx").getId();
        OrderEntity entity = new OrderEntity();
        entity.setUserId(userID);
        entity.setTrainId(1L);
        entity.setDepartureStationId(1L);
        entity.setArrivalStationId(2L);
        entity.setStatus(OrderStatus.COMPLETED);
        entity.setSeat("商务座");
        entity.setPrice(100);
        entity.setDiscount(0);
        entity.setPaymentType(PaymentType.WECHAT_PAY);
        entity.setTradeId("4000");
        orderDao.save(entity);

        OrderServiceImpl orderService = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        assertThrows(Exception.class, () -> {
            orderService.refundOrder(orderDao.findByTradeId("4000").getId());
        });
        assert entity.getStatus().equals(OrderStatus.COMPLETED);
    }
}
