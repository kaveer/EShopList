package com.kavsoftware.kaveer.eshoplist.Model;

import java.io.Serializable;

/**
 * Created by kaveer on 8/19/2017.
 */
@SuppressWarnings("serial")
public class ItemViewModel implements Serializable {
    public int itemId;
    public  int listId;
    public String itemName;
    public int quantity;
    public String category;
    public double price;
    public double totalPrice;
}
