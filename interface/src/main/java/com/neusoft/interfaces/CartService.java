package com.neusoft.interfaces;

import com.neusoft.bean.po.CartInfo;

import java.util.List;

public interface CartService {

    CartInfo ifCartExists(CartInfo cartInfo);

    void updateCart(CartInfo cartInfoDb);

    void insertCart(CartInfo cartInfo);

    void syncChe(Long userId);

    List<CartInfo> getCartCache(Long userId);
}
