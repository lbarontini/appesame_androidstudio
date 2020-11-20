package com.example.appesame.entities;

import java.util.ArrayList;

public class StudiedItem {
    private String itemName;
    private boolean isMemorized;

    public StudiedItem() {
    }
    public StudiedItem(String itemName) {
        this.itemName = itemName;
        this.isMemorized=false;
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
