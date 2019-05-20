package com.neusoft.userservice.serviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.neusoft.bean.po.UserInfo;
import com.neusoft.interfaces.UserService;
@Service
public class UserServiceImpl  implements UserService {
    @Override
    public int addUser(UserInfo userInfo) {
        return 1;
    }
}
