package com.nuesoft.listweb.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.neusoft.bean.po.*;
import com.neusoft.interfaces.ListService;
import com.neusoft.interfaces.MangerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
public class ListController {

    @Reference
    ListService searchService;
    @Reference
    MangerService mangerService;

    @RequestMapping("/list.html")
    public String search(SkuLsParam skuLsParam, ModelMap map){
       List<SkuLsInfo> skuLsInfoList =  searchService.search(skuLsParam);
        map.put("skuLsInfoList",skuLsInfoList);


        //封装平台属性列表,排除已经选中的属性
        List<BaseAttrInfo> baseAttrInfoList= getAttrValueIds(skuLsInfoList);

        String[] valueId = skuLsParam.getValueId();
        List<Crumb> crumbs=new ArrayList<>();//面包屑
        if (valueId!=null&&valueId.length>0){
            for (String s:valueId){
                Iterator<BaseAttrInfo> iterator = baseAttrInfoList.iterator();//迭代器
                while(iterator.hasNext()){
                    BaseAttrInfo baseAttrInfo= iterator.next();
                    List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
                    for (BaseAttrValue baseAttrValue : attrValueList) {
                        if (baseAttrValue.getId().toString().equals(s)){
                            Crumb crumb=new Crumb();
                            //制作面包屑url
                            String urlParamForCrumb = getUrlParamForCrumb(skuLsParam, s);
                            //制作面包屑name
                            String valueName= baseAttrValue.getValueName();
                            //封装面包屑
                            crumb.setValueName(valueName);
                            crumb.setUrlParam(urlParamForCrumb);
                            crumbs.add(crumb);

                            iterator.remove();//当前游标所在位置的元素删除
                        }
                    }
                }
            }
        }
        map.put("attrList",baseAttrInfoList);

        //商品筛选条件的请求路径
        String urlParam = getUrlParam(skuLsParam);
        map.put("urlParam",urlParam);

        //将封装好的面包屑传给页面
        map.put("attrValueSelectedList",crumbs);
        return "list";
    }

    /***
     *拼接面包屑url请求路径
     */
    private String getUrlParamForCrumb(SkuLsParam skuLsParam,String id) {

        String urlParam = "";
        String[] valueId = skuLsParam.getValueId();
        String keyword = skuLsParam.getKeyword();
        String catalog3Id = skuLsParam.getCatalog3Id();

        if(StringUtils.isNotEmpty(keyword)){
            if(StringUtils.isNotEmpty(urlParam)){
                urlParam = urlParam+"&keyword="+keyword;
            }else {
                urlParam = "keyword="+keyword;
            }
        }
        if(StringUtils.isNotEmpty(catalog3Id)){
            if(StringUtils.isNotEmpty(urlParam)){
                urlParam = urlParam+"&catalog3Id="+catalog3Id;
            }else {
                urlParam = "catalog3Id="+catalog3Id;
            }
        }
        if(valueId!=null&&valueId.length>0){
            for (String s:valueId){
                if (!id.equals(s)){
                    urlParam = urlParam+"&valueId="+s;
                }
            }
        }
        return urlParam;
    }
    /***
     *拼接普通url请求路径
     */
    private String getUrlParam(SkuLsParam skuLsParam) {

        String urlParam = "";
        String[] valueId = skuLsParam.getValueId();
        String keyword = skuLsParam.getKeyword();
        String catalog3Id = skuLsParam.getCatalog3Id();

        if(StringUtils.isNotEmpty(keyword)){
            if(StringUtils.isNotEmpty(urlParam)){
                urlParam = urlParam+"&keyword="+keyword;
            }else {
                urlParam = "keyword="+keyword;
            }
        }
        if(StringUtils.isNotEmpty(catalog3Id)){
            if(StringUtils.isNotEmpty(urlParam)){
                urlParam = urlParam+"&catalog3Id="+catalog3Id;
            }else {
                urlParam = "catalog3Id="+catalog3Id;
            }
        }
        if(valueId!=null&&valueId.length>0){
            for (String s:valueId){
                urlParam = urlParam+"&valueId="+s;
            }
        }
        return urlParam;
    }

    /***
     *sku的平台属性
     */
    private List<BaseAttrInfo> getAttrValueIds(List<SkuLsInfo> skuLsInfoList) {
        Set<String> valueIds=new HashSet<>();//自动去重
        for (SkuLsInfo skuLsInfo : skuLsInfoList) {
            List<SkuLsAttrValue> skuAttrValueList = skuLsInfo.getSkuAttrValueList();
            for (SkuLsAttrValue skuLsAttrValue : skuAttrValueList) {
                String valueId = skuLsAttrValue.getValueId();
                valueIds.add(valueId);
            }
        }
        //根据去重后的id集合检索,关联到的平台属性列表
        List<BaseAttrInfo> baseAttrInfoList= mangerService.getAttrListByValueIds(valueIds);
        return baseAttrInfoList;
    }

    @RequestMapping("/toindex")
    public String index(){
        return "index";
    }
}
