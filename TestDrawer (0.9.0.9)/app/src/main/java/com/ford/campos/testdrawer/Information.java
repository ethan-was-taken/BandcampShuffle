package com.ford.campos.testdrawer;

/**
 * Info used for nav drawer stuff
 */
public class Information {

    private int iconResourceId;
    private String title;
    private boolean active;

    public Information(int iconResourceId, String title, boolean active) {

        this.iconResourceId = iconResourceId;
        this.title = title;
        this.active = active;

    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public void setIconResourceId(int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
