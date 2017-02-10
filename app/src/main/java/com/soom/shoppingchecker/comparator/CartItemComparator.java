package com.soom.shoppingchecker.comparator;

import com.soom.shoppingchecker.model.CartItem;

import java.util.Comparator;

/**
 * Created by kjs on 2016-12-23.
 */

public class CartItemComparator implements Comparator<CartItem> {
    @Override
    public int compare(CartItem o1, CartItem o2) {

        return o2.getCreateDate().compareTo(o1.getCreateDate());
    }
}
