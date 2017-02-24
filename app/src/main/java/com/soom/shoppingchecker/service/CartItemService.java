package com.soom.shoppingchecker.service;

import android.database.Cursor;
import android.util.Log;

import com.soom.shoppingchecker.database.DBController;
import com.soom.shoppingchecker.model.Cart;
import com.soom.shoppingchecker.model.CartItem;

import java.util.ArrayList;
import java.util.List;

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
