package com.neusoft.mangerservice.dao;


import com.neusoft.bean.po.BaseAttrInfo;
import com.neusoft.bean.po.BaseAttrValue;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseAttrValueMapper extends Mapper<BaseAttrValue> {
    List<BaseAttrInfo> selectAttrListByValueIds(@Param("ids") String join);
}