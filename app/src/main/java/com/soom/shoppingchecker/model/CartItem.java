package com.soom.shoppingchecker.model;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by kjs on 2016-12-08.
 */

public class CartItem extends RealmObject implements Serializable{
    @PrimaryKey
    private long cartItemId;
    private String itemText;
    private boolean isChecked;
    private boolean isPurchased;
    private Date createDate;
    private Date updateDate;

    public CartItem(){}

    public CartItem(long cartItemId, String itemText, boolean isChecked, boolean isPurchased,
                    Date createDate, Date updateDate) {
        this.cartItemId = cartItemId;
        this.itemText = itemText;
        this.isChecked = isChecked;
        this.isPurchased = isPurchased;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public long getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    // List에서 Object를 비교할 때 사용.
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        CartItem cart = (CartItem) obj;

        if(cartItemId == cart.getCartItemId())
            return true;

        return false;
    }
}
