package com.neusoft.listservice;

import com.alibaba.dubbo.config.annotation.Reference;
import com.neusoft.bean.po.SkuInfo;
import com.neusoft.bean.po.SkuLsInfo;
import com.neusoft.interfaces.MangerService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.beanutils.BeanUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ListServiceApplicationTests {

    @Autowired
    JestClient jestClient;

    @Reference
    MangerService mangerService;


    public static String getMyDsl(){
        //创建一个dsl工具对象
        SearchSourceBuilder dsl = new SearchSourceBuilder(); //{}
        //创建一个先过滤后搜索的query对象
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();//bool
        //过滤语句 filter terms
        TermQueryBuilder t1=new TermQueryBuilder("catalog3Id","1");
        boolQueryBuilder.filter(t1);
        TermQueryBuilder t2=new TermQueryBuilder("skuAttrValueList.valueId","57");
        boolQueryBuilder.filter(t2);
        TermQueryBuilder t3=new TermQueryBuilder("skuAttrValueList.valueId","55");
        boolQueryBuilder.filter(t3);
        //搜索语句 match
        MatchQueryBuilder matchQueryBuilder=new MatchQueryBuilder("skuName","华为");
        boolQueryBuilder.must(matchQueryBuilder);
        //将query和from的size放入dsl
        dsl.query(boolQueryBuilder);
        dsl.size(100);
        dsl.from(0);

        return dsl.toString();
    }

    @Test
    public void search() {
        //如何查询es中的数据
        Search search=new Search.Builder(getMyDsl()).addIndex("gmall0328").addType("SkuLsInfo").build() ;
        List<SkuLsInfo> skuLsInfos=new ArrayList<>();
        try {
            SearchResult execute = jestClient.execute(search);
            List<SearchResult.Hit<SkuLsInfo,Void>> hits = execute.getHits(SkuLsInfo.class);
            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
                SkuLsInfo source = hit.source;
                skuLsInfos.add(source);
            }
            System.out.println(skuLsInfos.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void contextLoads(){
        //查询MySQL中的sku信息
        List<SkuInfo> skuInfoList = mangerService.getSkuListByCatalog3Id(1l);
        //转化es中的sku信息
        List<SkuLsInfo> skuLsInfoList=new ArrayList<>();
        for (SkuInfo skuInfo : skuInfoList) {
            SkuLsInfo skuLsInfo=new SkuLsInfo();
            try {
                BeanUtils.copyProperties(skuLsInfo,skuInfo);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            skuLsInfoList.add(skuLsInfo);
        }
        //导入到es中
        for (SkuLsInfo skuLsInfo : skuLsInfoList) {
            String id=skuLsInfo.getId();
            Index build = new Index.Builder(skuLsInfo).index("gmall0328").type("SkuLsInfo").id(id).build();
            try {
                jestClient.execute(build);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
