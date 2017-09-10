package com.kavsoftware.kaveer.eshoplist.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.kavsoftware.kaveer.eshoplist.DbHandler.DbHandler;
import com.kavsoftware.kaveer.eshoplist.Model.ItemViewModel;
import com.kavsoftware.kaveer.eshoplist.Model.ListViewModel;
import com.kavsoftware.kaveer.eshoplist.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class GenerateListActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener  {

    private ListView listShop;
    private Button saveList;
    private Button addItem;
    private ArrayList<String> arrayItems = new ArrayList<>();
    private ArrayList<ItemViewModel> items = new ArrayList<>();
    private ArrayList<ItemViewModel> hardCodedItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.generate_list);
        setTitle("Generate List");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {


            if (InitializeWidget()){

                Intent updatedItems = getIntent();
                Bundle extras = updatedItems.getExtras();

                if (extras != null) {
                    if (extras.containsKey("hasUpdated")) {
                        boolean hasUpdate = extras.getBoolean("hasUpdated", false);

                        if (hasUpdate){

                            ArrayList<ItemViewModel> result = (ArrayList<ItemViewModel>) extras.getSerializable("updatedItem");
                            items = result;
                            CalculateTotalPriceForItems(items);
                            PopulateListView(items);
                        }

                    }
                }else {

                    InformationPopUpMessage(this);
                    hardCodedItems = GetFavoriteItems();
                    items = RetrieveItems(this);

                    if (items.size() != 0){
                        for (ItemViewModel item: hardCodedItems) {
                            items.add(item);
                        }
                    }
                    else {
                        items = hardCodedItems;
                    }

                    SetDefaultPriceAndQuantity(items);
                    CalculateTotalPriceForItems(items);
                    PopulateListView(items);
                }

            }

            saveList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Context context = GenerateListActivity.this;
                    LinearLayout layout = new LinearLayout(context);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText listTitle = new EditText(GenerateListActivity.this);
                    listTitle.setHint("List name");
                    listTitle.setInputType(InputType.TYPE_CLASS_TEXT);
                    layout.addView(listTitle);

                    AlertDialog.Builder popUpMessage = new AlertDialog.Builder(GenerateListActivity.this);
                    popUpMessage.setMessage("Add list name")
                            .setTitle("Information")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    if (listTitle.getText().length() != 0){
                                        if(SaveList(listTitle.getText().toString())){
                                            Toast messageBox = Toast.makeText(GenerateListActivity.this , "List saved" , Toast.LENGTH_LONG);
                                            messageBox.show();

                                            Intent navigateToViewList = new Intent(GenerateListActivity.this, ViewListActivity.class);
                                            startActivity(navigateToViewList);

                                        }else {
                                            Toast messageBox = Toast.makeText(GenerateListActivity.this , "List already exist" , Toast.LENGTH_LONG);
                                            messageBox.show();
                                        }

                                    }else {
                                        Toast messageBox = Toast.makeText(GenerateListActivity.this , "Enter list name" , Toast.LENGTH_LONG);
                                        messageBox.show();
                                    }
                                }
                            });
                    popUpMessage.setIcon(R.drawable.appicon);
                    popUpMessage.setView(layout);
                    popUpMessage.create();
                    popUpMessage.show();

                }


            });

            addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(GenerateListActivity.this, UpdateGeneratedItemActivity.class);
                    i.putExtra("itemList",  items);
                    i.putExtra("itemPosition",  0);
                    startActivity(i);

                }


            });

            listShop.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    int itemPosition = position;

                    Intent i = new Intent(GenerateListActivity.this, UpdateGeneratedItemActivity.class);
                    i.putExtra("itemList",  items);
                    i.putExtra("itemPosition",  itemPosition);
                    startActivity(i);

                }

            });

        }catch (Exception msg){
            Toast messageBox = Toast.makeText(this , msg.getMessage() , Toast.LENGTH_LONG);
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

    private boolean SaveList(String listTitle) {
        DbHandler DB = new DbHandler(GenerateListActivity.this);
        ListViewModel isListExist = new ListViewModel();

        ListViewModel listModel = new ListViewModel();
        listModel.listTitle = listTitle;
        listModel.listDate = GetDateNow();

        isListExist = DB.GetListByListNameAndStatus(listModel);
        if (isListExist.listId > 0){
            return false;
        }
        else {
            if (!DB.SaveList(listModel)){
                return false;
            }
            listModel = DB.GetListByListNameAndStatus(listModel);
            int listId = listModel.listId;
            SaveItems(listId);

            return  true;
        }
    }

    private void SaveItems(int listId) {
        DbHandler DB = new DbHandler(GenerateListActivity.this);

        for (ItemViewModel item:items) {
            try{
                item.listId = listId;
                DB.SaveItem(item);
            }
            catch (Exception msg){
                Log.e("Error", msg.getMessage());
                System.out.println("Error " + msg.getMessage());
            }
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

    private void CalculateTotalPriceForItems(ArrayList<ItemViewModel> items) {
        for (ItemViewModel item:items) {
            item.totalPrice = CalculateTotalPrice(item.quantity, item.price);
        }
    }

    private void SetDefaultPriceAndQuantity(ArrayList<ItemViewModel> items) {
        for (ItemViewModel item: items) {
            item.quantity = 1;
            item.price = 0.0;
        }
    }

    private double CalculateTotalPrice(int quantity, double price) {
        double result;
        result = quantity * price;

        return  result;
    }

    private void PopulateListView(ArrayList<ItemViewModel> items) {

        for (ItemViewModel item: items) {

            arrayItems.add(item.itemName + " Quantity: " + item.quantity + " Price: " + item.price + " total price: " + item.totalPrice);
        }

        ArrayAdapter adapter = new ArrayAdapter(GenerateListActivity.this, android.R.layout.simple_list_item_1, arrayItems);
        listShop.setAdapter(adapter);
    }

    private ArrayList<ItemViewModel> GetFavoriteItems() {
        ArrayList<ItemViewModel> result = new ArrayList<>();
        ItemViewModel item ;

        item = new ItemViewModel();
        item.itemName = "Milk";
        item.price = 0.0;
        item.quantity = 1;
        result.add(item);

        item = new ItemViewModel();
        item.itemName = "Yogurt";
        item.price = 0.0;
        item.quantity = 1;
        result.add(item);

        item = new ItemViewModel();
        item.itemName = "Butter";
        item.price = 0.0;
        item.quantity = 1;
        result.add(item);

        item = new ItemViewModel();
        item.itemName = "Cheese";
        item.price = 0.0;
        item.quantity = 1;
        result.add(item);

        item = new ItemViewModel();
        item.itemName = "Rice";
        item.price = 0.0;
        item.quantity = 1;
        result.add(item);

        item = new ItemViewModel();
        item.itemName = "Flour";
        item.price = 0.0;
        item.quantity = 1;
        result.add(item);

        item = new ItemViewModel();
        item.itemName = "Cereal";
        item.price = 0.0;
        item.quantity = 1;
        result.add(item);

        item = new ItemViewModel();
        item.itemName = "Cookies";
        item.price = 0.0;
        item.quantity = 1;
        result.add(item);

        return result;
    }

    private ArrayList<ItemViewModel> RetrieveItems(GenerateListActivity generateListActivity) {
        DbHandler DB = new DbHandler(generateListActivity);
        ArrayList<ItemViewModel> result;

        result = DB.RetrieveItemsToGenerateList();

        return result;
    }

    private boolean InitializeWidget() {
        try{
            listShop = (ListView) findViewById(R.id.listViewGeneratedItems);
            saveList = (Button) findViewById(R.id.btnSaveGeneratedList);
            addItem = (Button) findViewById(R.id.BtnAddItemGeneratedList);

            return true;
        } catch (Exception msg) {
            Toast messageBox = Toast.makeText(this , msg.getMessage() , Toast.LENGTH_LONG);
            messageBox.show();

            Log.e("Error", msg.getMessage());
            System.out.println("Error " + msg.getMessage());

            return false;
        }
    }

    private void InformationPopUpMessage(GenerateListActivity generateListActivity) {

        AlertDialog.Builder popUpMessage = new AlertDialog.Builder(generateListActivity);
        popUpMessage.setMessage("Click on an item to edit the price and quantity")
                .setTitle("Information")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CONFIRM
                    }
                });
        popUpMessage.setIcon(R.drawable.appicon);
        popUpMessage.create();
        popUpMessage.show();

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
            Intent i = new Intent(GenerateListActivity.this, CreateListActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (id == R.id.nav_generate_list) {
            Intent i = new Intent(GenerateListActivity.this, GenerateListActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (id == R.id.nav_view_list) {
            Intent i = new Intent(GenerateListActivity.this, ViewListActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (id == R.id.nav_add_category) {
            Intent i = new Intent(GenerateListActivity.this, AddCategoryActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
