package com.zyn.mall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyn.mall.api.bean.sku.PmsSearchSkuInfo;
import com.zyn.mall.api.bean.sku.PmsSkuInfo;
import com.zyn.mall.api.service.PmsSkuInfoService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallSearchServiceApplicationTests {

    @Reference
    private PmsSkuInfoService skuInfoService;

    @Autowired
    private JestClient jestClient;

    @Test
    public void contextLoads() {

        //从mysql中查询出sku商品全部数据
        List<PmsSkuInfo> skuInfos = skuInfoService.getSkuAll("61");

        //把mysql中的数据转化为es中去
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();

        for (PmsSkuInfo skuInfo : skuInfos) {

            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(skuInfo, pmsSearchSkuInfo);
            pmsSearchSkuInfos.add(pmsSearchSkuInfo);
        }

        //导入es
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            //index:对应es的索引,type对应es中的表名
            Index build = new Index.Builder(pmsSearchSkuInfo).index("gmall0105").type("PmsSkuInfo").id(pmsSearchSkuInfo.getId()).build();
            try {
                jestClient.execute(build);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}
