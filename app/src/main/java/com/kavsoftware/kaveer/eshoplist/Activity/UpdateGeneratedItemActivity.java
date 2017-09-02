package com.kavsoftware.kaveer.eshoplist.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kavsoftware.kaveer.eshoplist.Model.ItemViewModel;
import com.kavsoftware.kaveer.eshoplist.R;

import java.util.ArrayList;

public class UpdateGeneratedItemActivity extends AppCompatActivity {

    private EditText itemTitle;
    private EditText quantity;
    private EditText price;
    private Button cancel;
    private Button update;

    private ArrayList<ItemViewModel> items = new ArrayList<>();
    private int itemPosition = 0;
    private ItemViewModel item = new ItemViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_update_generated_item);

        try{
            if (InitializeWidget()){


                Intent i = getIntent();
                items  = (ArrayList<ItemViewModel>)i.getSerializableExtra("itemList");
                itemPosition = (int)i.getSerializableExtra("itemPosition");

                if (items.size() != 0 && itemPosition != 0){

                    item = items.get(itemPosition);

                    SetEditText(item);
                }
                else if(items.size() != 0 && itemPosition == 0){
                    Toast messageBox = Toast.makeText(this , "Add item in generated list" , Toast.LENGTH_LONG);
                    messageBox.show();
                }
                else {

                    Toast messageBox = Toast.makeText(this , "Error" , Toast.LENGTH_LONG);
                    messageBox.show();

                    Intent returnToGenerateList = new Intent(this, GenerateListActivity.class);
                    startActivity(returnToGenerateList);

                }

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(IsValid()){

                            SetItemViewModel(items, item, itemPosition);

                            Intent updatedItem = new Intent(UpdateGeneratedItemActivity.this, GenerateListActivity.class);
                            updatedItem.putExtra("updatedItem", items);
                            updatedItem.putExtra("hasUpdated", true);
                            startActivity(updatedItem);
                        }


                    }


                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent cancel = new Intent(UpdateGeneratedItemActivity.this, GenerateListActivity.class);
                        startActivity(cancel);

                    }


                });

            }


        }catch (Exception msg){
            Toast messageBox = Toast.makeText(this , msg.getMessage() , Toast.LENGTH_LONG);
            messageBox.show();

            Log.e("Error", msg.getMessage());
            System.out.println("Error " + msg.getMessage());
        }

    }

    private void SetItemViewModel(ArrayList<ItemViewModel> items, ItemViewModel item, int itemPosition) {

        item.itemName = itemTitle.getText().toString();
        item.price = Double.parseDouble(price.getText().toString());
        item.quantity = Integer.parseInt(quantity.getText().toString());

        if(itemPosition == 0)
            items.add(item);
        else
            items.set(itemPosition, item);

    }

    private boolean IsValid() {
        if(itemTitle.getText().length() == 0){
           itemTitle.setError("Enter item name");
            return false;
        }

        if(price.getText().length() == 0){
            price.setError("Enter price");
            return false;
        }

        if(quantity.getText().length() == 0){
            quantity.setError("Enter quantity");
            return false;
        }

        return true;
    }

    private void SetEditText(ItemViewModel item) {
        itemTitle.setText(item.itemName);
        price.setText(String.valueOf(item.price));
        quantity.setText(String.valueOf(item.quantity));
    }

    private boolean InitializeWidget() {
        try{
            itemTitle = (EditText) findViewById(R.id.txtItemTitleToUpdate);
            price = (EditText) findViewById(R.id.txtPriceToUpdate);
            quantity = (EditText) findViewById(R.id.txtQuantityToUpdate);

            cancel = (Button) findViewById(R.id.btnCancelToGenerateList);
            update = (Button) findViewById(R.id.btnUpdateItem);

            return true;
        } catch (Exception msg) {
            Toast messageBox = Toast.makeText(this , msg.getMessage() , Toast.LENGTH_LONG);
            messageBox.show();

            Log.e("Error", msg.getMessage());
            System.out.println("Error " + msg.getMessage());

            return false;
        }
    }
}
