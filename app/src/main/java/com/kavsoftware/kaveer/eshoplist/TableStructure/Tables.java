package com.kavsoftware.kaveer.eshoplist.TableStructure;

import android.provider.BaseColumns;

/**
 * Created by kaveer on 8/19/2017.
 */

public class Tables {
    public static abstract class Item implements BaseColumns{
        public static final String tableName = "Item";

        public static String colItemId = "ItemId";
        public static String colListId = "ListId";
        public static String colItemName ="ItemName";
        public static String colQuantity = "ItemQuantity";
        public static String colCategory = "ItemCategory";
        public static String colPrice = "UnitPrice";
        public static String colTotalPrice = "TotalPrice";

    }

    public static abstract class List implements BaseColumns{
        public static final String tableName = "List";

        public static String colListId = "ListId";
        public static String colListTitle ="ListTitle";
        public static String colDate = "ListDate";
        public static String colStatus = "ListStatus";

    }
}
