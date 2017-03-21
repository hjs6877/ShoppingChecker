package com.soom.shoppingchecker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.soom.shoppingchecker.database.DBController;
import com.soom.shoppingchecker.database.SQLData;
import com.soom.shoppingchecker.model.Cart;
import com.soom.shoppingchecker.model.CartItem;
import com.soom.shoppingchecker.service.CartItemService;
import com.soom.shoppingchecker.service.CartService;

import java.util.Date;

import io.realm.Realm;

public class CartCreateActivity extends AppCompatActivity {
    private Realm realm = Realm.getDefaultInstance();
    private EditText editCreateModifyCartText;
    private Button buttonCreateModifyCart;
    private Button buttonCloseCreateCart;
    private Cart cart;
    private CartService cartService;
    private int mode;
    private long cartId;
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

        editCreateModifyCartText = (EditText) findViewById(R.id.editCreateModifyCartText);
        buttonCreateModifyCart = (Button) findViewById(R.id.buttonCreateModifyCart);
        buttonCreateModifyCart.setOnClickListener(new CartTextCreateButtonClickListener());

        // 공백일 경우 disable 처리
        if(editCreateModifyCartText.getEditableText().toString().isEmpty())
            buttonCreateModifyCart.setEnabled(false);

        editCreateModifyCartText.addTextChangedListener(new CartTextChangedListener());

        buttonCloseCreateCart = (Button) findViewById(R.id.buttonCloseCreateCart);
        buttonCloseCreateCart.setOnClickListener(new CartCreateCloseButtonClickListener());

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 0);

        cartId = intent.getLongExtra("cartId", 0L);
        String cartName = intent.getStringExtra("cartName");

        if(mode == MainActivity.CART_MODE_MODIFY){
            editCreateModifyCartText.setText(cartName);
            editCreateModifyCartText.setSelection(cartName.length());
            buttonCreateModifyCart.setText(R.string.action_modify);
        }

    }

    private class CartTextChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            Log.d("CartCreateActivity", "beforeTextChanged: " + s + "-" + start + "-" + count + "-" + after);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            Log.d("CartCreateActivity", "onTextChanged: " + s + "-" + start + "-" + before + "-" + count);
        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean shouldEnableButtonCreateModifyCart;
            shouldEnableButtonCreateModifyCart = s.toString().isEmpty() ? false : true;

            buttonCreateModifyCart.setEnabled(shouldEnableButtonCreateModifyCart);
        }
    }
    /**
     * 쇼핑 목록(카트) 생성 및 수정 버튼 클릭 리스너
     */
    private class CartTextCreateButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(mode == MainActivity.CART_MODE_MODIFY){
                modifyCart();
            }else{
                createCart();
            }

            setResultData();
            finish();
        }

        private void createCart(){
            String cartName = editCreateModifyCartText.getEditableText().toString();
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
            if(!cartName.isEmpty()){
                long maxCartId = cartService.findMaxCartId();
                long cartId = maxCartId + 1;
                cart = new Cart(cartId, cartName, new Date(), new Date());
                cartService.saveCart(cart);
            }

        }

        private void modifyCart(){
            final String cartName = editCreateModifyCartText.getEditableText().toString();
            if(!cartName.isEmpty()){
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Cart cart = cartService.findOneCartByCartId(cartId);
                        cart.setCartName(cartName);
                    }
                });
            }

        }

        private void setResultData(){
            Intent resultIntent = new Intent();
            resultIntent.putExtra("cartId", cart.getCartId());
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
