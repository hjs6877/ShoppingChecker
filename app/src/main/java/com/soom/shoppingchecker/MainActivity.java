package com.soom.shoppingchecker;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.soom.shoppingchecker.adapter.CartItemListAdapter;
import com.soom.shoppingchecker.comparator.CartItemComparator;
import com.soom.shoppingchecker.listener.SwipeDismissListViewTouchListener;
import com.soom.shoppingchecker.model.Cart;
import com.soom.shoppingchecker.model.CartItem;
import com.soom.shoppingchecker.service.CartItemService;
import com.soom.shoppingchecker.service.CartService;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

import static android.widget.Toast.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView navigationView;
    public static final String TAG = "MainActivity";

    public static final int REQUEST_CODE_ADD_CART = 1001;
    public static final int REQUEST_CODE_MODIFY_CART = 1002;
    public static final int REQUEST_CODE_DELETE_CART = 1003;
    public static final int REQUEST_CODE_MODIFY_ITEM = 2001;
    public static final int REQUEST_CODE_ADD_CART_FROM_ITEM_COPY = 3001;
    public static final int CART_MODE_CREATE = 1;
    public static final int CART_MODE_MODIFY = 2;

    private ListView itemListView;
    private CartService cartService;
    private CartItemService cartItemService;

    private Button buttonAdd;
    private EditText editItemText;
    private TextView emptyItemTxt;

    private CartItemListAdapter adapter;
    private List<Cart> carts;
    private Realm realm = Realm.getDefaultInstance();
    private LayoutInflater inflater;
    private Context context;

    public MainActivity(){
        this.context = this;
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
        fab.setOnClickListener(new FloatingActionButtonClickListener(context));

    }

    private void initViews() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // 디폴트 쇼핑 리스트의 쇼핑 아이템 조회.
        Cart defaultCart = cartService.findOneCartByCartId(1);
        setTitle(defaultCart.getCartName());

        // 리스트뷰에 어댑터 연결.
        itemListView = (ListView) findViewById(R.id.itemListView);

        // 아이템을 추가 및 삭제할 때 cartId가 필요.
        adapter = new CartItemListAdapter(context, defaultCart.getCartItems());
        adapter.setCurrentCartId(defaultCart.getCartId());
        itemListView.setAdapter(adapter);

        SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(itemListView,
            new SwipeDismissListViewTouchListener.DismissCallbacks() {
                @Override
                public boolean canDismiss(int position) {
                    return true;
                }

                @Override
                public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                    realm.beginTransaction();
                    long currentCartId = adapter.getCurrentCartId();

                    for (int position : reverseSortedPositions) {
                        CartItem removedCartItem = adapter.getItem(position);

                        // listview의 카트 아이템을 삭제.
                        adapter.removeItem(position);

                        cartItemService.deleteCartItem(currentCartId, removedCartItem);
                    }

                    adapter.notifyDataSetChanged();
                    realm.commitTransaction();

                    setEmptyItemTxt();
                }
        });
        itemListView.setOnTouchListener(touchListener);
        itemListView.setOnScrollListener(touchListener.makeScrollListener());

        // 아이템 입력을 위한 이벤트 리스너 등록.
        editItemText = (EditText) findViewById(R.id.editItemText);
        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new ItemAddClickListener(context));

        emptyItemTxt = (TextView) findViewById(R.id.emptyItemTxt);

        setEmptyItemTxt();

        createShoppingListMenu();
    }

    /**
     * 쇼핑 목록을 조회하여 서브 메뉴로 추가한다.
     */
    private void createShoppingListMenu() {
        carts = cartService.findAllCart();

        Menu menu = navigationView.getMenu();

        SubMenu subMenu = menu.addSubMenu(100, 1000, 0, R.string.txt_shopping_list);


        for(Cart cart : carts){
            final long cartId = cart.getCartId();
            final String cartName = cart.getCartName();

            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.cart_modify_layout, null);

            MenuItem menuItem = subMenu.add(R.id.cart_menu_group_id, (int) cartId, 0, cart.getCartName());
            menuItem.setCheckable(true);

            // 기본 카트는 수정 및 삭제가 불가능하도록 아이콘을 표시하지 않는다.
            if(!cart.getCartName().equals("Common")){
                menuItem.setActionView(view);

                Button buttonModifyCart = (Button) view.findViewById(R.id.buttonModifyCart);
                Button buttonDeleteCart = (Button) view.findViewById(R.id.buttonDeleteCart);

                buttonModifyCart.setOnClickListener(new CartModifyClickListener(cartId, cartName));


                buttonDeleteCart.setOnClickListener(new CartDeleteClickListener(cartId));

            }

            menuItem.setIcon(R.drawable.ic_shopping_basket);

        }

        initSelectedCartMenuColor();

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
        MenuItem menuItem = menu.findItem(R.id.action_item_delete);
        menuItem.setVisible(false);
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
                deleteCartItems(checkedItemMap);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d("MainActivity", "selected Navigation Menu!");
        long id = item.getItemId();
        int groupId = item.getGroupId();

        // 카트 group일 경우 cart item을 listview에 표시한다.
        if(groupId == R.id.cart_menu_group_id){
            // DB에서 id에 해당하는 cart를 조회해서 listview를 갱신한다.
            Cart cart = cartService.findOneCartByCartId(id);
            adapter.setCurrentCartId(cart.getCartId());
            refreshCartItems(cart);
        }

        initSelectedCartMenuColor();

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

        if(requestCode == REQUEST_CODE_ADD_CART || requestCode == REQUEST_CODE_MODIFY_CART){
            if(resultCode == RESULT_OK){
                long cartId = data.getLongExtra("cartId", 0L);
                refreshCart(cartId);
            }
        }else if(requestCode == REQUEST_CODE_ADD_CART_FROM_ITEM_COPY){
            if(resultCode == RESULT_OK){
                refreshCartMenuList();
                Toast.makeText(context, R.string.toast_copied_item, Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", String.valueOf(R.string.toast_copied_item));
            }
        }else if(requestCode == REQUEST_CODE_DELETE_CART){
            if(resultCode == RESULT_OK){
                refreshCart(1);
            }
        }else if(requestCode == REQUEST_CODE_MODIFY_ITEM){
            if(resultCode == RESULT_OK){
                Log.d("MainActivity", "## Item modified.");
                String modifiedItemText = data.getExtras().getString("modifiedItemText");
                int position = data.getExtras().getInt("position");

                realm.beginTransaction();
                adapter.getCartItemList().get(position).setItemText(modifiedItemText);
                realm.commitTransaction();
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 카트 추가, 수정, 삭제 시 카트를 갱신한다.
     * @param cartId
     */
    private void refreshCart(long cartId) {
        Cart cart = cartService.findOneCartByCartId(cartId);
        adapter.setCurrentCartId(cartId);
        refreshCartMenuList();
        refreshCartItems(cart);
    }

    /**
     * cart에 해당하는 cart item들을 갱신한다.
     * @param cart
     */
    private void refreshCartItems(Cart cart) {
        setTitle(cart.getCartName());
        adapter.setCartItemList(cart.getCartItems());
        adapter.notifyDataSetChanged();
        setEmptyItemTxt();
    }

    /**
     * cart menu를 갱신한다.
     */
    private void refreshCartMenuList(){
        Menu menu = navigationView.getMenu();
        menu.clear();
        createShoppingListMenu();
    }

    /**
     * 선택된 cart menu를 하일라이팅한다.
     */
    private void initSelectedCartMenuColor(){
        Menu menu = navigationView.getMenu();
        MenuItem cartMenuItem = menu.getItem(0);

        SubMenu cartSubmenu = cartMenuItem.getSubMenu();
        for(int i = 0; i < cartSubmenu.size(); i++){
            MenuItem cartSubMenuItem = cartSubmenu.getItem(i);
            long cartId = adapter.getCurrentCartId();
            if(cartId == cartSubMenuItem.getItemId()){
                cartSubMenuItem.setChecked(true);
            }else{
                cartSubMenuItem.setChecked(false);
            }

        }
    }

    /**
     * empty message를 보여주기위한 설정을 한다.
     */
    private void setEmptyItemTxt() {
        if(adapter.getCartItemList().size() > 0)
            emptyItemTxt.setVisibility(View.GONE);
        else
            emptyItemTxt.setVisibility(View.VISIBLE);
    }

    /**
     * cart item id에 해당하는 cart item을 삭제한다.
     * @param checkedItemMap
     */
    private void deleteCartItems(Map<Long, CartItem> checkedItemMap) {
        if(checkedItemMap.size() == 0) {
            makeText(this, R.string.toast_no_delete_item, LENGTH_SHORT).show();
            return;
        }

        realm.beginTransaction();
        long currentCartId = adapter.getCurrentCartId();
        cartItemService.deleteCartItems(currentCartId, checkedItemMap);
        realm.commitTransaction();

        adapter.removeItems(checkedItemMap);
        makeText(this, R.string.toast_deleted_item, LENGTH_SHORT).show();

        setEmptyItemTxt();
    }

    /**
     * 카트 수정 버튼 클릭 리스너
     */
    private class CartModifyClickListener implements View.OnClickListener{
        private long cartId;
        private String cartName;

        public CartModifyClickListener(long cartId, String cartName){
            this.cartId = cartId;
            this.cartName = cartName;
        }
        @Override
        public void onClick(View v) {
            Log.d("CartModifyClickListener", "Cart modify button Click!!");

            Intent intent = new Intent(context, CartCreateActivity.class);
            intent.putExtra("mode", MainActivity.CART_MODE_MODIFY);
            intent.putExtra("cartId", cartId);
            intent.putExtra("cartName", cartName);
            startActivityForResult(intent, REQUEST_CODE_MODIFY_CART);
        }
    }

    /**
     * cart 삭제 버튼 클릭 리스너
     */
    private class CartDeleteClickListener implements View.OnClickListener {
        private long cartId;

        public CartDeleteClickListener(long cartId) {
            this.cartId = cartId;
        }

        @Override
        public void onClick(View v) {
            Log.i("MainActivity", "buttonDeleteCart click!!");

            Intent intent = new Intent(context, CartDeleteActivity.class);
            intent.putExtra("cartId", cartId);
            startActivityForResult(intent, REQUEST_CODE_DELETE_CART);
        }
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
            long cartId = adapter.getCurrentCartId();

            if(itemText.isEmpty()){
                Toast.makeText(context, R.string.toast_no_input_item, LENGTH_SHORT).show();
            }else{

                long cartItemId = cartItemService.getNewCartItemId();

                cartItemService.insertItem(cartId, cartItemId, itemText);
                refreshCartItems(cartId);
                setEmptyItemTxt();
            }

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
