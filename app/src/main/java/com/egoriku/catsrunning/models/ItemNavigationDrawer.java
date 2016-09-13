package com.egoriku.catsrunning.models;

import android.graphics.drawable.Drawable;

public class ItemNavigationDrawer {
    private String itemName;
    private Drawable imgRes;
    private String tagFragment;

    public ItemNavigationDrawer(String itemName, Drawable imgRes, String tagFragment) {
        this.itemName = itemName;
        this.imgRes = imgRes;
        this.tagFragment = tagFragment;
    }

    public String getTagFragment() {
        return tagFragment;
    }

    public void setTagFragment(String tagFragment) {
        this.tagFragment = tagFragment;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Drawable getImgRes() {
        return imgRes;
    }

    public void setImgRes(Drawable imgRes) {
        this.imgRes = imgRes;
    }
}
