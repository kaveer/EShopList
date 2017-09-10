package com.kavsoftware.kaveer.eshoplist.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.kavsoftware.kaveer.eshoplist.DbHandler.DbHandler;
import com.kavsoftware.kaveer.eshoplist.Model.ItemViewModel;
import com.kavsoftware.kaveer.eshoplist.Model.ListViewModel;
import com.kavsoftware.kaveer.eshoplist.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateListActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    ItemViewModel itemModel = new ItemViewModel();
    ListViewModel listModel = new ListViewModel();
    private  ArrayList<ItemViewModel> arrayItemsModel = new ArrayList<>();
    private  int listId;

    private EditText date;
    private EditText listTitle;
    private EditText item;
    private EditText price;
    private Spinner category;
    private ListView items;
    private Button addItem;
    private EditText quantity;
    private Button saveList;
    private ArrayList<String> arrayItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.create_list);
        setTitle("Create List");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        try{
            if (InitializeWidget()){
                SetDate();
                DisplayItemInListView("no item added");

                addItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (IsItemValid()){
                            SetItemModel();
                            String displayItem = itemModel.itemName
                                                + "     " + itemModel.quantity
                                                + "      " + itemModel.price + "     "
                                                + itemModel.totalPrice;
                            if(DisplayItemInListView(displayItem)){
                                arrayItemsModel.add(itemModel);
                            }
                        }
                    }
                });

                saveList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (arrayItemsModel.size() > 0) {
                            SetListModel();
                            if (SaveListDetails(listModel, CreateListActivity.this)) {
                                if (SaveItem(arrayItemsModel, listId, CreateListActivity.this)) {
                                    Toast messageBox = Toast.makeText(CreateListActivity.this, "List Saved ", Toast.LENGTH_LONG);
                                    messageBox.show();
                                }
                                else {
                                    Toast messageBox = Toast.makeText(CreateListActivity.this, "Unable to save list ", Toast.LENGTH_LONG);
                                    messageBox.show();
                                }
                            } else {
                                Toast messageBox = Toast.makeText(CreateListActivity.this, "List already exist", Toast.LENGTH_LONG);
                                messageBox.show();
                            }
                        }else {
                            Toast messageBox = Toast.makeText(CreateListActivity.this, "No item added cannot save list", Toast.LENGTH_LONG);
                            messageBox.show();
                        }
                    }
                });
            }
        }catch(Exception msg) {
            Toast messageBox = Toast.makeText(this, msg.getMessage(), Toast.LENGTH_LONG);
            messageBox.show();

            Log.e("Error", msg.getMessage());
            System.out.println("Error " + msg.getMessage());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private boolean SaveItem(ArrayList<ItemViewModel> arrayItemsModel, int listId, CreateListActivity listActivity) {
        DbHandler DB = new DbHandler(listActivity);
        boolean result = false;
        for (ItemViewModel item:arrayItemsModel) {
            try{
                item.listId = listId;
                DB.SaveItem(item);
                result = true;
            }
            catch (Exception msg){
                Log.e("Error", msg.getMessage());
                System.out.println("Error " + msg.getMessage());
                result = false;
            }
        }
        return result;
    }

    private boolean SaveListDetails(ListViewModel listModel, CreateListActivity listActivity) {
        DbHandler DB = new DbHandler(listActivity);
        ListViewModel isListExist = new ListViewModel();

        isListExist = DB.GetListByListNameAndStatus(listModel);
        if (isListExist.listId > 0){
            return false;
        }
        else {
            if (!DB.SaveList(listModel)){
                return false;
            }
            listModel = DB.GetListByListNameAndStatus(listModel);
            listId = listModel.listId;
            return  true;
        }
    }

    private void SetListModel() {

        listModel.listTitle = listTitle.getText().toString();
        listModel.listDate = date.getText().toString() ;
    }

    private void SetItemModel() {
        itemModel = new ItemViewModel();

        itemModel.itemName = item.getText().toString();
        itemModel.quantity = Integer.parseInt(quantity.getText().toString());
        itemModel.category = category.getSelectedItem().toString();
        itemModel.price = Double.parseDouble(price.getText().toString());
        itemModel.totalPrice = CalculateTotalPrice(itemModel.quantity, itemModel.price);
    }

    private double CalculateTotalPrice(int quantity, double price) {
        double result;
        result = quantity * price;

        return  result;
    }

    private boolean IsItemValid() {
        boolean result = true;

        if (listTitle.length() == 0){
            listTitle.setError("Enter List Title");
            return false;
        }
        if (item.length() == 0){
            item.setError("Enter item");
            return false;
        }
        if (quantity.length() == 0){
            quantity.setError("Enter Quantity");
            return false;
        }
        if (price.length() == 0){
            price.setText("0.0");
        }

        return result;
    }

    private boolean DisplayItemInListView(String item) {
        if (arrayItems.contains("no item added") && arrayItems.size() == 1){
            arrayItems.clear();
        }

        if (arrayItems.contains(item)){
            Toast messageBox = Toast.makeText(this, "Item already added", Toast.LENGTH_LONG);
            messageBox.show();

            return false;
        }
        else {
            arrayItems.add(item);
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayItems);
            items.setAdapter(adapter);

            return true;
        }
    }

    private void SetDate() {
        date.setText(GetDateNow());
        date.setKeyListener(null);
    }

    private boolean InitializeWidget() {
        try{
            date = (EditText) findViewById(R.id.txtDate);
            listTitle = (EditText) findViewById(R.id.txtTitle);
            item = (EditText) findViewById(R.id.txtItem);
            price = (EditText) findViewById(R.id.txtPrice);
            quantity = (EditText) findViewById(R.id.txtQuantity);
            category = (Spinner) findViewById(R.id.spinnerCategory);
            items = (ListView) findViewById(R.id.listItems);
            addItem = (Button) findViewById(R.id.btnSaveItem);
            saveList = (Button) findViewById(R.id.btnSaveList);

            return true;

        } catch (Exception msg) {
            Toast messageBox = Toast.makeText(this , msg.getMessage() , Toast.LENGTH_LONG);
            messageBox.show();

            Log.e("Error", msg.getMessage());
            System.out.println("Error " + msg.getMessage());

            return false;
        }
    }

    private String GetDateNow() {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //import java.text.SimpleDateFormat instead of android.icu.text.simpleDateFormat
            String currentDate = dateFormat.format(calendar.getTime());
            return currentDate;
        } catch (Exception msg) {
            Toast messageBox = Toast.makeText(this , msg.getMessage() , Toast.LENGTH_LONG);
            messageBox.show();

            Log.e("Error", msg.getMessage());
            System.out.println("Error " + msg.getMessage());
            return null;
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_create_list) {
            Intent i = new Intent(CreateListActivity.this, CreateListActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (id == R.id.nav_generate_list) {
            Intent i = new Intent(CreateListActivity.this, GenerateListActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (id == R.id.nav_view_list) {
            Intent i = new Intent(CreateListActivity.this, ViewListActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (id == R.id.nav_add_category) {
            Intent i = new Intent(CreateListActivity.this, AddCategoryActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
