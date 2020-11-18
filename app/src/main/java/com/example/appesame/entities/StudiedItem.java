package com.example.appesame.entities;

public class StudiedItem {
    private String itemName;
    private boolean isMemorized;
    private String downloadUrl;

    public StudiedItem() {
    }

    public StudiedItem(String itemName, String downloadUrl) {
        this.itemName = itemName;
        this.isMemorized=false;
        this.downloadUrl = downloadUrl;
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

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

}
