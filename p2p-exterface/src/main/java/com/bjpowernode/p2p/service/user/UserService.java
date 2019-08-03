package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;

public interface UserService {
    Long queryAllUserCount();

    User queryUserByPhone(String phone);

    ResultObject register(String phone, String loginPassword);

    int modifyUserById(User user);

    User login(String loginPassword, String phone);
}
