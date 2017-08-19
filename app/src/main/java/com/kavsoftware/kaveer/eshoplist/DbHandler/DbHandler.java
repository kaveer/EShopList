package com.kavsoftware.kaveer.eshoplist.DbHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kavsoftware.kaveer.eshoplist.Model.ItemViewModel;
import com.kavsoftware.kaveer.eshoplist.Model.ListViewModel;
import com.kavsoftware.kaveer.eshoplist.TableStructure.Tables;

/**
 * Created by kaveer on 8/19/2017.
 */

public class DbHandler extends SQLiteOpenHelper {

    public static final String databaseName = "EShopList";
    public static final int databaseVersion = 1;

    public DbHandler(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableItem;
        String createTableList;

        createTableItem =
                "CREATE TABLE "+ Tables.Item.tableName +
                        " ("
                        + Tables.Item.colItemId + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + Tables.Item.colListId + "  INT,"
                        + Tables.Item.colItemName + "  TEXT,"
                        + Tables.Item.colQuantity + "  INT,"
                        + Tables.Item.colCategory + "  TEXT,"
                        + Tables.Item.colPrice + "  REAL,"
                        + Tables.Item.colTotalPrice + "  REAL"
                        + " )";

        createTableList =
                "CREATE TABLE " + Tables.List.tableName +
                        " ("
                        + Tables.List.colListId + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + Tables.List.colListTitle + "  TEXT,"
                        + Tables.List.colDate + "  DATE DEFAULT CURRENT_DATE,"
                        + Tables.List.colStatus + "  TEXT"
                        + " )";

        db.execSQL(createTableItem);
        db.execSQL(createTableList);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + Tables.Item.tableName);
        db.execSQL("DROP TABLE IF EXISTS" + Tables.List.tableName);
        onCreate(db);
    }

    public ListViewModel GetListByListNameAndStatus(ListViewModel item){
        SQLiteDatabase db = this.getWritableDatabase();
        ListViewModel result = new ListViewModel();
        String query;

        try{
            query  = "SELECT * FROM "
                    + Tables.List.tableName  +
                    " WHERE " + Tables.List.colListTitle + " = " + "'" + item.listTitle + "'" +
                    " AND "
                    + Tables.List.colStatus + " = " + "'" + item.isActive + "'" ;

            Cursor cursor = db.rawQuery(query , null);
            if(cursor.getCount() > 0){
                cursor.moveToFirst();

                result.listId = Integer.parseInt(cursor.getString(0));
                result.listTitle =  cursor.getString(1);
                result.listDate = cursor.getString(2);
                result.isActive = cursor.getString(3);

            }
            db.close();
        }
        catch (Exception msg){
            Log.e("Error", msg.getMessage());
            System.out.println("Error " + msg.getMessage());
        }

        return  result;
    }

    public boolean SaveList(ListViewModel item){
        boolean result = true;
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Tables.List.colListTitle , item.listTitle);
            values.put(Tables.List.colDate , item.listDate);
            values.put(Tables.List.colStatus , item.isActive);


            db.insert(Tables.List.tableName , null , values);
            db.close();

            result = true;
        }
        catch (Exception msg){
            Log.e("Error", msg.getMessage());
            System.out.println("Error " + msg.getMessage());
            result = false;
        }

        return result;
    }

    public boolean SaveItem(ItemViewModel item){
        boolean result = true;
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Tables.Item.colListId , item.listId);
            values.put(Tables.Item.colItemName , item.itemName);
            values.put(Tables.Item.colQuantity , item.quantity);
            values.put(Tables.Item.colCategory , item.category);
            values.put(Tables.Item.colPrice , item.price);
            values.put(Tables.Item.colTotalPrice , item.totalPrice);


            db.insert(Tables.Item.tableName , null , values);
            db.close();

            result = true;
        }
        catch (Exception msg){
            Log.e("Error", msg.getMessage());
            System.out.println("Error " + msg.getMessage());
            result = false;
        }

        return result;
    }
}
