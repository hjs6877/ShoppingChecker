package com.soom.shoppingchecker.service;

import android.database.Cursor;
import android.util.Log;

import com.soom.shoppingchecker.database.DBController;
import com.soom.shoppingchecker.model.CartItem;

import java.util.ArrayList;

/**
 * Created by kjs on 2017-01-13.
 */

public class CartItemService {
    private final String TAG = "CartItemService";
    private DBController dbController;

    public CartItemService(DBController dbController){
        this.dbController = dbController;
    }

    public void insertCartItem(String sql, CartItem cartItem){
        Log.d(TAG, "## insert to cart item.");
    }

    public void updateCartItem(String sql, CartItem cartItem){
        Log.d(TAG, "## update cart item.");
    }

    public void updateIsPurchased(String sql, int regId, int isPurchased){
        Log.d(TAG, "## update isPurchased.");
        Object[] sqlData = {isPurchased, regId};

    }

    public void updateIsChecked(String sql, int regId, int isChecked){
        Log.d(TAG, "## update isChecked.");
        Object[] sqlData = {isChecked, regId};

    }



    public void deleteCartItem(String sql, int regId){
        Log.d(TAG, "## delete from cart_item table.");
    }

    public ArrayList<CartItem> findAllCartItemByCart(String sql){

        Log.d(TAG, "## cart_item table select.");
        ArrayList<CartItem> cartItemList = new ArrayList<>();

        return cartItemList;
    }

    public int findMaxCartItemRegIdByCart(String sql){

        Log.d(TAG, "## select max reg_id.");
        int maxRegId = 0;

        return maxRegId;
    }

}
