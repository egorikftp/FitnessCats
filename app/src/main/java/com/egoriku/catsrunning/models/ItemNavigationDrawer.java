package com.egoriku.catsrunning.models;


public class ItemNavigationDrawer {
    private String itemName;
    private int imgResId;
    private String tagFragment;
    private boolean isSelected;

    private String userName;
    private String userEmail;
    private boolean showLine;


    public ItemNavigationDrawer(String userName, String userEmail) {
        this.userName = userName;
        this.userEmail = userEmail;
    }


    public ItemNavigationDrawer(String itemName, int imgResId, String tagFragment, boolean isSelected, boolean showLine) {
        this.itemName = itemName;
        this.imgResId = imgResId;
        this.tagFragment = tagFragment;
        this.isSelected = isSelected;
        this.showLine = showLine;
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

    public int getImgResId() {
        return imgResId;
    }

    public void setImgResId(int imgResId) {
        this.imgResId = imgResId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isShowLine() {
        return showLine;
    }

    public void setShowLine(boolean showLine) {
        this.showLine = showLine;
    }
}
