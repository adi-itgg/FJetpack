package me.phantomxcraft.utils;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Fungsi {


    public static final String GET_JETPACK_NAME = "FJetpack";
    public static final String GET_JETPACK_FUEL = "FJFuel";
    public static final String GET_JETPACK_IS_BURNING = "FJBurning";

    public static final String JETPACK_FUEL_VAR = "{#fuel}";
    public static final String JETPACK_FUEL_ITEM_VAR = "{#fuel_value}";

    public static final char AND_SYMBOL = '&';

    public static final String CONFIG_FILE = "config.yml";
    public static final String MESSAGES_FILE = "messages.yml";
    public static final String JETPACKS_FILE = "jetpacks.yml";

    public static final String PERM_STRING = "fjetpack.";

    public static final String STRING_EMPTY = new StringBuilder().toString();

    public static String getString(Object o) {
        return o == null ? STRING_EMPTY : o.toString();
    }

    public static int getIntOnly(String s, int defaultValue) {
        try {
            if (s == null) return defaultValue;
            return Integer.parseInt(s.replaceAll("\\D+", STRING_EMPTY));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String translateCodes(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}
