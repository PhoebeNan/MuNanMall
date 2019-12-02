package com.zyn.mall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.zyn.mall.api.bean.cart.OmsCartItem;
import com.zyn.mall.api.service.CartService;
import com.zyn.mall.cart.service.mapper.CartServiceMapper;
import com.zyn.mall.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhaoyanan
 * @create 2019-12-02-14:27
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartServiceMapper cartServiceMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public OmsCartItem ifCartExitByUser(String memberId, String skuId) {

        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        OmsCartItem cartItem = cartServiceMapper.selectOne(omsCartItem);
        return cartItem;
    }

    @Override
    public void addCart(OmsCartItem omsCartItem) {

        if (StringUtils.isNotBlank(omsCartItem.getMemberId())) {
            cartServiceMapper.insertSelective(omsCartItem);
        }
    }

    @Override
    public void setCart(OmsCartItem omsCartItemFromDb) {

        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("id", omsCartItemFromDb.getId());
        cartServiceMapper.updateByExampleSelective(omsCartItemFromDb, example);
    }

    @Override
    public void flushCartCache(String memberId) {

        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        List<OmsCartItem> omsCartItems = cartServiceMapper.select(omsCartItem);

        Jedis jedis = redisUtils.getJedis();

        try {
            Map<String, String> cartMapValue = new HashMap<>();
            for (OmsCartItem cartItem : omsCartItems) {

                //设置map中的键和值
                cartMapValue.put(cartItem.getProductSkuId(), JSON.toJSONString(cartItem));
            }

            //设置map的键
            String cartMapKey = "user:" + memberId + ":cart";
            jedis.hmset(cartMapKey,cartMapValue);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }

    }
}
