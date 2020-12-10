package com.example.appesame.entities;

import java.util.ArrayList;

public class StudiedItem {
    private String itemName;
    private String itemId;
    private boolean isMemorized;

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
}
