package com.soom.shoppingchecker.service;

import com.soom.shoppingchecker.model.Cart;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by kjs on 2017-02-15.
 */

public class CartService {
    private Realm realm = Realm.getDefaultInstance();

    public Cart saveCart(Cart cart){
        realm.beginTransaction();
        Cart realmCart = realm.copyToRealmOrUpdate(cart);
        realm.commitTransaction();

        return realmCart;
    }

    public List<Cart> findAllCart(){
        return realm.where(Cart.class).findAll();
    }

    public Cart findOneCartByCartId(long cartId){
        return realm.where(Cart.class).equalTo("cartId", cartId).findFirst();
    }

    public void deleteCartByCartId(final long cartId){
       realm.executeTransaction(new Realm.Transaction() {
           @Override
           public void execute(Realm realm) {
               RealmResults<Cart> results = realm.where(Cart.class).equalTo("cartId", cartId).findAll();
               results.deleteAllFromRealm();
           }
       });
    }

    public void deleteAllCart(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Cart> results = realm.where(Cart.class).findAll();
                results.deleteAllFromRealm();
            }
        });
    }

    public long findMaxCartId(){
        long maxId;

        if(realm.where(Cart.class).max("cartId") == null)
            maxId = 1;
        else
            maxId = (long) realm.where(Cart.class).max("cartId");

        return maxId;
    }

}
