package com.photo.editor.snapstudio.PhEditor.Model;

public class ToolsList {
    public String id;
    public int icon;
    public String iconname;

    public ToolsList(String id, String iconname, int icon) {
        this.id = id;
        this.icon = icon;
        this.iconname = iconname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getIconname() {
        return iconname;
    }

    public void setIconname(String iconname) {
        this.iconname = iconname;
    }
}
