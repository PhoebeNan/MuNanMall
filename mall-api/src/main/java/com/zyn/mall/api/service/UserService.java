package com.zyn.mall.api.service;


import com.zyn.mall.api.bean.UmsMember;
import com.zyn.mall.api.bean.UmsMemberReceiveAddress;

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
}
