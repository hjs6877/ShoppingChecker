package com.soom.shoppingchecker.service;

import android.database.Cursor;
import android.util.Log;

import com.soom.shoppingchecker.database.DBController;
import com.soom.shoppingchecker.model.Cart;
import com.soom.shoppingchecker.model.CartItem;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by kjs on 2017-01-13.
 */

public class CartItemService {
    private final String TAG = "CartItemService";
    private Realm realm = Realm.getDefaultInstance();

}
