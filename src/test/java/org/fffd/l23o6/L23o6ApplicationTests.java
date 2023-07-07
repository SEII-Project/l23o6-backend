package org.fffd.l23o6;

import org.fffd.l23o6.dao.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class L23o6ApplicationTests {
    @Autowired
    UserDao userDao;

    @Test
    void contextLoads() {
        userDao.save(null);
    }

}
