package com.example.appesame.entities;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
public class StudiedItem {

    @PropertyName("itemName")
    public String itemName;

    @PropertyName("itemId")
    public String itemId;

    public boolean isMemorized;

    public StudiedItem() {}

    public StudiedItem(String itemId, String itemName) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.isMemorized=false;
    }

    public String getItemId() {
        return itemId;
    }
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public boolean isMemorized() {
        return isMemorized;
    }
    public void setMemorized(boolean isMemorized) {
        this.isMemorized = isMemorized;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj != null&&obj instanceof StudiedItem){
            StudiedItem item = (StudiedItem)obj;
            if (item.itemId.equals(this.itemId))
                return true;
        }
        return false;
    }
}
