package com.neusoft.orderweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.neusoft.bean.po.OrderInfo;
import com.neusoft.bean.po.UserInfo;
import com.neusoft.interfaces.OrderService;
import com.neusoft.interfaces.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

//@RestController
@Controller
public class OrderController {

    @Reference
    OrderService orderService;
    @Reference
    UserService userService;

    @RequestMapping("/addOrder.do")
    public String addOrder(ModelMap map){
        List<OrderInfo> orderInfoList = orderService.selectOrder(new OrderInfo());
        System.out.println(orderInfoList.size());
        for (OrderInfo orderInfo1:orderInfoList){
            System.out.println(orderInfo1);
        }
        map.put("orderInfoList",orderInfoList);


        return "index";
    }

}
