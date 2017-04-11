package com.soom.shoppingchecker.presenter;

import com.soom.shoppingchecker.model.Cart;
import com.soom.shoppingchecker.service.CartItemService;
import com.soom.shoppingchecker.service.CartService;

/**
 * Created by kjs on 2017-04-11.
 */

public class MainPresenter implements MainContract.Presenter {
    private MainContract.View view;
    private CartService cartService;
    private CartItemService cartItemService;

    public MainPresenter(CartService cartService, CartItemService cartItemService) {
        this.cartService = cartService;
        this.cartItemService = cartItemService;
    }

    @Override
    public void attachView(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void copyCartItem() {

    }

    @Override
    public void addCart() {

    }

    @Override
    public void addCartItem() {

    }

    @Override
    public void findAllCart() {

    }

    @Override
    public void loadCartItems(long cartId) {
        Cart cart = cartService.findOneCartByCartId(1);
        view.refreshCartItems(cart);
    }
}
