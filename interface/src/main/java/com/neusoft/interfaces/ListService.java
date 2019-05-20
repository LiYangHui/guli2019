package com.neusoft.interfaces;

import com.neusoft.bean.po.SkuLsInfo;
import com.neusoft.bean.po.SkuLsParam;

import java.util.List;

public interface ListService {
    List<SkuLsInfo> search(SkuLsParam skuLsParam);
}
