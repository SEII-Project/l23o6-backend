package org.fffd.l23o6;

import org.fffd.l23o6.dao.*;
import org.fffd.l23o6.pojo.entity.*;
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

    @Test
    void createOrderTest1() {
        userDao.delete(userService.findByUserName("xsswsx"));
        userService.register("xsswsx","123456Aa","宋毅恒","32050120020925527X","15050196910","用户");
        assert userDao.findByUsername("xsswsx").getName().equals("宋毅恒");

        routeService.addRoute("京南线",new ArrayList<>(Arrays.asList(1L,2L,3L)));
        Long routeID = -1L;
        for (int i = 0 ; i < routeService.listRoutes().size(); i++){
            if (routeService.listRoutes().get(i).getName().equals("京广线")){
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
        for (int i = 0 ; i < trainService.listTrains(1L,3L,"2023-7-8").size() ; i++){
            if (trainService.listTrains(1L,3L,"2023-7-8").get(i).getName().equals("京南线")){
                trainID = trainService.listTrains(1L,3L,"2023-7-8").get(i).getId();
                break;
            }
        }
        assert trainID != -1L;
        assert trainDao.existsById(trainID);
        OrderServiceImpl impl = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        impl.createOrder("xsswsx",1L,1L,3L,"一等座",1L,50);
    }
}
