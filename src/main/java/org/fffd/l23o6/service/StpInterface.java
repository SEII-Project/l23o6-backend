package org.fffd.l23o6.service;

import java.util.List;

public interface StpInterface {
    List<String> getPermissionList(Object loginId, String loginType);

    List<String> getRoleList(Object loginId, String loginType);
}
