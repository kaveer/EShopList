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

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public String getListTitle() {
        return listTitle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }

    public String getListDate() {
        return listDate;
    }

    public void setListDate(String listDate) {
        this.listDate = listDate;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
