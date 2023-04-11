package com.finalyear.mobiletracking.model;

public class DashboardMenuModel {
    private String menuName;
    private int icon;
    private int pos;

    public DashboardMenuModel(String menuName, int icon, int pos) {
        this.menuName = menuName;
        this.icon = icon;
        this.pos = pos;
    }

    public String getMenuName() {
        return menuName;
    }

    public int getIcon() {
        return icon;
    }

    public int getPos() {
        return pos;
    }
}
