package com.nuesoft.cartweb.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.neusoft.bean.po.CartInfo;
import com.neusoft.bean.po.SkuInfo;
import com.neusoft.interfaces.CartService;
import com.neusoft.interfaces.MangerService;
import com.neusoft.webutil.CookieUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    @Reference
    MangerService mangerService;

    @Reference
    CartService cartService;

    @RequestMapping("/cartList")
    private String cartList(HttpServletRequest request, ModelMap map) {
        List<CartInfo> cartInfoList = new ArrayList<>();
        Long userId=1L;
        if(userId==null){
            //取cookie中的数据
            String cartListCookie = CookieUtil.getCookieValue(request,"cartListCookie",true);
            if (StringUtils.isNotEmpty(cartListCookie)) {
                cartInfoList = JSON.parseArray(cartListCookie, CartInfo.class);
            }
        }else {
            //取缓存中的数据
            cartInfoList = cartService.getCartCache(userId);
        }
        map.put("cartList",cartInfoList);
        return "cartList";
    }

    @RequestMapping("/cartSuccess")
    public String cartSuccess(){

        return "success";
    }
    @RequestMapping("/addToCart")
    public String addToCart(HttpServletRequest request, HttpServletResponse response,CartInfo cartInfo){
        Long id=cartInfo.getSkuId();
        SkuInfo skuInfo=mangerService.getSkuById(id);
        cartInfo.setCartPrice(skuInfo.getPrice().multiply(new BigDecimal(cartInfo.getSkuNum())));
        cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
        cartInfo.setSkuName(skuInfo.getSkuName());

        Long userId=1L;
        List<CartInfo> cartInfoList=new ArrayList<>();
        if(userId==null){
            //用户未登录,添加cookie
            String cartListCookieStr= CookieUtil.getCookieValue(request,"cartListCookie",true);
            if (StringUtils.isBlank(cartListCookieStr)){
                cartInfoList.add(cartInfo);
            }else {
                cartInfoList=new ArrayList<>();
                cartInfoList = JSON.parseArray(cartListCookieStr,CartInfo.class);
                //判断是否重复的sku
                boolean b = ifNewCart(cartInfoList,cartInfo);
                if (b){
                    cartInfoList.add(cartInfo);//添加
                }else {
                    for (CartInfo info:cartInfoList){//更新
                        Long skuId = info.getSkuId();
                        if (skuId==cartInfo.getSkuId()){//判断id是否相等
                            info.setSkuNum(info.getSkuNum()+cartInfo.getSkuNum());//总数量
                            info.setCartPrice(info.getCartPrice().multiply(new BigDecimal(info.getSkuNum())));//总价格
                        }
                    }
                }
            }
            //操作完成后覆盖cookie
            CookieUtil.setCookie(request,response,"cartListCookie", JSON.toJSONString(cartInfoList),60*60*24*7,true);
        }else {
            //用户已登录
            Long skuId=cartInfo.getSkuId();
            cartInfo.setUserId(userId);
            CartInfo cartInfoDb = cartService.ifCartExists(cartInfo);
            if (cartInfoDb!=null){
                //更新数据库
                cartInfoDb.setSkuNum(cartInfoDb.getSkuNum()+cartInfo.getSkuNum());
                cartInfoDb.setCartPrice(cartInfoDb.getCartPrice().multiply(new BigDecimal(cartInfoDb.getSkuNum())));
                cartService.updateCart(cartInfoDb);
            }else {
                //新增数据库
                cartService.insertCart(cartInfo);
            }
            //同步缓存
                cartService.syncChe(userId);
        }

         return "redirect:/cartSuccess";
    }

    private boolean ifNewCart(List<CartInfo> cartInfoList, CartInfo cartInfo) {
        boolean b = true;
        for (CartInfo info:cartInfoList){
            Long skuId = info.getSkuId();
            if (skuId==cartInfo.getSkuId()){
                b = false;
            }
        }
        return false;
    }
}
