package com.nuesoft.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.neusoft.bean.po.SkuInfo;
import com.neusoft.bean.po.SkuSaleAttrValue;
import com.neusoft.bean.po.SpuSaleAttr;
import com.neusoft.interfaces.MangerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Reference
    MangerService mangerService;

    @RequestMapping("/todemo")
    public String ff(){
        return "demo";
    }

    @RequestMapping("/{skuId}.html")
    public String item(@PathVariable Long skuId, ModelMap map){
        SkuInfo skuInfo=mangerService.getSkuById(skuId);
        map.put("skuInfo",skuInfo);
        Long spuId = skuInfo.getSpuId();
        System.out.println(spuId);
       // getSpuId当前的sku所包含的销售属性
     //   List<SkuSaleAttrValue> SkuSaleAttrValueskuSaleAttrValueList=skuInfo.getSkuSaleAttrValueList();

        //spu的销售属性列表
//        List<SpuSaleAttr> spuSaleAttrList = mangerService.selcetAllSpuSaleAttr(spuId);
//        map.put("spuSaleAttrListCheckBySku",spuSaleAttrList);

        //spu的sku和销售属性对应关系的hash表
        List<SkuInfo> skuInfoList=mangerService.getSkuSaleAttrValueListBySpu(spuId);

       Map<String, Long> kvmap = new HashMap<>();
        for (SkuInfo info : skuInfoList) {
            System.out.println(info);
            Long v = info.getId();

            List<SkuSaleAttrValue> skuSaleAttrValueList=info.getSkuSaleAttrValueList();
            String k = "";
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                k = k + "|" +skuSaleAttrValue.getSaleAttrValueId();
            }
            kvmap.put(k,v);
        }
        String skuJson = JSON.toJSONString(kvmap);
        map.put("skuJson",skuJson);

        //销售属性列表
        Map<String, Long> stringLongHashMap = new HashMap<>();
        stringLongHashMap.put("spuId",spuId);
        stringLongHashMap.put("skuId",skuId);
        List<SpuSaleAttr> spuSaleAttrListCheckBySku = mangerService.getSpuSaleAttrListCheckBySku(stringLongHashMap);

        map.put("spuSaleAttrListCheckBySku",spuSaleAttrListCheckBySku);
        return "item";
    }
}
