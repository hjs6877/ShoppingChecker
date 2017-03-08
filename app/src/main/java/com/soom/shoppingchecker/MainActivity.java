package com.soom.shoppingchecker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.soom.shoppingchecker.adapter.CartItemListAdapter;
import com.soom.shoppingchecker.comparator.CartItemComparator;
import com.soom.shoppingchecker.database.DBController;
import com.soom.shoppingchecker.database.SQLData;
import com.soom.shoppingchecker.model.Cart;
import com.soom.shoppingchecker.model.CartItem;
import com.soom.shoppingchecker.service.CartItemService;
import com.soom.shoppingchecker.service.CartService;
import com.soom.shoppingchecker.utils.DateUtil;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

import static android.widget.Toast.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView navigationView;
    public static final String TAG = "MainActivity";

    public static final int REQUEST_CODE_ADD_CART = 1001;
    public static final int REQUEST_CODE_MODIFY_ITEM = 1002;

    private ListView itemListView;
    private DBController dbController;
    private CartService cartService;
    private CartItemService cartItemService;

    private Button buttonAdd;
    private EditText editItemText;
    private TextView emptyItemTxt;

    private CartItemListAdapter adapter;
    private List<Cart> carts;
    private Realm realm = Realm.getDefaultInstance();
    private LayoutInflater inflater;

    public MainActivity(){
        dbController = new DBController(this);
        cartService = new CartService();
        cartItemService = new CartItemService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "onCreate() is called.");

        initActivity();
        initViews();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart() is called.");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart() is called.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume() is called.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause() is called.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop() is called.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy() is called.");
    }

    private void initActivity() {
        // 앱바 추가
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new FloatingActionButtonClickListener(this));

    }

    private void initViews() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        createShoppingListMenu();

        // 디폴트 쇼핑 리스트의 쇼핑 아이템 조회.
        Cart defaultCart = cartService.findOneCartByCartId(1);
        setTitle(defaultCart.getCartName());

        // 리스트뷰에 어댑터 연결.
        itemListView = (ListView) findViewById(R.id.itemListView);
        adapter = new CartItemListAdapter(this, defaultCart.getCartItems());
        itemListView.setAdapter(adapter);

        // 아이템 long click 시, 아이템 수정을 위한 리스너 등록
        itemListView.setOnItemLongClickListener(new ItemLongClickListener(this));

        // 아이템 입력을 위한 이벤트 리스너 등록.
        editItemText = (EditText) findViewById(R.id.editItemText);
        editItemText.setTag(R.string.key_cartId, defaultCart.getCartId());
        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new ItemAddClickListener(this));

        emptyItemTxt = (TextView) findViewById(R.id.emptyItemTxt);

        setEmptyItemTxt();
    }

    /**
     * 쇼핑 목록을 조회하여 서브 메뉴로 추가한다.
     */
    private void createShoppingListMenu() {
        carts = cartService.findAllCart();

        Menu menu = navigationView.getMenu();

        SubMenu subMenu = menu.addSubMenu(100, 1000, 0, R.string.txt_shopping_list);


        for(Cart cart : carts){
            inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.cart_modify_layout, null);

            MenuItem menuItem = subMenu.add(101, (int) cart.getCartId(), 0, cart.getCartName());

            // 기본 카트는 수정 및 삭제가 불가능하도록 아이콘을 표시하지 않는다.
            if(!cart.getCartName().equals("Common")){
                menuItem.setActionView(view);

                Button buttonModifyCart = (Button) view.findViewById(R.id.buttonModifyCart);
                Button buttonDeleteCart = (Button) view.findViewById(R.id.buttonDeleteCart);

                buttonModifyCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("MainActivity", "buttonModifyCart click!!");
                    }
                });

                buttonDeleteCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("MainActivity", "buttonDeleteCart click!!");
                    }
                });
            }

            menuItem.setIcon(R.drawable.ic_shopping_basket);

        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Map<Long, CartItem> checkedItemMap = adapter.getCheckedItemMap();

        switch (item.getItemId()){
            /**
             * - DB에서 item 삭제.
             * - cartItemList에서 item 삭제
             * - item 갱신
             */
            case R.id.action_item_delete:
                // TODO Realm 전환.
//                deleteCartItem(checkedItemMap);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // DB에서 id에 해당하는 cart를 조회해서 listview를 갱신한다.
        Cart cart = cartService.findOneCartByCartId(id);
        adapter.setCartItemList(cart.getCartItems());
        adapter.notifyDataSetChanged();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 활성화 상태의 Activity로부터 응답을 받아 처리.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_ADD_CART){
            if(resultCode == RESULT_OK){
                Menu menu = navigationView.getMenu();
                menu.clear();
                createShoppingListMenu();
            }
        }else if(requestCode == REQUEST_CODE_MODIFY_ITEM){
            if(resultCode == RESULT_OK){
                String modifiedItemText = data.getExtras().getString("modifiedItemText");
                int position = data.getExtras().getInt("position");

                adapter.getCartItemList().get(position).setItemText(modifiedItemText);
                adapter.notifyDataSetChanged();
            }
        }
    }


    private void setEmptyItemTxt() {
        if(adapter.getCartItemList().size() > 0)
            emptyItemTxt.setVisibility(View.GONE);
        else
            emptyItemTxt.setVisibility(View.VISIBLE);
    }

    private void deleteCartItem(Map<Integer, CartItem> checkedItemMap) {
        if(checkedItemMap.size() == 0) {
            makeText(this, R.string.toast_no_delete_item, LENGTH_SHORT).show();
            return;
        }
        for(Map.Entry<Integer, CartItem> map : checkedItemMap.entrySet()){
            CartItem cartItem = map.getValue();
            // TODO Realm 전환.
//            cartItemService.deleteCartItem(SQLData.SQL_DELETE_ITEM, cartItem.getCartItemId());
        }
        adapter.removeItems(checkedItemMap);
        makeText(this, R.string.toast_deleted_item, LENGTH_SHORT).show();

        setEmptyItemTxt();
    }

    /**
     * 아이템 추가 버튼 클릭 리스너
     */
    private class ItemAddClickListener implements View.OnClickListener{
        private Context context;

        public ItemAddClickListener(Context context){
            this.context = context;
        }

        /**
         * DB에 아이템 저장.
         *
         * @param v
         */
        @Override
        public void onClick(View v) {
            String itemText = editItemText.getEditableText().toString();
            long cartId = (long) editItemText.getTag(R.string.key_cartId);

            if(itemText.isEmpty()){
                makeText(context, R.string.toast_no_input_item, LENGTH_SHORT).show();
            }else{
                // max cartItem ID를 조회한다.
                long maxCartItemId = cartItemService.findMaxCartItemId();

                long cartItemId = maxCartItemId + 1;

                insertItem(cartId, cartItemId, itemText);
                refreshCartItems(cartId);
                setEmptyItemTxt();
            }

        }

        private Cart insertItem(long cartId, long cartItemId, String itemText) {
            realm.beginTransaction();
            Cart cart = cartService.findOneCartByCartId(cartId);
            CartItem cartItem = new CartItem(cartItemId, itemText, false, false, new Date(), new Date());
            cart.getCartItems().add(realm.copyToRealm(cartItem));
            realm.commitTransaction();
            return cart;
        }

        private void refreshCartItems(long cartId) {
            List<CartItem> cartItems = cartService.findOneCartByCartId(cartId).getCartItems();
            adapter.setCartItemList(cartItems);
            realm.beginTransaction();
            Collections.sort(cartItems, new CartItemComparator());
            realm.commitTransaction();

            adapter.notifyDataSetChanged();

            // editText의 텍스트 지워서 초기화.
            editItemText.setText(null);
        }
    }

    /**
     * 메뉴 아이템 long click Listener
     * - 메뉴 아이템의 이름을 수정 한다.
     * - 기본 메뉴는 수정할 수 없다는 Toast message를 표시한다.
     */
    private class MenuItemLongClickListener implements View.OnLongClickListener {
        private Context context;
        public MenuItemLongClickListener(Context context){
            this.context = context;
        }
        @Override
        public boolean onLongClick(View v) {
            makeText(context, "메뉴 아이템 long Click", LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 아이템 Long Click Listener
     */
    private class ItemLongClickListener implements AdapterView.OnItemLongClickListener {
        private Context context;

        public ItemLongClickListener(Context context){
            this.context = context;
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("ItemLongClickListener", "long click!!");
            CartItem cartItem = adapter.getCartItemList().get(position);

            Intent intent = new Intent(context, ItemModifyActivity.class);
            intent.putExtra("cartItem", cartItem);
            intent.putExtra("position", position);
            startActivityForResult(intent, REQUEST_CODE_MODIFY_ITEM);
            return false;
        }
    }

    private class FloatingActionButtonClickListener implements View.OnClickListener {
        private Context context;

        public FloatingActionButtonClickListener(Context context){
            this.context = context;
        }

        @Override
        public void onClick(View view) {
            Log.d("FABClickListener", "Floating Action Button Click!!");

            Intent intent = new Intent(context, CartCreateActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_CART);
        }
    }


}
