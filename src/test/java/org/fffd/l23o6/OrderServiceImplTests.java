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
    TrainDao trainDao;
    @Autowired
    RouteDao routeDao;
    @Test
    void createOrderTest1() {
        UserEntity userEntity = new UserEntity();
        TrainEntity trainEntity = new TrainEntity();
        RouteEntity routeEntity = new RouteEntity();
        if (userDao.findByUsername("xsswsx") == null){
            userEntity.setUsername("xsswsx");
            userEntity.setPassword("123456Aa");
            userDao.save(userEntity);
        }
        if (!routeDao.existsById(1L)){
            routeEntity.setId(1L);
            routeEntity.setName("京广线");
            routeEntity.setStationIds(new ArrayList<>(Arrays.asList(1L,2L,3L)));
            routeEntity.setId(1L);
            routeDao.save(routeEntity);
        }
        if (!trainDao.existsById(1L)){
            trainEntity.setId(1L);
            trainEntity.setName("G0001");
            trainEntity.setRouteId(routeEntity.getId());
            boolean[][] seat_stub = {{true,true,true,true},{true,true,true,true},{true,true,true,true}};
            trainEntity.setSeats(seat_stub);
            trainEntity.setTrainType(TrainType.HIGH_SPEED);
            trainEntity.setDate("2023-7-7");
            trainEntity.setArrivalTimes(new ArrayList<>(Arrays.asList(
                    new Date(2023,Calendar.JULY,8,20,0),
                    new Date(2023,Calendar.JULY,8,21,0),
                    new Date(2023,Calendar.JULY,8,22,0))));
            trainEntity.setDepartureTimes(new ArrayList<>(Arrays.asList(
                    new Date(2023,Calendar.JULY,7,20,0),
                    new Date(2023,Calendar.JULY,8,21,10),
                    new Date(2023,Calendar.JULY,8,22,0))));
            trainEntity.setExtraInfos(new ArrayList<>(Arrays.asList(TrainStatus.ON_TIME,TrainStatus.ON_TIME,TrainStatus.DELAY)));
            trainDao.save(trainEntity);
        }
        OrderServiceImpl impl = new OrderServiceImpl(orderDao,userDao,trainDao,routeDao);
        impl.createOrder("xsswsx",1L,1L,3L,"一等座",13L,50);
    }
}
