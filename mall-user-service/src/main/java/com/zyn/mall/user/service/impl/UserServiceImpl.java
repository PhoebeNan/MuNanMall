package com.zyn.mall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.zyn.mall.api.bean.user.UmsMember;
import com.zyn.mall.api.bean.user.UmsMemberReceiveAddress;
import com.zyn.mall.api.service.UserService;
import com.zyn.mall.user.mapper.UmsMemberReceiveAddressMapper;
import com.zyn.mall.user.mapper.UserMapper;
import com.zyn.mall.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


/**
 * @author zhaoyanan
 * @create 2019-10-30-9:13
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public List<UmsMember> getAllUser() {

        List<UmsMember> umsMembers = userMapper.selectAll();
        return umsMembers;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {

        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("memberId", memberId);
        List<UmsMemberReceiveAddress> UmsMemberReceiveAddress =
                umsMemberReceiveAddressMapper.selectByExample(example);
        return UmsMemberReceiveAddress;
    }


    @Override
    public UmsMember login(UmsMember umsMember) {

        //从redis中查询用户信息
        Jedis jedis = null;
        String redisKey = "user:" + umsMember.getPassword() + umsMember.getUsername() + ":info";

        try {
            jedis = redisUtils.getJedis();


            if (jedis != null) {
                //从redis中查询密码
                String password = jedis.get(redisKey);

                if (password != null) {
                    //登录成功
                    UmsMember umsMemberFromCache = JSON.parseObject(password, UmsMember.class);
                    return umsMemberFromCache;

                }
            }

            //登录失败 ①密码错误②redis缓存中没有，开mysql数据库，开启数据库从数据库中查询
            //可能事redis服务器宕机了，redis没有连接上，开启数据库从数据库中查询
            UmsMember memberFromDb = umsMemberFromDb(umsMember);
            if (memberFromDb != null) {
                jedis.setex(redisKey, 60 * 60 * 24, JSON.toJSONString(memberFromDb));
            }
            return memberFromDb;


        } finally {
            jedis.close();
        }

    }

    @Override
    public void addUserTokenToCache(String token, String userId) {
        //从redis中查询用户信息
        Jedis jedis = null;
        String tokenKey = "user:" + userId + ":token";

        try {
            jedis = redisUtils.getJedis();
            jedis.setex(tokenKey, 60 * 60 * 4, token);
        } finally {
            jedis.close();
        }

    }

    @Override
    public UmsMember addAuthUserToDb(UmsMember umsMember) {
        userMapper.insertSelective(umsMember);

        return umsMember;
    }

    @Override
    public UmsMember checkAuthUser(UmsMember checkUser) {

        UmsMember umsMember = userMapper.selectOne(checkUser);
        return umsMember;
    }

    /**
     * redis缓存中没有或redis宕机了，从数据库中查询用户信息
     *
     * @param umsMember
     * @return
     */
    private UmsMember umsMemberFromDb(UmsMember umsMember) {

        List<UmsMember> umsMembers = userMapper.select(umsMember);
        if (umsMembers != null && umsMembers.size() > 0) {
            return umsMembers.get(0);
        }

        return null;
    }
}
