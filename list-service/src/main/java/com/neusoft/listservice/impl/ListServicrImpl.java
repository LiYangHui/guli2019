package com.neusoft.listservice.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.neusoft.bean.po.SkuLsInfo;
import com.neusoft.bean.po.SkuLsParam;
import com.neusoft.interfaces.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.util.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ListServicrImpl implements ListService {
    @Autowired
    JestClient jestClient;

    @Override
    public List<SkuLsInfo> search(SkuLsParam skuLsParam) {

        //如何查询es中的数据
        Search search=new Search.Builder(getMyDsl(skuLsParam)).addIndex("gmall0328").addType("SkuLsInfo").build() ;
        List<SkuLsInfo> skuLsInfos=new ArrayList<>();

        try {
            SearchResult execute = jestClient.execute(search);
            List<SearchResult.Hit<SkuLsInfo,Void>> hits = execute.getHits(SkuLsInfo.class);
            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
                SkuLsInfo source = hit.source;
//                Map<String, List<String>> highlight = hit.highlight;
//                List<String> skuName = highlight.get("skuName");
//                String s = skuName.get(0);
//                source.setSkuName(s);
                skuLsInfos.add(source);
            }
          System.out.println(skuLsInfos.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return skuLsInfos;
    }

    //拼接语句
    public String getMyDsl(SkuLsParam skuLsParam){
        //用户所选择的过滤条件
        String keyword = skuLsParam.getKeyword();//关键字
        String[] valueId = skuLsParam.getValueId();//属性值ID
        String catalog3Id = skuLsParam.getCatalog3Id();//3级分类ID
        //创建一个dsl工具对象
        SearchSourceBuilder dsl = new SearchSourceBuilder(); //{}
        //创建一个先过滤后搜索的query对象
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();//bool
        //query过滤语句 filter terms
        if(StringUtil.isNotEmpty(catalog3Id)){
            TermQueryBuilder t = new TermQueryBuilder("catalog3Id", catalog3Id);
            boolQueryBuilder.filter(t);
        }
        if(null!=valueId && valueId.length>0) {
            for (int i = 0; i < valueId.length; i++) {
                TermQueryBuilder t = new TermQueryBuilder("skuAttrValueList.valueId", valueId[i]);
                boolQueryBuilder.filter(t);
            }
        }
//        TermQueryBuilder t2=new TermQueryBuilder("skuAttrValueList.valueId","57");
//        boolQueryBuilder.filter(t2);
//        TermQueryBuilder t3=new TermQueryBuilder("skuAttrValueList.valueId","55");
//        boolQueryBuilder.filter(t3);
        //query对象搜索语句 match
        if(StringUtil.isNotEmpty(keyword)){
            MatchQueryBuilder matchQueryBuilder=new MatchQueryBuilder("skuName",keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }
        //将query和from的size放入dsl
        dsl.query(boolQueryBuilder);
        dsl.size(100);
        dsl.from(0);
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        highlightBuilder.field("skuName");
//        highlightBuilder.preTags("<span style='color:red;font-weight:bolder;'");
//        highlightBuilder.preTags("</span>");
//        dsl.highlight(highlightBuilder);


        System.out.println(dsl.toString());
        return dsl.toString();
    }


}
