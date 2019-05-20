package com.neusoft.interfaces;

import com.neusoft.bean.po.OrderInfo;

import java.util.List;

public interface OrderService {

    List<OrderInfo> selectOrder(OrderInfo orderInfo);

}
