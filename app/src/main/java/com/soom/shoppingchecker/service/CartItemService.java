package com.soom.shoppingchecker.service;

import android.database.Cursor;
import android.util.Log;

import com.soom.shoppingchecker.database.DBController;
import com.soom.shoppingchecker.model.Cart;
import com.soom.shoppingchecker.model.CartItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by kjs on 2017-01-13.
 */

public class CartItemService {
    private final String TAG = "CartItemService";
    private Realm realm = Realm.getDefaultInstance();
    private CartService cartService;

    public CartItemService(){
        this.cartService = new CartService();
    }
    public long findMaxCartItemId(){

        long maxCartItemId = 0;
        List<Cart> carts = cartService.findAllCart();

        for(Cart cart : carts){
            long cartItemId = getMaxCartItemIdInCart(cart);
            if(cartItemId > maxCartItemId)
                maxCartItemId = cartItemId;
        }

        return maxCartItemId;
    }

    public long getNewCartItemId(){
        long maxCartItemId = findMaxCartItemId();

        return maxCartItemId + 1;
    }

    public Cart insertItem(long cartId, long cartItemId, String itemText) {
        realm.beginTransaction();
        Cart cart = cartService.findOneCartByCartId(cartId);


        CartItem cartItem = new CartItem(cartItemId, itemText, false, false, new Date(), new Date());
        // 1:N 관계에서 자식 객체는 Realm Object로 객체화 하지 않아도 됨.
        cart.getCartItems().add(cartItem);
        realm.commitTransaction();
        return cart;
    }

    public void deleteCartItem(long cartId, CartItem removedCartItem){
        Cart cart = cartService.findOneCartByCartId(cartId);
        List<CartItem> cartItems = cart.getCartItems();
        Iterator<CartItem> iter = cartItems.iterator();

        // 1:N 관계에서 N쪽 자식을 삭제하기 위해서는 Iterator를 사용해야 함.
        while(iter.hasNext()){
            CartItem cartItem = iter.next();
            if(cartItem.equals(removedCartItem)){
                iter.remove();
            }
        }
    }

    public void deleteCartItems(long cartId, Map<Long, CartItem> checkedItemMap){
        Cart cart = cartService.findOneCartByCartId(cartId);
        List<CartItem> cartItems = cart.getCartItems();
        Iterator<CartItem> iter = cartItems.iterator();

        // 1:N 관계에서 N쪽 자식을 삭제하기 위해서는 Iterator를 사용해야 함.
        while(iter.hasNext()){
            CartItem cartItem = iter.next();
            if(cartItem.equals(checkedItemMap.get(cartItem.getCartItemId()))){
                iter.remove();
            }
        }
    }

    private long getMaxCartItemIdInCart(Cart cart) {
        long maxCartItemId = 0;
        List<CartItem> cartItems = cart.getCartItems();
        for(CartItem cartItem : cartItems){
            long cartItemId = cartItem.getCartItemId();
            if(cartItemId > maxCartItemId)
                maxCartItemId = cartItemId;
        }
        return maxCartItemId;
    }
}
