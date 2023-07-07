package org.fffd.l23o6;

import com.alipay.api.AlipayApiException;
import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.pojo.enum_.UserType;
import org.fffd.l23o6.service.OrderService;
import org.fffd.l23o6.service.impl.OrderServiceImpl;
import org.fffd.l23o6.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OrderServiceImplTests {
    @Autowired
    UserDao userDao;
    @Autowired
    OrderServiceImpl orderService;
    @Autowired
    UserServiceImpl userService;
    @Test
    void createOrderTest1() throws AlipayApiException {
        userService.register("test", "Abcd1234", "测试", "111111111", "13333333333", "管理员");
        assert userDao.findByUsername("test").getName().equals("测试");
    }
}
