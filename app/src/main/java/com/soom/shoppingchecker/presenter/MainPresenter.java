package com.soom.shoppingchecker.presenter;

import com.soom.shoppingchecker.model.Cart;
import com.soom.shoppingchecker.service.CartItemService;
import com.soom.shoppingchecker.service.CartService;

import java.util.Date;

/**
 * Created by kjs on 2017-04-11.
 */

public class MainPresenter implements MainContract.Presenter {
    private MainContract.View view;
    private CartService cartService;
    private CartItemService cartItemService;
    private Cart cart;

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
    public long addCart(String cartName) {
        /**
         * - max cartId를 가져온다.
         * - new cartId = 조회한 max cartId + 1
         * - 저장.
         * - 저장되었는지 확인 테스트
         * - 팝업 액티비티 닫기
         * - CartItemListAdapter를 통해 저장된 Cart에 해당하는 cart Item을 listviw에 가져와서 표시. TODO
         *      - 액티비티를 닫을 때, cartId를 응답으로 전달
         *      - 응답으로 전달 받은 cartId에 해당하는 Cart Item을 조회하여 adapter를 갱신. 기존 arrayList를 clear하고 갱신한다.
         */
        long cartId = 0L;
        if(!cartName.isEmpty()){
            long maxCartId = cartService.findMaxCartId();
            cartId = maxCartId + 1;
            cart = new Cart(cartId, cartName, new Date(), new Date());
            cartService.saveCart(cart);
        }

        return cartId;
    }

    @Override
    public void addCartItem(long cartId, long cartItemId, String itemText) {
        cartItemService.insertItem(cartId, cartItemId, itemText);
        loadCartItems(cartId);
    }

    @Override
    public void findAllCart() {

    }

    @Override
    public void loadCartItems(long cartId) {
        Cart cart = cartService.findOneCartByCartId(cartId);
        view.refreshCartItems(cart);
    }
}
