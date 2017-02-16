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
     * 아이템 수정 버튼 클릭 리스너
     */
    private class CartTextCreateButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            createCart();
            finish();
        }

        private void createCart(){
            String cartName = editCreateCartText.getEditableText().toString();
            /**
             * - max cartId를 가져온다.
             * - new cartId = 조회한 max cartId + 1
             * - 저장.
             */
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
