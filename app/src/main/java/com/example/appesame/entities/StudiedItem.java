package com.example.appesame.entities;

import androidx.annotation.Nullable;

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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof StudiedItem) {
            if (((StudiedItem) obj).itemId.equals(this.itemId))
                return true;
        }
        return false;
    }
}
