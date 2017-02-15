package com.soom.shoppingchecker;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.soom.shoppingchecker.model.Cart;
import com.soom.shoppingchecker.service.CartService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

import static org.junit.Assert.assertEquals;

/**
 * Created by kjs on 2017-02-15.
 */
@RunWith(AndroidJUnit4.class)
public class CartServiceTest {

    @Test
    public void useAppContext() throws Exception{
        Context context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void saveCartTest(){
        Cart cart = new Cart(1, "test Cart4", new Date(), new Date());

        CartService cartService = new CartService();
        cartService.deleteAllCart();
        Cart realmCart = cartService.saveCart(cart);

        Log.i("CartServiceTest", "cart name: " + realmCart.getCartName());
        assertEquals("test Cart4", realmCart.getCartName());
    }
}
