package com.zyn.mall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.zyn.mall.api.bean.sku.PmsSkuAttrValue;
import com.zyn.mall.api.bean.sku.PmsSkuImage;
import com.zyn.mall.api.bean.sku.PmsSkuInfo;
import com.zyn.mall.api.bean.sku.PmsSkuSaleAttrValue;
import com.zyn.mall.api.service.PmsSkuInfoService;
import com.zyn.mall.manager.mapper.PmsSkuAttrValueMapper;
import com.zyn.mall.manager.mapper.PmsSkuImageMapper;
import com.zyn.mall.manager.mapper.PmsSkuInfoMapper;
import com.zyn.mall.manager.mapper.PmsSkuSaleAttrValueMapper;
import com.zyn.mall.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author zhaoyanan
 * @create 2019-11-14-9:48
 */
@Service
public class PmsSkuInfoImpl implements PmsSkuInfoService {

    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {

        //如何前台传递过来的pmsSkuInfo对象中没有选择图片的默认值，那么后台可以给用户设置一个默认值
        String skuDefaultImg = pmsSkuInfo.getSkuDefaultImg();
        if (StringUtils.isBlank(skuDefaultImg)) {

            String imgUrl = pmsSkuInfo.getSkuImageList().get(0).getImgUrl();
            pmsSkuInfo.setSkuDefaultImg(imgUrl);
            pmsSkuInfo.getSkuImageList().get(0).setIsDefault("1");

        }

        //保存skuInfo
        pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());
        pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        //获取sku主键Id值，此值为下面三个的外键，并赋予设置
        String skuInfoId = pmsSkuInfo.getId();
        //保存此sku下关联的平台属性
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {

            pmsSkuAttrValue.setSkuId(skuInfoId);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }
        //保存此sku下关联的销售属性
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {

            pmsSkuSaleAttrValue.setSkuId(skuInfoId);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }
        //保存此sku下管理的图片
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();

        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(skuInfoId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }


    }

    @Override
    public PmsSkuInfo skuInfoBySkuIdFromDb(String skuId) {

        //商品数据
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo info = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
        info.setSpuId(info.getProductId());
        //图片列表
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
        info.setSkuImageList(pmsSkuImages);

        return info;
    }

    @Override
    public PmsSkuInfo skuInfoBySkuIdFromRedis(String skuId, String ip) {

        System.out.println("ip为" + ip + "的同学:" + Thread.currentThread().getName() + "进入商品详情的请求");
        PmsSkuInfo pmsSkuInfo = null;
        Jedis jedis = null;
        try {
            //连接redis缓存
            pmsSkuInfo = new PmsSkuInfo();
            jedis = redisUtils.getJedis();
            //从缓存中查询数据
            String sku_key = "sku:" + skuId + ":info";
            String sku_key_json = jedis.get(sku_key);
            String sku_key_lock = "sku:" + skuId + ":lock";

            if (StringUtils.isNotBlank(sku_key_json)) {
                pmsSkuInfo = JSON.parseObject(sku_key_json, PmsSkuInfo.class);
            } else {

                //设置token值为分布式锁的value
                String token = UUID.randomUUID().toString();
                System.out.println("ip为" + ip + "的同学:" + Thread.currentThread().getName() + "发现缓存redis中没有，请求分布式锁" + sku_key_lock);
                //若缓存中没有数据则查询mysql
                //设置分布式锁
                String nx = jedis.set(sku_key_lock, token, "nx", "px", 10 * 1000);
                if (StringUtils.isNotBlank(nx) && nx.equals("OK")) {
                    //成功拿到锁设置成功，有权在10秒的过期时间内访问MySQL数据库
                    System.out.println("ip为" + ip + "的同学:" + Thread.currentThread().getName() + "有权在10秒的过期时间内访问MySQL数据库" + sku_key_lock);
                    pmsSkuInfo = skuInfoBySkuIdFromDb(skuId);

                    //从数据库获取到数据后先睡眠5秒，然后再把pmsSkuInfo对象设置到redis缓存中
                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //将mysql的查询结果存入缓存
                    if (pmsSkuInfo != null) {
                        jedis.set(sku_key, JSON.toJSONString(pmsSkuInfo));
                    } else {
                        //防止缓存穿透，将null或者空字符串设置给redis缓存数据库
                        jedis.setex(sku_key, 60 * 3, JSON.toJSONString(""));
                    }

                    System.out.println("ip为" + ip + "的同学:" + Thread.currentThread().getName() + "使用完毕，将锁归还" + sku_key_lock);
                    String lockValue = jedis.get(sku_key_lock);
                    if (StringUtils.isNotBlank(lockValue) && lockValue.equals(token)) {
                        //释放分布式锁
                        //jedis.eval("lua"); 可以用lua脚本在查询到key的同时删除key，防止高并发下的意外发生
                        String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                        jedis.eval(script, Collections.singletonList(sku_key_lock),Collections.singletonList(token));
                        //jedis.del(sku_key_lock);
                    }
                } else {
                    //设置失败,将该线程睡眠几秒钟后，重新尝试访问此方法
                    System.out.println("ip为" + ip + "的同学:" + Thread.currentThread().getName() + "没有拿到锁，开始自旋");
                    try {
                        Thread.sleep(3 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return skuInfoBySkuIdFromRedis(skuId, ip);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭redis缓存
            //assert jedis != null;
            jedis.close();
        }

        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String spuId) {

        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(spuId);
        return pmsSkuInfos;
    }

    @Override
    public List<PmsSkuInfo> getSkuAll(String catalog3Id) {

        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            String skuId = pmsSkuInfo.getId();

            PmsSkuAttrValue pmsSkuAttrValue= new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(skuId);
            List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);

            pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValues);
        }

        return pmsSkuInfos;
    }
}
