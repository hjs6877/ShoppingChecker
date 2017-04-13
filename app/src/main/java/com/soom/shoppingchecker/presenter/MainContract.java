package com.soom.shoppingchecker.presenter;

import com.soom.shoppingchecker.model.Cart;

/**
 * Created by kjs on 2017-04-06.
 */

public interface MainContract {
    interface View {
        void refreshCartMenues();
        void refreshCartItems(Cart cart);
    }

    interface Presenter {
        void attachView(View view);
        void copyCartItem();
        void addCart();
        void addCartItem(long cartId, long cartItemId, String itemText);
        void findAllCart();
        void loadCartItems(long cartId);
    }
}
