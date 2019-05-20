package com.neusoft.mangerweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.neusoft.bean.po.*;
import com.neusoft.interfaces.MangerService;

import com.neusoft.mangerweb.util.MyUploadUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class MangerController {

   @Reference
   MangerService mangerService;

    @RequestMapping("/toindex")
    public String toindexff(){
        return "index";
    }
    @RequestMapping("/toattrListPage")
    public String toattrListPageff(){
        return "attrListPage";
    }
    @RequestMapping("/tospuInfoPage")
    public String tospuInfoPage(){
        return "spuInfoPage";
    }
    @RequestMapping("/tospuListPage")
    public String tospuListPage(){
        return "spuListPage";
    }
    //查询所有1级分类
   @ResponseBody
   @RequestMapping("/getCatalog1")
   public List<BaseCatalog1> selectBase1(){
      List<BaseCatalog1> baseCatalog1List = mangerService.selectAllBase1();
      return baseCatalog1List;
   }
    //通过1级分类Id查询所有2级分类
    @ResponseBody
    @RequestMapping("/getCatalog2")
    public List<BaseCatalog2> selectBase2(String catalog1Id){
        List<BaseCatalog2> baseCatalog2List = mangerService.selectAllBase2(Integer.parseInt(catalog1Id));
        return baseCatalog2List;
    }
    //通过2级分类Id查询所有3级分类
    @ResponseBody
    @RequestMapping("/getCatalog3")
    public List<BaseCatalog3> selectBase3(@RequestParam(value = "catalog2Id",required = false,defaultValue = "0") String base2id){
        List<BaseCatalog3> baseCatalog3List = mangerService.selectAllBase3(Integer.parseInt(base2id));
        return baseCatalog3List;
    }
    //通过3级分类Id查询所有BaseAttrInfo
    @ResponseBody
    @RequestMapping("/getAttrList")
    public List<BaseAttrInfo> selectAttrpuInfo(@RequestParam(value = "catalog3Id",required = false,defaultValue = "0") String base3id){
        List<BaseAttrInfo> baseAttrInfoList = mangerService.selectAll(Integer.parseInt(base3id));
        return baseAttrInfoList;
    }

    //上传图片
    @RequestMapping("/fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile file){
        //fdfs的上传工具 同时需要辅助文件tracker.conf
        String imgUrl= MyUploadUtil.uploadImage(file);
        return imgUrl;
    }
    //保存AttrValue
    @RequestMapping("/saveAttr")
    public String addAttrValue(BaseAttrInfo attrInfo){
        int i = mangerService.addBaseAttrValue(attrInfo);
        return "forward:toindex";
    }
    //删除attrInfo
    @RequestMapping("/todeleteInfo")
    public String deleteAttrInfo(String ainfoid){
        int i = mangerService.deleteBaseInfo(Integer.parseInt(ainfoid));
        return "success";
    }
    //查询所有BaseAttrValue
    @ResponseBody
    @RequestMapping("/toselectValue")
    public List<BaseAttrValue> selectValue(String ainfoid){
        System.out.println(ainfoid);
        List<BaseAttrValue> baseAttrValueList = mangerService.selectAllValue(Integer.parseInt(ainfoid));
        return baseAttrValueList;
    }

    //查询所有SpuInfo
    @ResponseBody
    @RequestMapping("/spuList")
    public List<SpuInfo> selectSpuInfo(@RequestParam(value = "catalog3Id",required = false,defaultValue = "0")String base3id){
        List<SpuInfo> spuInfoList=mangerService.selectAllSpuInfo(Integer.parseInt(base3id));
        return spuInfoList;
    }

    //查询所有BaseSaleAttr
    @ResponseBody
    @RequestMapping("/baseSaleAttrList")
    public List<BaseSaleAttr> selectBaseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrList=mangerService.selectAllBaseSaleAttr();
        return baseSaleAttrList;
    }
    @ResponseBody
    @RequestMapping("/saveSpu")
    public String addSpuInfo(SpuInfo spuInfo){

        if(spuInfo.getId()!=null){
            //修改Spu
           mangerService.updateSpuInfos(spuInfo);
           return "修改success";
        }else {
            //添加Spu
           mangerService.addSpuInfos(spuInfo);
            return "添加success";
        }
    }
    //删除Spu
    @ResponseBody
    @RequestMapping("/todeleteSpuInfo")
    public void deleteSpuInfo(Long spuId){
       mangerService.deleteSpuInfos(spuId);
    }

    //查询某个Spu
    @ResponseBody
    @RequestMapping("/showSpuId")
    public SpuInfo selectSpu(Long spuId){
        SpuInfo spuOne = mangerService.selectSpus(spuId);
        return spuOne;
    }

    //根据SpuInfo的spuId查询Sku
    @ResponseBody
    @RequestMapping("/getSkuListBySpu")
    public List<SkuInfo> selectSku(Long spuId){
        List<SkuInfo> skuInfoList = mangerService.selectAllSkuInfo(spuId);
        return skuInfoList;
    }

    //获取图片getSpuImageListBySpuId
    @ResponseBody
    @RequestMapping("/getSpuImageListBySpuId")
    public List<SpuImage> selectSpuImageListBySpuId(Long spuId){
        List<SpuImage> spuImageList = mangerService.selcetAllSpuImage(spuId);
        return spuImageList;
    }

    //获取平台属性
    @ResponseBody
    @RequestMapping("/getAttrListByCtg3Id")
    public List<BaseAttrInfo> selectSaleAttrListBySpuId(Long spuId){
        List<BaseAttrInfo> baseAttrInfoList = mangerService.selcetAllBaseAttrValue(spuId);
        return baseAttrInfoList;
    }
    //获取销售属性SpuSaleAttr以及销售属性值SpuSaleAttrValue
    @ResponseBody
    @RequestMapping("/getSaleAttrListBySpuId")
    public List<SpuSaleAttr> selectaa(Long spuId){
        List<SpuSaleAttr> spuSaleAttrList = mangerService.selcetAllSpuSaleAttr(spuId);
        return spuSaleAttrList;
    }

    @ResponseBody
    @RequestMapping("/saveSku")
    public String addSkuInfo(SkuInfo skuInfo){
        mangerService.addSkuInfos(skuInfo);
        return "添加success";
    }

    @ResponseBody
    @RequestMapping("/todeleteSkuInfo")
    public void removeSkuInfo(Long skuId){
        mangerService.deleteSkuInfos(skuId);
    }
}
