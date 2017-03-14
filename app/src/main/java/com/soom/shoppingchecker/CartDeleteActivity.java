package com.soom.shoppingchecker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.soom.shoppingchecker.service.CartService;

public class CartDeleteActivity extends AppCompatActivity {
    private CartService cartService;
    private Button buttonDeleteConfirmCart;
    private Button buttonCloseDeleteCart;
    private long cartId;

    public CartDeleteActivity(){
        cartService = new CartService();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_delete);

        initViews();
    }

    /**
     * 화면에 표시되는 뷰에 대한 초기화 작업을 진행한다.
     */
    private void initViews() {
        buttonDeleteConfirmCart = (Button) findViewById(R.id.buttonDeleteConfirmCart);
        buttonDeleteConfirmCart.setOnClickListener(new CartDeleteButtonClickListener());
        buttonCloseDeleteCart = (Button) findViewById(R.id.buttonCloseDeleteCart);
        buttonCloseDeleteCart.setOnClickListener(new CartDeleteCloseButtonClickListener());

        Intent intent = getIntent();
        cartId = intent.getLongExtra("cartId", 0L);

    }

    private class CartDeleteButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            deleteCart();
            setResultData();
            finish();
        }

        private void deleteCart(){
            cartService.deleteCartByCartId(cartId);
        }
    }

    private void setResultData(){
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
    }

    /**
     * 닫기 버튼 클릭 리스너
     */
    private class CartDeleteCloseButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }
}
