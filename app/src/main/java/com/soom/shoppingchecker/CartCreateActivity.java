package com.soom.shoppingchecker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.soom.shoppingchecker.database.DBController;
import com.soom.shoppingchecker.database.SQLData;
import com.soom.shoppingchecker.model.Cart;
import com.soom.shoppingchecker.model.CartItem;
import com.soom.shoppingchecker.service.CartItemService;
import com.soom.shoppingchecker.service.CartService;

import java.util.Date;

public class CartCreateActivity extends AppCompatActivity {
    private EditText editCreateCartText;
    private Button buttonCreateCart;
    private Button buttonCloseCreateCart;
    private Cart cart;
    private CartService cartService;

    public CartCreateActivity() {
        cartService = new CartService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_create);

        initViews();
    }

    /**
     * 화면에 표시되는 뷰에 대한 초기화 작업을 진행한다.
     */
    private void initViews(){
        editCreateCartText = (EditText) findViewById(R.id.editCreateCartText);
        buttonCreateCart = (Button) findViewById(R.id.buttonCreateCart);
        buttonCreateCart.setOnClickListener(new CartTextCreateButtonClickListener());

        buttonCloseCreateCart = (Button) findViewById(R.id.buttonCloseCreateCart);
        buttonCloseCreateCart.setOnClickListener(new CartCreateCloseButtonClickListener());
    }

    /**
     * 쇼핑 목록(카트) 생성 버튼 클릭 리스너
     */
    private class CartTextCreateButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            createCart();
            setResultData();
            finish();
        }

        private void createCart(){
            String cartName = editCreateCartText.getEditableText().toString();
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
            long maxCartId = cartService.findMaxCartId();
            long cartId = maxCartId + 1;
            cart = new Cart(cartId, cartName, new Date(), new Date());
            cartService.saveCart(cart);
        }

        private void setResultData(){
            Intent resultIntent = new Intent();
            resultIntent.putExtra("cartId", cart.getCartId());
            resultIntent.putExtra("cartName", cart.getCartName());
            setResult(RESULT_OK, resultIntent);
        }
    }

    /**
     * 닫기 버튼 클릭 리스너
     */
    private class CartCreateCloseButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }
}
