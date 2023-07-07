package org.fffd.l23o6;

import org.fffd.l23o6.dao.*;
import org.fffd.l23o6.pojo.entity.OrderEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.enum_.OrderStatus;
import org.fffd.l23o6.pojo.enum_.PaymentType;
import org.fffd.l23o6.pojo.enum_.TrainStatus;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.service.impl.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;


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
        userService.register("xsswsx","123456Aa","宋毅恒","32050120020925527X","15050196910","用户");
        assert userDao.findByUsername("xsswsx").getName().equals("宋毅恒");
        
        if (stationService.getStation(1L) == null) stationService.addStation("1");
        if (stationService.getStation(2L) == null) stationService.addStation("2");
        if (stationService.getStation(3L) == null) stationService.addStation("3");

        if (routeService.getRoute(routeDao.findByName("京南线").getId()) == null) routeService.addRoute("京南线", new ArrayList<>(Arrays.asList(1L, 2L, 3L)));
        long routeID = -1L;
        for (int i = 0 ; i < routeService.listRoutes().size(); i++){
            if (routeService.listRoutes().get(i).getName().equals("京南线")){
                routeID = routeService.listRoutes().get(i).getId();
                break;
            }
        }
        assert routeID != -1L;
        assert routeDao.existsById(routeID);
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
        
        Long trainID = -1L;
        for (TrainEntity train: trainDao.findAll()) {
            if (routeService.getRoute(train.getRouteId()).getName().equals("京南线")) {
                trainID = train.getId();
                break;
            }
        }
        OrderServiceImpl impl = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        Long orderID = impl.createOrder("xsswsx", trainID,1L,3L,"一等座",1L,50);
        assert orderID != null;
    }

    @Test
    void createOrderTest2() {
        userDao.delete(userService.findByUserName("xsswsx"));
        userService.register("xsswsx","123456Aa","宋毅恒","32050120020925527X","15050196910","用户");
        assert userDao.findByUsername("xsswsx").getName().equals("宋毅恒");

        routeService.addRoute("京广线",new ArrayList<>(Arrays.asList(1L,6L,9L)));
        Long routeID = -1L;
        for (int i = 0 ; i < routeService.listRoutes().size(); i++){
            if (routeService.listRoutes().get(i).getName().equals("京广线")){
                routeID = routeService.listRoutes().get(i).getId();
                break;
            }
        }
        assert routeID != -1L;
        assert routeDao.existsById(routeID);
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
        Long trainID = -1L;
        for (int i = 0 ; i < trainService.listTrains(1L,9L,"2023-7-8").size() ; i++){
            if (trainService.listTrains(1L,9L,"2023-7-8").get(i).getName().equals("京广线")){
                trainID = trainService.listTrains(1L,9L,"2023-7-8").get(i).getId();
                break;
            }
        }
        OrderServiceImpl impl = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        Long orderID = impl.createOrder("xsswsx",trainID,1L,9L,"无座",3L,10);
        assert orderID != null;
    }

    @Test
    void createOrderTest3() {
        userDao.delete(userService.findByUserName("xsswsx"));
        userService.register("xsswsx","123456Aa","宋毅恒","32050120020925527X","15050196910","用户");
        assert userDao.findByUsername("xsswsx").getName().equals("宋毅恒");

        routeService.addRoute("京海线",new ArrayList<>(Arrays.asList(1L,5L,7L)));
        Long routeID = -1L;
        for (int i = 0 ; i < routeService.listRoutes().size(); i++){
            if (routeService.listRoutes().get(i).getName().equals("京海线")){
                routeID = routeService.listRoutes().get(i).getId();
                break;
            }
        }
        assert routeID != -1L;
        assert routeDao.existsById(routeID);
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
        Long trainID = -1L;
        for (int i = 0 ; i < trainService.listTrains(1L,5L,"2023-7-8").size() ; i++){
            if (trainService.listTrains(1L,5L,"2023-7-8").get(i).getName().equals("京海线")){
                trainID = trainService.listTrains(1L,5L,"2023-7-8").get(i).getId();
                break;
            }
        }
        assert trainID != -1L;
        assert trainDao.existsById(trainID);
        OrderServiceImpl impl = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        Long orderID = impl.createOrder("xsswsx",trainID,1L,5L,"商务座",3L,100);
        assert orderID != null;
    }

    @Test
    void createOrderTest4() {
        userDao.delete(userService.findByUserName("xsswsx"));
        userService.register("xsswsx","123456Aa","宋毅恒","32050120020925527X","15050196910","用户");
        assert userDao.findByUsername("xsswsx").getName().equals("宋毅恒");

        routeService.addRoute("新粤线",new ArrayList<>(Arrays.asList(10L,15L,17L)));
        Long routeID = -1L;
        for (int i = 0 ; i < routeService.listRoutes().size(); i++){
            if (routeService.listRoutes().get(i).getName().equals("新粤线")){
                routeID = routeService.listRoutes().get(i).getId();
                break;
            }
        }
        assert routeID != -1L;
        assert routeDao.existsById(routeID);
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
        Long trainID = -1L;
        for (int i = 0 ; i < trainService.listTrains(10L,17L,"2023-7-8").size() ; i++){
            if (trainService.listTrains(10L,17L,"2023-7-8").get(i).getName().equals("新粤线")){
                trainID = trainService.listTrains(10L,17L,"2023-7-8").get(i).getId();
                break;
            }
        }
        OrderServiceImpl impl = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        Long orderID = impl.createOrder("xsswsx",trainID,1L,9L,"商务座",1L,100);
        assert orderID == null;
    }

    @Test
    void listOrdersTest(){
        userDao.delete(userService.findByUserName("xsswsx"));
        userService.register("xsswsx","123456Aa","宋毅恒","32050120020925527X","15050196910","用户");
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
        entity2.setDepartureStationId(3L);
        entity2.setArrivalStationId(4L);
        entity2.setStatus(OrderStatus.PAID);
        entity2.setSeat("一等座");
        entity2.setPrice(50);
        entity2.setDiscount(0);
        entity2.setPaymentType(PaymentType.ALIPAY_PAY);

        OrderEntity entity3 = new OrderEntity();
        entity3.setUserId(userID);
        entity3.setTrainId(3L);
        entity3.setDepartureStationId(5L);
        entity3.setArrivalStationId(6L);
        entity3.setStatus(OrderStatus.PENDING_PAYMENT);
        entity3.setSeat("二等座");
        entity3.setPrice(30);
        entity3.setDiscount(0);
        entity3.setPaymentType(PaymentType.ALIPAY_PAY);

        orderDao.save(entity1);
        orderDao.save(entity2);
        orderDao.save(entity3);

        OrderServiceImpl orderService = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        orderService.listOrders("xsswsx");
    }

    @Test
    void cancelOrderTest(){
        userDao.delete(userService.findByUserName("xsswsx"));
        userService.register("xsswsx","123456Aa","宋毅恒","32050120020925527X","15050196910","用户");
        assert userDao.findByUsername("xsswsx").getName().equals("宋毅恒");

        Long userID = userService.findByUserName("xsswsx").getId();
        OrderEntity entity = new OrderEntity();
        entity.setUserId(userID);
        entity.setTrainId(4L);
        entity.setDepartureStationId(7L);
        entity.setArrivalStationId(8L);
        entity.setStatus(OrderStatus.PENDING_PAYMENT);
        entity.setSeat("商务座");
        entity.setPrice(100);
        entity.setDiscount(0);
        entity.setPaymentType(PaymentType.WECHAT_PAY);
        entity.setId(1000L);

        OrderServiceImpl orderService = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        try{
            orderService.cancelOrder(1000L);
        }catch (Exception e){
            System.err.println("cancelOrder throws AlipayApiException");
        }
        assert entity.getStatus().equals(OrderStatus.CANCELLED);
    }

    @Test
    void cancelOrderTest2(){
        userDao.delete(userService.findByUserName("xsswsx"));
        userService.register("xsswsx","123456Aa","宋毅恒","32050120020925527X","15050196910","用户");
        assert userDao.findByUsername("xsswsx").getName().equals("宋毅恒");

        Long userID = userService.findByUserName("xsswsx").getId();
        OrderEntity entity = new OrderEntity();
        entity.setUserId(userID);
        entity.setTrainId(5L);
        entity.setDepartureStationId(9L);
        entity.setArrivalStationId(10L);
        entity.setStatus(OrderStatus.PAID);
        entity.setSeat("商务座");
        entity.setPrice(100);
        entity.setDiscount(0);
        entity.setPaymentType(PaymentType.WECHAT_PAY);
        entity.setId(1001L);

        OrderServiceImpl orderService = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        try{
            orderService.cancelOrder(1001L);
        }catch (Exception e){
            System.err.println("cancelOrder should throw BizException");
        }
        assert entity.getStatus().equals(OrderStatus.PAID);
    }

    @Test
    void refundOrderTest(){
        userDao.delete(userService.findByUserName("xsswsx"));
        userService.register("xsswsx","123456Aa","宋毅恒","32050120020925527X","15050196910","用户");
        assert userDao.findByUsername("xsswsx").getName().equals("宋毅恒");

        Long userID = userService.findByUserName("xsswsx").getId();
        OrderEntity entity = new OrderEntity();
        entity.setUserId(userID);
        entity.setTrainId(6L);
        entity.setDepartureStationId(11L);
        entity.setArrivalStationId(12L);
        entity.setStatus(OrderStatus.PAID);
        entity.setSeat("商务座");
        entity.setPrice(100);
        entity.setDiscount(0);
        entity.setPaymentType(PaymentType.WECHAT_PAY);
        entity.setId(1002L);

        OrderServiceImpl orderService = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        try{
            orderService.cancelOrder(1002L);
        }catch (Exception e){
            System.err.println("refundOrder throws AlipayApiException");
        }
        assert entity.getStatus().equals(OrderStatus.REFUNDED);
    }

    @Test
    void refundOrderTest2(){
        userDao.delete(userService.findByUserName("xsswsx"));
        userService.register("xsswsx","123456Aa","宋毅恒","32050120020925527X","15050196910","用户");
        assert userDao.findByUsername("xsswsx").getName().equals("宋毅恒");

        Long userID = userService.findByUserName("xsswsx").getId();
        OrderEntity entity = new OrderEntity();
        entity.setUserId(userID);
        entity.setTrainId(7L);
        entity.setDepartureStationId(13L);
        entity.setArrivalStationId(14L);
        entity.setStatus(OrderStatus.COMPLETED);
        entity.setSeat("商务座");
        entity.setPrice(100);
        entity.setDiscount(0);
        entity.setPaymentType(PaymentType.WECHAT_PAY);
        entity.setId(1003L);

        OrderServiceImpl orderService = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        try{
            orderService.cancelOrder(1003L);
        }catch (Exception e){
            System.err.println("refundOrder should throw BizException");
        }
        assert entity.getStatus().equals(OrderStatus.COMPLETED);
    }
}
