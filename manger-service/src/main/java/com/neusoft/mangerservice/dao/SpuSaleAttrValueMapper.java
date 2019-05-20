package com.neusoft.mangerservice.dao;




import com.neusoft.bean.po.SkuInfo;
import com.neusoft.bean.po.SpuSaleAttr;
import com.neusoft.bean.po.SpuSaleAttrValue;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SpuSaleAttrValueMapper extends Mapper<SpuSaleAttrValue> {
    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(Map<String,Long> stringLongHashMap);
    List<SkuInfo> selectSkuSaleAttrValueListBySpu(@Param("spuId") Long spuId);
}