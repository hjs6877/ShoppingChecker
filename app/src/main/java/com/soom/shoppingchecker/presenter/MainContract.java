package com.soom.shoppingchecker.presenter;

/**
 * Created by kjs on 2017-04-06.
 */

public interface MainContract {
    interface View {
        void refreshCartMenues();
        void refreshCartItems();
    }

    interface Presenter {
        void copyCartItem();
        void addCart();
        void addCartItem();
        void findAllCart();
        void findCartByCartId();
    }
}
