package me.phantomxcraft.abstrak;

import java.util.List;

public class CustomFuel {
    private final String ID;
    private final String CustomDisplay;
    private final String DisplayName;
    private String Item;
    private final String Permission;
    private final List<String> Lore;
    private final boolean Glowing;

    public CustomFuel(String id, String customDisplay, String displayName, String item, String permission, List<String> lore, boolean glowing) {
        ID = id;
        CustomDisplay = customDisplay;
        DisplayName = displayName;
        Item = item;
        Permission = permission;
        Lore = lore;
        Glowing = glowing;
    }

    public String getID() {
        return ID;
    }

    public String getCustomDisplay() {
        return CustomDisplay;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public String getItem() {
        return Item;
    }

    public void setItem(String item) {
        Item = item;
    }

    public String getPermission() {
        return Permission;
    }

    public List<String> getLore() {
        return Lore;
    }

    public boolean isGlowing() {
        return Glowing;
    }

}
