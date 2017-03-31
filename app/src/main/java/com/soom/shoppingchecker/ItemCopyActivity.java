package com.soom.shoppingchecker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.soom.shoppingchecker.adapter.CartSpinnerAdapter;
import com.soom.shoppingchecker.model.Cart;
import com.soom.shoppingchecker.model.CartItem;
import com.soom.shoppingchecker.service.CartItemService;
import com.soom.shoppingchecker.service.CartService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

public class ItemCopyActivity extends AppCompatActivity {
    private Realm realm = Realm.getDefaultInstance();
    private List<Cart> cartList;

    private long currentCartId;
    private long selectedCartItemId;
    private String itemText;
    private long selectedCartId;

    private TextView txtCartName;
    private Spinner spinnerCart;
    private EditText editTextCartName;
    private Button buttonAddCart;
    private Button buttonCopyCart;
    private Button buttonCloseCopyCart;

    private CartService cartService;
    private CartSpinnerAdapter adapter;
    private Context context;

    public ItemCopyActivity() {
        this.context = this;
        this.cartService = new CartService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_copy);

        initViews();
    }

    private void initViews() {
        Intent intent = getIntent();
        currentCartId = intent.getLongExtra("currentCartId", 0);
        selectedCartItemId = intent.getLongExtra("cartItemId", 0L);
        itemText = intent.getStringExtra("itemText");

        txtCartName = (TextView) findViewById(R.id.txtCartName);
        txtCartName.setText(itemText);
        spinnerCart = (Spinner) findViewById(R.id.spinnerCart);
        spinnerCart.setOnItemSelectedListener(new OnCartSelectedListener());

        /**
         * DB에서 모든 카트를 조회해온다.
         */
        cartList = realm.where(Cart.class).findAll();


        adapter = new CartSpinnerAdapter(context, cartList);
        spinnerCart.setAdapter(adapter);

        editTextCartName = (EditText) findViewById(R.id.editTextCartName);
        buttonAddCart = (Button) findViewById(R.id.buttonAddCart);
        buttonAddCart.setOnClickListener(new OnCartAddClickListener());
        buttonCopyCart = (Button) findViewById(R.id.buttonCopyCart);
        buttonCopyCart.setOnClickListener(new OnItemCopyButtonClickListener(new CartItemService()));
        buttonCloseCopyCart = (Button) findViewById(R.id.buttonCloseCopyCart);
        buttonCloseCopyCart.setOnClickListener(new OnItemCopyCloseButtonClickListener());
    }


    private class OnCartAddClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String cartName = editTextCartName.getEditableText().toString();
            Log.d("ItemCopyActivity", "cartName: " + cartName);

            if(!cartName.isEmpty()){
                Log.d("ItemCopyActivity", "added Cart!!!!");

                long maxCartId = cartService.findMaxCartId();
                long cartId = maxCartId + 1;
                Cart cart = new Cart(cartId, cartName, new Date(), new Date());
                cartService.saveCart(cart);

                /**
                 * - cartList에 생성된 카트명을 추가
                 * - adapter notifyChaneged() 호출해서 갱신.
                 * - 스피너에서 해당 카트 선택.
                 * - onActivityResult에서 카트 메뉴 리프레시 해야됨. TODO
                 */
                List<Cart> carts = cartService.findAllCart();
                realm.beginTransaction();
                adapter.setCartList(carts);
                realm.commitTransaction();
                adapter.notifyDataSetChanged();
                for(int i = 0; i < spinnerCart.getCount(); i++){
                    Cart eachCart = (Cart) spinnerCart.getItemAtPosition(i);

                    if(eachCart.getCartId() == cartId) {
                        spinnerCart.setSelection(i);
                        editTextCartName.setText(null);
                    }
                }
            }
        }
    }

    private class OnCartSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d("ItemCopyActivity", "selected Cart from cart spinner.");

            Cart selectedCart = (Cart) view.getTag();
            selectedCartId = selectedCart.getCartId();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    /**
     * 해당 카트로 아이템 복사.
     */
    private class OnItemCopyButtonClickListener implements View.OnClickListener {
        private CartItemService cartItemService;

        public OnItemCopyButtonClickListener(CartItemService cartItemService) {
            this.cartItemService = cartItemService;
        }

        @Override
        public void onClick(View v) {
            Cart sourceCart = cartService.findOneCartByCartId(currentCartId);
            for(CartItem sourceCartItem : sourceCart.getCartItems()){
                if(selectedCartItemId == sourceCartItem.getCartItemId()){
                    long cartItemId = cartItemService.getNewCartItemId();
                    cartItemService.insertItem(selectedCartId, cartItemId, sourceCartItem.getItemText());
                    Log.d("ItemCopyActivity", "Copied a cartItem from " + sourceCart.getCartName());
                    break;
                }
            }

            setResultData();
            finish();
        }

        private void setResultData(){
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
        }
    }

    /**
     * 닫기 버튼 클릭 리스너
     */
    private class OnItemCopyCloseButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }



}
