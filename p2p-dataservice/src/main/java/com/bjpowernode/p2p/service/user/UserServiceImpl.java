package com.bjpowernode.p2p.service.user;


import com.bjpowernode.p2p.common.constants.Constants;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.user.UserMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

//    定义redis模板对象

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Override
    public Long queryAllUserCount() {

        //从redis中取数据
        Long allUserCount = (Long) redisTemplate.opsForValue().get(Constants.ALL_USER_COUNT);

        if(null == allUserCount){

            //同步代码块
            synchronized (this){

                allUserCount = (Long) redisTemplate.opsForValue().get(Constants.ALL_USER_COUNT);

                //二次检测
                if(null == allUserCount){
                    allUserCount = userMapper.selectAllUserCount();
                    //将值添加到redis缓存中
                    redisTemplate.opsForValue().set(Constants.ALL_USER_COUNT, allUserCount, 15, TimeUnit.MINUTES);

                    System.out.println("数据库中取值");
                }else {
                    System.out.println("redis中取值");
                }

            }
            System.out.println("数据库中取值");
        }else{
            System.out.println("redis中取值");
        }

        return allUserCount;

}

    @Override
    public User queryUserByPhone(String phone) {
        return userMapper.selectqueryUserByPhone(phone);

    }

    @Override
    public ResultObject register(String phone, String loginPassword) {
        ResultObject resultObject = new ResultObject();
//        先给结果初始化
        resultObject.setErrorCode(Constants.SUCCESS);

//        新增用户，进行注册
        User user = new User();

        user.setPhone(phone);
        user.setLoginPassword(loginPassword);
        user.setAddTime(new Date());
        user.setLastLoginTime(new Date());
        int userCount = userMapper.insertSelective(user);
        if(userCount > 0){
//            用户添加成功，并且新增资金账户
//            resultObject.setErrorCode(Constants.SUCCESS);
            FinanceAccount financeAccount = new FinanceAccount();
//            资金账户中的uid需要从用户信息表中获取
            User userInfo = userMapper.selectqueryUserByPhone(phone);
            financeAccount.setUid(userInfo.getId());
            financeAccount.setAvailableMoney(888.0);
            int financeAccountCount =  financeAccountMapper.insertSelective(financeAccount);
            if(financeAccountCount <= 0){
                resultObject.setErrorCode(Constants.FAIL);

            }

        }else{
//            添加失败，返回错误信息
            resultObject.setErrorCode(Constants.FAIL);

        }
        return resultObject;
    }

    @Override
    public int modifyUserById(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public User login(String loginPassword, String phone) {
        User user = userMapper.selectUserByPhoneAndLoginPassword(loginPassword,phone);

        if(null != user){
            User updateUser = new User();
            updateUser.setLastLoginTime(new Date());
            updateUser.setId(user.getId());
            userMapper.updateByPrimaryKeySelective(updateUser);
        }

        return user;
    }

}
