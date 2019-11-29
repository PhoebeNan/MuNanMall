package com.zyn.mall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyn.mall.api.bean.search.PmsSearchSkuInfo;
import com.zyn.mall.api.bean.sku.PmsSkuInfo;
import com.zyn.mall.api.service.PmsSkuInfoService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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

        //构建es的dsl语句
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //1.1 bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        //1.1.1.1 terms
        TermsQueryBuilder termsQueryBuilder =
                new TermsQueryBuilder("skuAttrValueList.valueId", "39","40","41");

        //1.1.1.2 term
        TermQueryBuilder termQueryBuilder =
                new TermQueryBuilder("skuAttrValueList.valueId", "39");

        //1.1.1.3 term
        TermQueryBuilder termQueryBuilder1 =
                new TermQueryBuilder("skuAttrValueList.valueId", "43");

        //1.1.1 filter
        boolQueryBuilder.filter(termsQueryBuilder);
        boolQueryBuilder.filter(termQueryBuilder);
        boolQueryBuilder.filter(termQueryBuilder1);

        //1.1.2.1 match
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", "美颜");
        //1.1.2 must
        boolQueryBuilder.must(matchQueryBuilder);

        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);

        //1 query
        searchSourceBuilder.query(boolQueryBuilder);

        //获取es中的dsl语句
        String dslStr = searchSourceBuilder.toString();

        System.out.println(dslStr);


        ArrayList<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();

        Search build = new Search.Builder(dslStr).addIndex("gmall0105").addType("PmsSkuInfo").build();

        try {
            SearchResult searchResult = jestClient.execute(build);
            List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = searchResult.getHits(PmsSearchSkuInfo.class);
            for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {

                PmsSearchSkuInfo pmsSearchSkuInfo = hit.source;

                pmsSearchSkuInfos.add(pmsSearchSkuInfo);
            }

            System.out.println(pmsSearchSkuInfos.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向es数据库中导入mysql数据库中的商品数据
     */
    @Test
    public void put(){
        //从mysql中查询出sku商品全部数据
        List<PmsSkuInfo> skuInfos = skuInfoService.getSkuAll("61");

        //把mysql中的数据转化为es中去
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();

        for (PmsSkuInfo skuInfo : skuInfos) {

            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(skuInfo, pmsSearchSkuInfo);

            pmsSearchSkuInfo.setId(Long.parseLong(skuInfo.getId()));
            pmsSearchSkuInfos.add(pmsSearchSkuInfo);
        }

        //导入es
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            //index:对应es的索引,type对应es中的表名
            Index build = new Index.Builder(pmsSearchSkuInfo).index("gmall0105").type("PmsSkuInfo").id(pmsSearchSkuInfo.getId()+"").build();
            try {
                jestClient.execute(build);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
