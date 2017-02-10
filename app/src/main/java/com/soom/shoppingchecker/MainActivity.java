package com.soom.shoppingchecker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
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
import com.soom.shoppingchecker.database.DBController;
import com.soom.shoppingchecker.database.SQLData;
import com.soom.shoppingchecker.model.CartItem;
import com.soom.shoppingchecker.service.CartItemService;
import com.soom.shoppingchecker.utils.DateUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView navigationView;
    public static final String TAG = "MainActivity";
    public static final int REQUEST_CODE_ITEMMODIFY = 1001;

    private ListView itemListView;
    private DBController dbController;
    private CartItemService cartItemService;

    private Button buttonAdd;
    private EditText editItemText;
    private TextView emptyItemTxt;

    private CartItemListAdapter adapter;

    public MainActivity(){
        dbController = new DBController(this);
        cartItemService = new CartItemService(dbController);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "onCreate() is called.");

        initActivity();
        initViews();




        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        Button btnFetchMenu = (Button) findViewById(R.id.btnFetchMenu);
//        btnFetchMenu.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Fetch menu!", Toast.LENGTH_SHORT).show();
//
//                Menu menu = navigationView.getMenu();
//                for(int i=0; i < menu.size(); i++){
//                    MenuItem menuItem = menu.getItem(i);
//                    Log.i("Fetch menu", "title: " + menuItem.getTitle());
//                    Log.i("Fetch menu", "order: " + String.valueOf(menuItem.getOrder()));
//                }
//            }
//        });
//
//        Button btnAddSubmenu = (Button) findViewById(R.id.btnAddSubmenu);
//        btnAddSubmenu.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Add Submenu!", Toast.LENGTH_SHORT).show();
//
//                Menu menu = navigationView.getMenu();
//                SubMenu subMenu = menu.addSubMenu(100, 1000, 0, "쇼핑리스트");
//                subMenu.add("서브메뉴1");
//                subMenu.add("서브메뉴2");
//            }
//        });


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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    private void initViews() {
        List<CartItem> cartItemList = getCartItemList();

        // 리스트뷰에 어댑터 연결.
        itemListView = (ListView) findViewById(R.id.itemListView);
        adapter = new CartItemListAdapter(this, cartItemList, dbController);
        itemListView.setAdapter(adapter);

        // 아이템 long click 시, 아이템 수정을 위한 리스너 등록
        itemListView.setOnItemLongClickListener(new ItemLongClickListener(this));

        // 아이템 입력을 위한 이벤트 리스너 등록.
        editItemText = (EditText) findViewById(R.id.editItemText);
        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new ItemAddClickListener(this));

        emptyItemTxt = (TextView) findViewById(R.id.emptyItemTxt);

        setEmptyItemTxt();
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
        Map<Integer, CartItem> checkedItemMap = adapter.getCheckedItemMap();

        switch (item.getItemId()){
            /**
             * - DB에서 item 삭제.
             * - cartItemList에서 item 삭제
             * - item 갱신
             */
            case R.id.action_item_delete:
                deleteCartItem(checkedItemMap);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

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

        if(requestCode == REQUEST_CODE_ITEMMODIFY){
            if(resultCode == RESULT_OK){
                String modifiedItemText = data.getExtras().getString("modifiedItemText");
                int position = data.getExtras().getInt("position");

                adapter.getCartItemList().get(position).setItemText(modifiedItemText);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 아이템 조회
     *
     * @return
     */
    private List<CartItem> getCartItemList() {
        return cartItemService.selectAll(SQLData.SQL_SELECT_ALL_ITEM);
    }

    private void setEmptyItemTxt() {
        if(adapter.getCartItemList().size() > 0)
            emptyItemTxt.setVisibility(View.GONE);
        else
            emptyItemTxt.setVisibility(View.VISIBLE);
    }

    private void deleteCartItem(Map<Integer, CartItem> checkedItemMap) {
        if(checkedItemMap.size() == 0) {
            Toast.makeText(this, R.string.toast_no_delete_item, Toast.LENGTH_SHORT).show();
            return;
        }
        for(Map.Entry<Integer, CartItem> map : checkedItemMap.entrySet()){
            CartItem cartItem = map.getValue();
            cartItemService.deleteData(SQLData.SQL_DELETE_ITEM, cartItem.getRegId());
        }
        adapter.removeItems(checkedItemMap);
        Toast.makeText(this, R.string.toast_deleted_item, Toast.LENGTH_SHORT).show();

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
            if(itemText.isEmpty()){
                Toast.makeText(context, R.string.toast_no_input_item, Toast.LENGTH_SHORT).show();
            }else{
                String currentDate = DateUtil.currentDateToString();

                // max reg_id를 조회한다.
                int maxRegId = cartItemService.selectMaxRegId(SQLData.SQL_SELECT_MAX_REG_ID);
                int regId = maxRegId + 1;

                insertItem(regId, itemText, currentDate);
                refreshCartItems(regId, itemText, currentDate);
                setEmptyItemTxt();
            }

        }

        private void insertItem(int regId, String itemText, String currentDate) {
            // DB에 아이템 추가
            cartItemService.insertCartItem(SQLData.SQL_INSERT_ITEM, new CartItem(regId, 0, 0, itemText,currentDate, currentDate));
        }

        private void refreshCartItems(int regId, String itemText, String currentDate) {
            // 리스트뷰에 아이템 추가 및 갱신
            adapter.addItem(new CartItem(regId, 0, 0, itemText, currentDate, currentDate));
            Collections.sort(adapter.getCartItemList(), new CartItemComparator());
            adapter.notifyDataSetChanged();

            // editText의 텍스트 지워서 초기화.
            editItemText.setText(null);
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
            startActivityForResult(intent, REQUEST_CODE_ITEMMODIFY);
            return false;
        }
    }
}
