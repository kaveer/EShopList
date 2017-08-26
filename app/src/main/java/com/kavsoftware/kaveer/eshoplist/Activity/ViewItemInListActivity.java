package com.kavsoftware.kaveer.eshoplist.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.kavsoftware.kaveer.eshoplist.DbHandler.DbHandler;
import com.kavsoftware.kaveer.eshoplist.Model.ItemViewModel;
import com.kavsoftware.kaveer.eshoplist.Model.ListViewModel;
import com.kavsoftware.kaveer.eshoplist.R;

import java.util.ArrayList;

public class ViewItemInListActivity extends AppCompatActivity {

    private ArrayList<ItemViewModel> itemsFromList = new ArrayList<>();
    private ArrayList<String> arrayItems = new ArrayList<>();
    private ListView itemsListVIew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_view_item_in_list);
        setTitle("View item");

        Intent i = getIntent();
        ListViewModel selectedList = (ListViewModel) i.getSerializableExtra("selectedList");

        try{
            if (initializeWidget()){
                if (selectedList.listId != 0 && !selectedList.listTitle.isEmpty()){

                    setTitle("View list " + selectedList.listTitle);
                    itemsFromList = RetrieveItems(selectedList.listId, ViewItemInListActivity.this);

                    if (itemsFromList.size() > 0){
                        PopulateListView();
                    }else {
                        PopulateListView();

                        Toast messageBox = Toast.makeText(this , "No item in this list" , Toast.LENGTH_LONG);
                        messageBox.show();
                    }

                }
            }
        }catch (Exception msg){
            Toast messageBox = Toast.makeText(this , msg.getMessage() , Toast.LENGTH_LONG);
            messageBox.show();

            Log.e("Error", msg.getMessage());
            System.out.println("Error " + msg.getMessage());
        }

    }

    private void PopulateListView() {
        if (itemsFromList.size() == 0){
            arrayItems.add("No item");
        }else {
            for (ItemViewModel item: itemsFromList) {
                arrayItems.add(
                            "Item: " + item.itemName +  "\n" +
                            "Category: " + item.category  + "\n" +
                            "Qty: " + item.quantity + "\n" +
                            "Price: " + item.price + "\n" +
                            "Total price: " + item.totalPrice
                );
            }
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayItems);
        itemsListVIew.setAdapter(adapter);
    }

    private boolean initializeWidget() {
        try{
            itemsListVIew = (ListView) findViewById(R.id.itemsViewList);

            return true;
        } catch (Exception msg) {
            Toast messageBox = Toast.makeText(this , msg.getMessage() , Toast.LENGTH_LONG);
            messageBox.show();

            Log.e("Error", msg.getMessage());
            System.out.println("Error " + msg.getMessage());

            return false;
        }
    }

    private ArrayList<ItemViewModel> RetrieveItems(int listId, ViewItemInListActivity viewItemInListActivity) {
        DbHandler DB = new DbHandler(viewItemInListActivity);
        ArrayList<ItemViewModel> result;

        result = DB.RetrieveItemsByListId(listId);

        return result;
    }


}
