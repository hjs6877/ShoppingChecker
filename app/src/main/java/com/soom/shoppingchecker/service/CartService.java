package com.soom.shoppingchecker.service;

import com.soom.shoppingchecker.model.Cart;

import java.util.List;

import io.realm.Realm;

/**
 * Created by kjs on 2017-02-15.
 */

public class CartService {


    public Cart saveCart(Cart cart){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Cart realmCart = realm.copyToRealmOrUpdate(cart);
        realm.commitTransaction();

        return realmCart;
    }

    public List<Cart> findAllCart(){
        return null;
    }

    public void deleteCart(Cart cart){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        realm.commitTransaction();
    }

    public void deleteAllCart(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }
}
