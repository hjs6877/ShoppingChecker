package com.soom.shoppingchecker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.soom.shoppingchecker.R;
import com.soom.shoppingchecker.model.Cart;

import java.util.List;

/**
 * Created by kjs on 2017-03-24.
 */

public class CartSpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<Cart> cartList;
    private LayoutInflater inflater;

    public CartSpinnerAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
        inflater = LayoutInflater.from(context);
    }

    public void setCartList(List<Cart> cartList){
        this.cartList = cartList;
    }
    @Override
    public int getCount() {
        return cartList.size();
    }

    @Override
    public Object getItem(int position) {
        return cartList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cart cart = cartList.get(position);
        convertView = inflater.inflate(R.layout.cart_spinner_layout, null);
        convertView.setTag(cart);
        TextView txtViewCartName = (TextView) convertView.findViewById(R.id.txtCartNameForSpinner);
        txtViewCartName.setText(cart.getCartName());
        return convertView;
    }
}
