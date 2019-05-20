package com.neusoft.orderservice.serviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.neusoft.bean.po.OrderInfo;
import com.neusoft.interfaces.OrderService;
import com.neusoft.orderservice.dao.OrderInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderInfoMapper orderInfoMapper;
    @Override
    public List<OrderInfo> selectOrder(OrderInfo orderInfo) {
        List<OrderInfo> orderInfoList = orderInfoMapper.selectAll();
        //JSONObject jsonObject = new JSONObject();
        return orderInfoList;
    }
}
