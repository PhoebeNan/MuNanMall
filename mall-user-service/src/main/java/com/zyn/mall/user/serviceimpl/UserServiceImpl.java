package com.zyn.mall.user.serviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zyn.mall.api.bean.UmsMember;
import com.zyn.mall.api.bean.UmsMemberReceiveAddress;
import com.zyn.mall.api.service.UserService;
import com.zyn.mall.user.mapper.UmsMemberReceiveAddressMapper;
import com.zyn.mall.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public List<UmsMember> getAllUser() {

        List<UmsMember> umsMembers = userMapper.selectAll();
        return umsMembers;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {

        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("memberId",memberId);
        List<UmsMemberReceiveAddress> UmsMemberReceiveAddress =
                umsMemberReceiveAddressMapper.selectByExample(example);
        return UmsMemberReceiveAddress;
    }
}
