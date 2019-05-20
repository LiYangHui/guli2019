package com.neusoft.cartservice.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.neusoft.bean.po.CartInfo;
import com.neusoft.cartservice.dao.CartInfoMapper;
import com.neusoft.interfaces.CartService;
import com.neusoft.serviceutils.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    CartInfoMapper cartInfoDao;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public CartInfo ifCartExists(CartInfo cartInfo) {
        CartInfo cartInfo1 = new CartInfo();
        cartInfo1.setUserId(cartInfo.getUserId());
        cartInfo1.setSkuId(cartInfo.getSkuId());
        CartInfo select = cartInfoDao.selectOne(cartInfo1);
        return select;
    }

    @Override
    public void updateCart(CartInfo cartInfoDb) {
        cartInfoDao.updateByPrimaryKeySelective(cartInfoDb);
    }

    @Override
    public void insertCart(CartInfo cartInfo) {
        cartInfoDao.insertSelective(cartInfo);
    }

    @Override
    public void syncChe(Long userId) {
        Jedis jedis = redisUtil.getJedis();
        CartInfo cartinfo = new CartInfo();
        cartinfo.setUserId(userId);
        List<CartInfo> select = cartInfoDao.select(cartinfo);
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        for (CartInfo info:select){
            stringStringHashMap.put(info.getId().toString(), JSON.toJSONString(info));
        }
        jedis.hmset("carts:"+userId+":info",stringStringHashMap);

        jedis.close();
    }

    @Override
    public List<CartInfo> getCartCache(Long userId) {
        List<CartInfo> cartInfoList=new ArrayList<>();
        Jedis jedis = redisUtil.getJedis();
        List<String> hvals = jedis.hvals("carts:" + userId + ":info");
        if (hvals!=null&&hvals.size()>0){
            for (String hval:hvals){
                CartInfo info = JSON.parseObject(hval, CartInfo.class);
                cartInfoList.add(info);
            }
        }
        return cartInfoList;
    }
}
