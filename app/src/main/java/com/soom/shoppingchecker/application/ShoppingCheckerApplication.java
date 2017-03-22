package com.soom.shoppingchecker.application;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;

import com.facebook.stetho.Stetho;
import com.soom.shoppingchecker.model.Cart;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by kjs on 2017-02-15.
 */

public class ShoppingCheckerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());

        // 디폴트 쇼핑 리스트 생성.
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<Cart> query = realm.where(Cart.class);
        Cart defaultCart = query.equalTo("cartId", 1).findFirst();

        if(defaultCart == null){
            final Cart cart = new Cart(1, "Common", new Date(), new Date());
            realm.executeTransaction(new Realm.Transaction(){
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealm(cart);
                }
            });
        }

    }
}
