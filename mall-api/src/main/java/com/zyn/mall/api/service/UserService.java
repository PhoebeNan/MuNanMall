package com.zyn.mall.api.service;


import com.zyn.mall.api.bean.user.UmsMember;
import com.zyn.mall.api.bean.user.UmsMemberReceiveAddress;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-10-30-9:13
 */
public interface UserService {

    /**
     * 查询UmsMember表中的数据信息
     *
     * @return
     */
    List<UmsMember> getAllUser();


    /**
     * 通过用户id获取接收地址
     * @param memberId
     * @return
     */
    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);

    /**
     * 先从redis中查询用户信息，并验证用户名和密码是否正确
     * @param umsMember
     * @return
     */
    UmsMember login(UmsMember umsMember);

    /**
     *将token存入redis一份
     * @param token
     * @param userId
     */
    void addUserTokenToCache(String token, String userId);
}
