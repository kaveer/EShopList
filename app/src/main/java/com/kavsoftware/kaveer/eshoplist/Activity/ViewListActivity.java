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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.kavsoftware.kaveer.eshoplist.DbHandler.DbHandler;
import com.kavsoftware.kaveer.eshoplist.Model.ListViewModel;
import com.kavsoftware.kaveer.eshoplist.R;

import java.util.ArrayList;

public class ViewListActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listShop;
    private ArrayList<String> arrayItems = new ArrayList<>();
    private ArrayList<ListViewModel> lists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_list);
        setTitle("View List");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            if (InitializeWidget()){
                lists = RetrieveList(ViewListActivity.this);
                PopulateListAndDisplay(lists);
            }

            listShop.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    int itemPosition     = position;
                    if (arrayItems.contains("No List")){
                        Toast messageBox = Toast.makeText(ViewListActivity.this, "No list", Toast.LENGTH_LONG);
                        messageBox.show();
                    }
                    else {
                        ListViewModel selectedList = lists.get(itemPosition);

                        Intent i = new Intent(ViewListActivity.this, ViewItemInListActivity.class);
                        i.putExtra("selectedList", selectedList);
                        startActivity(i);
                    }
                }

            });

        }catch (Exception msg){
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

    private ArrayList<ListViewModel> RetrieveList(ViewListActivity viewListActivity) {
        DbHandler DB = new DbHandler(viewListActivity);
        ArrayList<ListViewModel> result;

        result = DB.RetrieveList();

        return result;
    }


    private void PopulateListAndDisplay(ArrayList<ListViewModel> items) {
        if (items.size() == 0){
            arrayItems.add("No List");
        }else {
            for (ListViewModel list: items) {
                arrayItems.add(
                        "List Title: " + list.listTitle + "\n" +
                        "Date created: " + list.listDate
                );
            }
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayItems);
        listShop.setAdapter(adapter);
    }

    private boolean InitializeWidget() {
        try{
            listShop = (ListView) findViewById(R.id.listShopList);

            return true;
        } catch (Exception msg) {
            Toast messageBox = Toast.makeText(this , msg.getMessage() , Toast.LENGTH_LONG);
            messageBox.show();

            Log.e("Error", msg.getMessage());
            System.out.println("Error " + msg.getMessage());

            return false;
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
            Intent i = new Intent(ViewListActivity.this, CreateListActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (id == R.id.nav_generate_list) {
            Intent i = new Intent(ViewListActivity.this, GenerateListActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (id == R.id.nav_view_list) {
            Intent i = new Intent(ViewListActivity.this, ViewListActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (id == R.id.nav_add_category) {
            Intent i = new Intent(ViewListActivity.this, AddCategoryActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
