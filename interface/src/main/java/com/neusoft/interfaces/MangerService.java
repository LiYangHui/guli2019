package com.neusoft.interfaces;

import com.neusoft.bean.po.*;

import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MangerService{

    List<BaseCatalog1> selectAllBase1();

    List<BaseCatalog2> selectAllBase2(int base1Id);

    List<BaseCatalog3> selectAllBase3(int base2Id);
    //商品属性表
    List<BaseAttrInfo> selectAll(int base3Id);

    //添加
    int addBaseAttrValue(BaseAttrInfo baseAttrInfo);

    //删除
    int deleteBaseInfo(int baseInfoId);

    //查看Value
    List<BaseAttrValue> selectAllValue(int infoId);

    //查Spu
    List<SpuInfo> selectAllSpuInfo(int base3Id);
    //查BaseSale
    List<BaseSaleAttr> selectAllBaseSaleAttr();

    //添加Spu
    int addSpuInfos(SpuInfo spuInfo);

    //删除Spu
    int deleteSpuInfos(Long base3Id);

    //修改Spu
    int updateSpuInfos(SpuInfo spuInfo);

    //查询某个Spu
    SpuInfo selectSpus(Long spuId);

    //查询Sku
    List<SkuInfo> selectAllSkuInfo(Long spuId);

    //查询SpuImage
    List<SpuImage> selcetAllSpuImage(Long spuId);

    //查询平台属性BaseAttrValue
    List<BaseAttrInfo> selcetAllBaseAttrValue(Long base3Id);

    //查询销售属性SpuSaleAttr
    List<SpuSaleAttr> selcetAllSpuSaleAttr(Long spuId);

    //添加Sku
    int addSkuInfos(SkuInfo skuInfo);

    //删除Sku
    int deleteSkuInfos(Long skuId);

    //商品详情:通过skuId查询SkuInfo
    SkuInfo getSkuById(Long skuId);

    //商品详情销售属性值isCheck选中
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Map<String,Long> stringLongMap);

    //根据销售属性切换sku
    List<SkuInfo> getSkuSaleAttrValueListBySpu(Long spuId);

    List<SkuInfo> getSkuListByCatalog3Id(Long catalog3Id);

    List<BaseAttrInfo> getAttrListByValueIds(Set<String> valueIds);
}
