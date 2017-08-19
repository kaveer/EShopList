package com.kavsoftware.kaveer.eshoplist.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.kavsoftware.kaveer.eshoplist.Model.ItemViewModel;
import com.kavsoftware.kaveer.eshoplist.Model.ListViewModel;
import com.kavsoftware.kaveer.eshoplist.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateListActivity extends AppCompatActivity {

    ItemViewModel itemModel = new ItemViewModel();
    ListViewModel listModel = new ListViewModel();
    private   ArrayList<ItemViewModel> arrayItemsModel = new ArrayList<>();

    private EditText date;
    private EditText listTitle;
    private EditText item;
    private EditText price;
    private Spinner category;
    private ListView items;
    private Button addItem;
    private EditText quantity;
    private  Button saveList;
    private   ArrayList<String> arrayItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_list);
        setTitle("Create List");

        try{
            if (InitializeWidget()){
                SetDate();
                DisplayItemInListView("no item added");

                addItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (IsItemValid()){
                            SetItemModel();
                            String displayItem = itemModel.itemName + "     " + itemModel.quantity + "      " + itemModel.price + "     " + itemModel.totalPrice;
                            if(DisplayItemInListView(displayItem)){
                                arrayItemsModel.add(itemModel);
                            }
                        }
                    }
                });

                saveList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SetListModel();
                        if (SaveListDetails(listModel)){
                           if (SaveItem(arrayItemsModel, listModel.listId)){
                               Toast messageBox = Toast.makeText(CreateListActivity.this, "List Saved ", Toast.LENGTH_LONG);
                               messageBox.show();
                           }

                            Toast messageBox = Toast.makeText(CreateListActivity.this, "Unable to save list ", Toast.LENGTH_LONG);
                            messageBox.show();
                        }
                        else {
                            Toast messageBox = Toast.makeText(CreateListActivity.this, "List already exist", Toast.LENGTH_LONG);
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
    }

    private boolean SaveItem(ArrayList<ItemViewModel> arrayItemsModel, int listId) {
        return true;
    }

    private boolean SaveListDetails(ListViewModel listModel) {
        //save list
        //get list id
        return  true;
    }

    private void SetListModel() {
        listModel.listTitle = listTitle.getText().toString();
        listModel.isActive = true;
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
}
