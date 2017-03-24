package com.soom.shoppingchecker.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.soom.shoppingchecker.ItemCopyActivity;
import com.soom.shoppingchecker.ItemModifyActivity;
import com.soom.shoppingchecker.R;
import com.soom.shoppingchecker.database.DBController;
import com.soom.shoppingchecker.database.SQLData;
import com.soom.shoppingchecker.model.Cart;
import com.soom.shoppingchecker.model.CartItem;
import com.soom.shoppingchecker.service.CartItemService;
import com.soom.shoppingchecker.utils.DataTypeUtils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by kjs on 2016-12-08.
 * 아이템 데이터를 리스트뷰에 제공해주는 어댑터 클래스
 */

public class CartItemListAdapter extends BaseAdapter {
    public static final int REQUEST_CODE_MODIFY_ITEM = 2001;
    public static final int REQUEST_CODE_COPY_ITEM = 2002;
    private Realm realm = Realm.getDefaultInstance();

    /**
     * 리스트 뷰에 아이템으로 표시되는 위젯 객체들을 보관하는 내부 클래스
     */
    private class ViewHolder {
        CheckBox itemCheckBox;
        TextView itemTextView;
        Button itemPurchasedButton;
        Button itemModifyButton;
        Button itemCopyButton;
    }
    private List<CartItem> cartItemList;
    private Map<Long, CartItem> checkedItemMap;
    private LayoutInflater inflater;
    private Context context;

    public CartItemListAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.cartItemList = cartItemList;
        checkedItemMap = new HashMap<>();
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    public List<CartItem> getCartItemList(){
        return cartItemList;
    }

    /**
     * 아이템 추가
     * @param cartItem
     */
    public void addItem(CartItem cartItem){
        cartItemList.add(cartItem);
    }

    /**
     * 체크박스 선택 체크된 아이템 맵을 반환.
     *
     * @return
     */
    public Map<Long, CartItem> getCheckedItemMap(){
        return this.checkedItemMap;
    }

    /**
     * 체크된 아이템 맵을 비운다.
     */
    public void clearCheckedItemMap(){
        this.checkedItemMap.clear();
    }

    /**
     * cartItemList에서 선택한 아이템들 제거
     *
     * @param checkedItemMap
     */
    public void removeItems(Map<Long, CartItem> checkedItemMap){
        subtractCheckedCartItem(checkedItemMap);

        notifyDataSetChanged();
        this.clearCheckedItemMap();                                 // 체크 된 아이템들을 제거.
    }

    /**
     * 전체 아이템 목록에서 체크된 아이템을 제거한다.
     * @param checkedItemMap
     */
    private void subtractCheckedCartItem(Map<Long, CartItem> checkedItemMap) {
        List<CartItem> checkedCartItemList = new ArrayList<>(checkedItemMap.values());
        Collection<CartItem> removedCartItemList = CollectionUtils.removeAll(cartItemList, checkedCartItemList);
        setCartItemList((List<CartItem>) removedCartItemList);      // 삭제되고 남은 아이템으로 재할당.
    }

    /**
     * 아이템의 갯수 반환. 반환 된 수만큼 실제 리스트뷰에 아이템이 표시 됨.
     * @return
     */
    @Override
    public int getCount() {
        return cartItemList.size();
    }

    @Nullable
    @Override
    public CartItem getItem(int position) {
        return cartItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 리스트뷰에 표시 될 뷰그룹(아이템 포함)을 생성. 아이템의 갯수만큼 내부적으로 반복 호출 됨.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if(view == null){
            // 아이템 뷰 inflation.
            view = inflater.inflate(R.layout.item_layout, null);

            viewHolder = new ViewHolder();
            viewHolder.itemCheckBox = (CheckBox) view.findViewById(R.id.itemChkbox);
            viewHolder.itemTextView = (TextView) view.findViewById(R.id.itemText);
            viewHolder.itemPurchasedButton = (Button) view.findViewById(R.id.itemPurchasedButton);
            viewHolder.itemModifyButton = (Button) view.findViewById(R.id.itemModifyButton);
            viewHolder.itemCopyButton = (Button) view.findViewById(R.id.itemCopyButton);

            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        setWidget(viewHolder, position);
        return view;
    }

    /**
     * 리스트 뷰에 그려질 아이템 위젯들을 셋팅한다.
     * @param viewHolder
     * @param position
     */
    private void setWidget(ViewHolder viewHolder, int position) {
        setItemCheckBox(viewHolder, position);
        setItemTextView(viewHolder, position);
        setItemPurchasedButton(viewHolder, position);
        setItemModifyButton(viewHolder, position);
        setItemCopyButton(viewHolder, position);
    }

    private void setItemPurchasedButton(ViewHolder viewHolder, int position) {
        CartItem cartItem = getItem(position);
        int buttonImage = getPurchasedButtonImage(cartItem);
        viewHolder.itemPurchasedButton.setBackgroundResource(buttonImage);

        viewHolder.itemPurchasedButton.setOnClickListener(new ItemPurchasedButtonClickListener(viewHolder, position));
    }

    private void setItemModifyButton(ViewHolder viewHolder, int position) {
        viewHolder.itemModifyButton.setOnClickListener(new ItemModifyButtonClickListener(position));
    }

    private void setItemCopyButton(ViewHolder viewHolder, int position) {
        viewHolder.itemCopyButton.setOnClickListener(new ItemCopyButtonClickListener(viewHolder, position));
    }

    private void setItemTextView(ViewHolder viewHolder, int position) {
        CartItem cartItem = getItem(position);
        viewHolder.itemTextView.setText(cartItem.getItemText());

        if(isPurchased(cartItem))
            viewHolder.itemTextView.setTextColor(ContextCompat.getColor(context, R.color.color_purchased_item_text));
        else
            viewHolder.itemTextView.setTextColor(ContextCompat.getColor(context, R.color.color_black));
    }

    private void setItemCheckBox(ViewHolder viewHolder, int position) {
        CartItem cartItem = getItem(position);
        viewHolder.itemCheckBox.setChecked(isChecked(cartItem));

        viewHolder.itemCheckBox.setOnCheckedChangeListener(new ItemCheckedChangeListener(position));
    }

    private int getPurchasedButtonImage(CartItem cartItem) {
        return cartItem.isPurchased() ? R.drawable.ic_purchased_item : R.drawable.ic_unpurchased_item;
    }

    private boolean isChecked(CartItem cartItem) {
        return cartItem.isChecked() ? true : false;
    }

    private boolean isPurchased(CartItem cartItem) {
        return cartItem.isPurchased() ? true : false;
    }

    /**
     * 아이템을 체크 선택. 아이템 삭제 용도로 사용된다.
     */
    class ItemCheckedChangeListener implements CompoundButton.OnCheckedChangeListener{
        private int position;

        ItemCheckedChangeListener(int position){
            this.position = position;
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
            CartItem cartItem = cartItemList.get(position);
            long cartItemId = cartItem.getCartItemId();

            /**
             * checked 아이템을 컬렉션에 담고, unchecked 아이템은 컬렉션에서 제거한다.
             */
            if(isChecked)
                checkedItemMap.put(cartItemId, cartItem);
            else
                checkedItemMap.remove(cartItemId);

            Cart cart = realm.where(Cart.class).equalTo("cartItems.cartItemId", cartItemId).findFirst();
            realm.beginTransaction();
            cart.getCartItems().get(0).setChecked(isChecked);
            cartItemList.get(position).setChecked(isChecked);
            realm.commitTransaction();


        }
    }

    /**
     * 아이템 구매 버튼 클릭 리스너
     * 1. CartItem isPurchased가 false이면
     *      - isPurchased를 true로 변경
     *      - 버튼 텍스트를 '구매완료'로 변경
     *      - 버튼 색깔을 회색으로 변경.
     * 2. CartItem isPurchased가 true이면
     *      - isPurchased를 false로 변경
     *      - 버튼 텍스트를 '구매전'으로 변경
     *      - 버튼 색깔을 원래 색으로 변경.
     */
    class ItemPurchasedButtonClickListener implements View.OnClickListener{
        private ViewHolder viewHolder;
        private int position;

        public ItemPurchasedButtonClickListener(ViewHolder viewHolder, int position){
            this.viewHolder = viewHolder;
            this.position = position;
        }

        /**
         * 구매 여부에 따라 구매 상태 변경 및 텍스트 변경.
         * @param v
         */
        @Override
        public void onClick(View v) {
            CartItem cartItem = getItem(position);
            long cartItemId = cartItem.getCartItemId();

            boolean purchasedStauts;
            int buttonImage;
            int buttonColor;

            if(cartItem.isPurchased()){
                purchasedStauts = false;
                buttonImage = R.drawable.ic_unpurchased_item;
                buttonColor = R.color.color_black;
            }else{
                purchasedStauts = true;
                buttonImage = R.drawable.ic_purchased_item;
                buttonColor = R.color.color_purchased_item_text;
            }

            viewHolder.itemPurchasedButton.setBackgroundResource(buttonImage);
            viewHolder.itemTextView.setTextColor(ContextCompat.getColor(context, buttonColor));

            Cart cart = realm.where(Cart.class).equalTo("cartItems.cartItemId", cartItemId).findFirst();
            realm.beginTransaction();
            cart.getCartItems().get(0).setPurchased(purchasedStauts);
            cartItemList.get(position).setPurchased(purchasedStauts);
            realm.commitTransaction();

        }
    }

    class ItemModifyButtonClickListener implements View.OnClickListener{
        private int position;

        public ItemModifyButtonClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Log.d("CartItemListAdapter", "modify item.");

            CartItem cartItem = getItem(position);

            Intent intent = new Intent(context, ItemModifyActivity.class);
            intent.putExtra("cartItemId", cartItem.getCartItemId());
            intent.putExtra("itemText", cartItem.getItemText());
            intent.putExtra("position", position);
            ((Activity)context).startActivityForResult(intent, REQUEST_CODE_MODIFY_ITEM);
        }
    }

    // TODO 구현 필요
    class ItemCopyButtonClickListener implements View.OnClickListener{

        private ViewHolder viewHolder;
        private int position;

        public ItemCopyButtonClickListener(ViewHolder viewHolder, int position){
            this.viewHolder = viewHolder;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Log.d("CartItemListAdapter", "## push an item copy button.");
            CartItem cartItem = getItem(position);

            Intent intent = new Intent(context, ItemCopyActivity.class);
            intent.putExtra("cartItemId", cartItem.getCartItemId());
            intent.putExtra("itemText", cartItem.getItemText());
            ((Activity)context).startActivityForResult(intent, REQUEST_CODE_COPY_ITEM);
        }
    }
}
