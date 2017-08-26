package com.kavsoftware.kaveer.eshoplist.Model;

import java.io.Serializable;

/**
 * Created by kaveer on 8/19/2017.
 */
@SuppressWarnings("serial")
public class ListViewModel implements Serializable {
    public  int listId;
    public String listTitle;
    public String listDate;
    public String isActive = "IsActive";
    public String isDeactivate = "IsDeactivate";

}
