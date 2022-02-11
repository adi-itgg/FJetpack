package me.phantomxcraft.nms;

import com.avaje.ebean.validation.NotNull;
import me.phantomxcraft.FJetpack;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

import static me.phantomxcraft.utils.Fungsi.STRING_EMPTY;

public class ItemMetaData {

    @NotNull
    public static ItemStack setItemMetaDataString(ItemStack itemStack, String key, String value) {
        try {
            Class<?> craftItem = Class.forName(String.format("org.bukkit.craftbukkit.%s.inventory.CraftItemStack", FJetpack.nmsServerVersion));
            Method method = craftItem.getMethod("asNMSCopy", ItemStack.class);
            Object cIS = method.invoke(craftItem, itemStack);
            Object nbt = cIS.getClass().getMethod(FJetpack.serverVersion > 17 ? "s" : "getTag").invoke(cIS);
            if (nbt == null) {
                nbt = Class.forName(FJetpack.serverVersion > 16 ? "net.minecraft.nbt.NBTTagCompound" : String.format("net.minecraft.server.%s.NBTTagCompound", FJetpack.nmsServerVersion));
                nbt.getClass().newInstance();
            }
            if (value == null)
                nbt.getClass().getMethod(FJetpack.serverVersion > 17 ? "r" : "remove", String.class).invoke(nbt, key);
            else
                nbt.getClass().getMethod(FJetpack.serverVersion > 17 ? "a" : "setString", String.class, String.class).invoke(nbt, key, value);
            cIS.getClass().getMethod(FJetpack.serverVersion > 17 ? "c" : "setTag", nbt.getClass()).invoke(cIS, nbt);

            Method m = craftItem.getMethod("asBukkitCopy", cIS.getClass());
            return (ItemStack) m.invoke(craftItem, cIS);
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return itemStack;
    }

    @Nonnull
    @NotNull
    public static String getItemMetaDataString(ItemStack itemStack, String key) {
        try {
            Class<?> craftItem = Class.forName(String.format("org.bukkit.craftbukkit.%s.inventory.CraftItemStack", FJetpack.nmsServerVersion));
            Method method = craftItem.getMethod("asNMSCopy", ItemStack.class);
            Object cIS = method.invoke(craftItem, itemStack);
            Object nbt = cIS.getClass().getMethod(FJetpack.serverVersion > 17 ? "s" : "getTag").invoke(cIS);
            if (nbt == null) return STRING_EMPTY;

            String r = (String) nbt.getClass().getMethod(FJetpack.serverVersion > 17 ? "l" : "getString", String.class).invoke(nbt, key);
            return r == null ? STRING_EMPTY : r;
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return STRING_EMPTY;
    }

    public static boolean isNotItemArmor(ItemStack itemStack) {
        try {
            Class<?> craftItem = Class.forName(String.format("org.bukkit.craftbukkit.%s.inventory.CraftItemStack", FJetpack.nmsServerVersion));
            Method method = craftItem.getMethod("asNMSCopy", ItemStack.class);
            Object cIS = method.invoke(craftItem, itemStack);
            Object itm = cIS.getClass().getMethod(FJetpack.serverVersion > 17 ? "c" : "getItem").invoke(cIS);
            return !itm.getClass().getName().equals(FJetpack.serverVersion > 16 ? "net.minecraft.world.item.ItemArmor" : String.format("net.minecraft.server.%s.ItemArmor", FJetpack.nmsServerVersion));
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return true;
    }

}
