package com.soom.shoppingchecker.model;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.Date;

import io.realm.CartRealmProxy;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by kjs on 2017-02-14.
 */
@Parcel(implementations = { CartRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { Cart.class })
public class Cart extends RealmObject implements Serializable {
    @PrimaryKey
    private long cartId;
    private String cartName;
    private Date createDate;
    private Date updateDate;
    private RealmList<CartItem> cartItems;

    public Cart(){}

    public Cart(long cartId, String cartName, Date createDate, Date updateDate) {
        this.cartId = cartId;
        this.cartName = cartName;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public long getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public String getCartName() {
        return cartName;
    }

    public void setCartName(String cartName) {
        this.cartName = cartName;
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

    public RealmList<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(RealmList<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

}
