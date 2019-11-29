package com.zyn.mall.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zyn.mall.api.bean.search.PmsSearchParam;
import com.zyn.mall.api.bean.search.PmsSearchSkuInfo;
import com.zyn.mall.api.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhaoyanan
 * @create 2019-11-27-15:18
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private JestClient jestClient;

    @Override
    public List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam) {


        String dslStr = getDslStr(pmsSearchParam);
        System.out.println(dslStr);

        ArrayList<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();

        Search build = new Search.Builder(dslStr).addIndex("gmall0105").addType("PmsSkuInfo").build();

        try {
            SearchResult searchResult = jestClient.execute(build);
            List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = searchResult.getHits(PmsSearchSkuInfo.class);
            for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {

                PmsSearchSkuInfo pmsSearchSkuInfo = hit.source;
                Map<String, List<String>> highlight = hit.highlight;

                if(highlight!=null){
                    String skuName = highlight.get("skuName").get(0);

                    //将高亮highlight的skuName设置到pmsSearchSkuInfo中
                    pmsSearchSkuInfo.setSkuName(skuName);
                }

                pmsSearchSkuInfos.add(pmsSearchSkuInfo);
            }

            //System.out.println(pmsSearchSkuInfos.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pmsSearchSkuInfos;
    }

    private String getDslStr(PmsSearchParam pmsSearchParam) {

        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String[] skuAttrValueList = pmsSearchParam.getValueId();

        //构建es的dsl语句
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //1.1 bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        if(StringUtils.isNotBlank(catalog3Id)){
            TermQueryBuilder termQueryBuilder =
                    new TermQueryBuilder("catalog3Id", catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);

        }

        if (StringUtils.isNotBlank(keyword)){
            //1.1.2.1 match
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", keyword);
            //1.1.2 must
            boolQueryBuilder.must(matchQueryBuilder);
        }

        if (skuAttrValueList!=null){
            for (String pmsSkuAttrValue : skuAttrValueList) {
                //1.1.1.1 terms
//                TermsQueryBuilder termsQueryBuilder =
//                        new TermsQueryBuilder("skuAttrValueList.valueId", "39","40","41");

                //1.1.1.2 term
                TermQueryBuilder termQueryBuilder =
                        new TermQueryBuilder("skuAttrValueList.valueId", pmsSkuAttrValue);

                //1.1.1.3 term
//                TermQueryBuilder termQueryBuilder1 =
//                        new TermQueryBuilder("skuAttrValueList.valueId", "43");

                //1.1.1 filter
//                boolQueryBuilder.filter(termsQueryBuilder);
                boolQueryBuilder.filter(termQueryBuilder);
//                boolQueryBuilder.filter(termQueryBuilder1);
            }

        }

        //1 query
        searchSourceBuilder.query(boolQueryBuilder);

        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);

        //highlight
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.field("skuName");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlight(highlightBuilder);

        //排序
        searchSourceBuilder.sort("id", SortOrder.DESC);

        //获取es中的dsl语句

        return searchSourceBuilder.toString();
    }
}
