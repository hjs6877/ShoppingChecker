package com.soom.shoppingchecker.presenter;

/**
 * Created by kjs on 2017-04-06.
 */

public interface MainContract {
    interface View {
        void reloadCart();
        void addCartItems();
    }

    interface Presenter {
        void copyCartItem();
        void loadCartItems();
        void addCart();
        void addCartItem();
        void findAllCart();
        void findCartByCartId();
    }
}
