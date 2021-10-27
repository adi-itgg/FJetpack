package me.phantomxcraft.utils;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Fungsi {

//    public static final String GET_JETPACK_DISPLAYNAME = "FJDisplay";
//    public static final String GET_JETPACK_LORE = "FJLore";
//    public static final String GET_JETPACK_ITEM = "FJItem";
    public static final String GET_JETPACK_NAME = "FJetpack";
//    public static final String GET_JETPACK_PERMISSION = "FPerm";
//    public static final String GET_JETPACK_FUEL_ITEM = "FJFuelItem";
    public static final String GET_JETPACK_FUEL = "FJFuel";
//    public static final String GET_JETPACK_FUEL_BURN_AMOUT = "FJFuelBurnAmount";
//    public static final String GET_JETPACK_BURN_RATE = "FJBurnRate";
//    public static final String GET_JETPACK_SPEED = "FJSpeed";
//    public static final String GET_JETPACK_PARTICLE_ITEM = "FJParticleItem";
//    public static final String GET_JETPACK_PARTICLE_AMOUNT = "FJParticleAmount";
//    public static final String GET_JETPACK_PARTICLE_DELAY = "FJParticleDelay";
//    public static final String GET_JETPACK_BLOCKED_WORLD = "FJBlockedWorld";
    public static final String GET_JETPACK_IS_BURNING = "FJBurning";

    public static final String JETPACK_FUEL_VAR = "{#fuel}";
    public static final String JETPACK_FUEL_ITEM_VAR = "{#fuel_value}";

    public static final char AND_SYMBOL = '&';

    public static final String CONFIG_FILE = "config.yml";
    public static final String MESSAGES_FILE = "messages.yml";
    public static final String JETPACKS_FILE = "jetpacks.yml";

    public static final String STRING_EMPTY = new StringBuilder().toString();

    public static String getString(Object o) {
        return o == null ? STRING_EMPTY : o.toString();
    }

    public static int getIntOnly(String s) {
        try {
            if (s == null) return 0;
            return Integer.parseInt(s.replaceAll("\\D+", STRING_EMPTY));
        } catch (Exception e) {
            return 0;
        }
    }

    public static String p(@NotNull String s, String sp, int index) throws PatternSyntaxException, ArrayIndexOutOfBoundsException {
        return s.split(Pattern.quote(sp))[index];
    }

    @Deprecated
    @Nullable
    public static String getItemUUID(String lore) {
        try {
            return p(p(lore, "[id~", 1), "~id]", 0);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static String getGenUID(String uid) {
        return translateCodes("&0") + "[id~" + uid + "~id]";
    }

    public static String translateCodes(String s) {
        return ChatColor.translateAlternateColorCodes('&', s); // .replace("&", "§")
    }

}
