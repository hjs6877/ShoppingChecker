package com.soom.shoppingchecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import io.realm.Realm;

import static android.widget.Toast.LENGTH_SHORT;


/**
 * 아이템 수정 팝업 액티비티. 메인 액티비티에서 아이템을 long click하면 오픈 되는 액티비티 임.
 */
public class ItemModifyActivity extends AppCompatActivity {
    private EditText editModifyItemText;
    private Button buttonModifyItem;
    private Button buttonCloseModifyItem;

    private Realm realm = Realm.getDefaultInstance();

    private int position;
    private long cartItemId;
    private String itemText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_modify);

        initViews();
    }

    /**
     * 화면에 표시되는 뷰에 대한 초기화 작업을 진행한다.
     */
    private void initViews(){
        Intent intent = getIntent();
        cartItemId = intent.getLongExtra("cartItemId", 0);
        itemText = intent.getStringExtra("itemText");
        position = intent.getIntExtra("position", 0);
        editModifyItemText = (EditText) findViewById(R.id.editModifyItemText);
        editModifyItemText.setText(itemText);
        editModifyItemText.setSelection(editModifyItemText.length());
        editModifyItemText.addTextChangedListener(new CartItemTextChangedListener());
        buttonModifyItem = (Button) findViewById(R.id.buttonModifyItem);
        buttonModifyItem.setOnClickListener(new ItemTextModifyButtonClickListener());

        buttonCloseModifyItem = (Button) findViewById(R.id.buttonCloseModifyItem);
        buttonCloseModifyItem.setOnClickListener(new ItemModifyCloseButtonClickListener());
    }

    private class CartItemTextChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            Log.d("ItemModifyActivity", "beforeTextChanged: " + s + "-" + start + "-" + count + "-" + after);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            Log.d("ItemModifyActivity", "onTextChanged: " + s + "-" + start + "-" + before + "-" + count);
        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean shouldEnableButtonCreateModifyCart;
            shouldEnableButtonCreateModifyCart = s.toString().isEmpty() ? false : true;

            buttonModifyItem.setEnabled(shouldEnableButtonCreateModifyCart);
        }
    }

    /**
     * 아이템 수정 버튼 클릭 리스너
     */
    private class ItemTextModifyButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            updateCartItem();
            setResultData();
            finish();
        }

        private void updateCartItem(){
            String modifiedItemText = editModifyItemText.getEditableText().toString();

            if(!modifiedItemText.isEmpty()){
                Cart cart = realm.where(Cart.class).equalTo("cartItems.cartItemId", cartItemId).findFirst();
                realm.beginTransaction();
                cart.getCartItems().get(0).setItemText(modifiedItemText);
                realm.commitTransaction();
            }

        }

        /**
         * 수정된 아이템 텍스트를 리스트뷰에 반영하기 위한 데이터를 setting한다.
         */
        private void setResultData(){
            String modifiedItemText = editModifyItemText.getEditableText().toString();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("modifiedItemText", modifiedItemText);
            resultIntent.putExtra("position", position);
            setResult(RESULT_OK, resultIntent);
        }
    }

    /**
     * 닫기 버튼 클릭 리스너
     */
    private class ItemModifyCloseButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }
}
