package org.fffd.l23o6.pojo.vo.user;

import lombok.Data;
import org.fffd.l23o6.pojo.enum_.UserType;

@Data
public class UserVO {
    private String username;
    private String name;
    private String phone;
    private String idn;
    private String type;
    private int credits;
    private double deposit;
    private UserType userType;
}
