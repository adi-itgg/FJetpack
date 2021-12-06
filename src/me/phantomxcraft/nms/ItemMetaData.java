package me.phantomxcraft.nms;

import com.avaje.ebean.validation.NotNull;
import me.phantomxcraft.FJetpack;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class ItemMetaData {

    public static ItemStack setItemMetaDataString(ItemStack itemStack, String key, String value) {
        switch (FJetpack.nmsServerVersion) {
//            case "v1_17_R1":
//                return (new ItemMetaData_v1_17_R1()).setItemDataString(itemStack, key, value);
            case "v1_16_R3":
                return (new ItemMetaData_v1_16_R3()).setItemDataString(itemStack, key, value);
            case "v1_16_R2":
                return (new ItemMetaData_v1_16_R2()).setItemDataString(itemStack, key, value);
            case "v1_16_R1":
                return (new ItemMetaData_v1_16_R1()).setItemDataString(itemStack, key, value);
            case "v1_15_R1":
                return (new ItemMetaData_v1_15_R1()).setItemDataString(itemStack, key, value);
            case "v1_14_R1":
                return (new ItemMetaData_v1_14_R1()).setItemDataString(itemStack, key, value);
            case "v1_13_R2":
                return (new ItemMetaData_v1_13_R2()).setItemDataString(itemStack, key, value);
            case "v1_13_R1":
                return (new ItemMetaData_v1_13_R1()).setItemDataString(itemStack, key, value);
            case "v1_12_R1":
                return (new ItemMetaData_v1_12_R1()).setItemDataString(itemStack, key, value);
            case "v1_11_R1":
                return (new ItemMetaData_v1_11_R1()).setItemDataString(itemStack, key, value);
            case "v1_10_R1":
                return (new ItemMetaData_v1_10_R1()).setItemDataString(itemStack, key, value);
            case "v1_9_R2":
                return (new ItemMetaData_v1_9_R2()).setItemDataString(itemStack, key, value);
            case "v1_9_R1":
                return (new ItemMetaData_v1_9_R1()).setItemDataString(itemStack, key, value);
            case "v1_8_R3":
                return (new ItemMetaData_v1_8_R3()).setItemDataString(itemStack, key, value);
            case "v1_8_R2":
                return (new ItemMetaData_v1_8_R2()).setItemDataString(itemStack, key, value);
            case "v1_8_R1":
                return (new ItemMetaData_v1_8_R1()).setItemDataString(itemStack, key, value);
            default:
                return (new ItemMetaData_v1_18_R1()).setItemDataString(itemStack, key, value);
        }
    }

    @Nonnull @NotNull
    public static String getItemMetaDataString(ItemStack itemStack, String key) {
        switch (FJetpack.nmsServerVersion) {
//            case "v1_17_R1":
//                return (new ItemMetaData_v1_17_R1()).getItemDataString(itemStack, key);
            case "v1_16_R3":
                return (new ItemMetaData_v1_16_R3()).getItemDataString(itemStack, key);
            case "v1_16_R2":
                return (new ItemMetaData_v1_16_R2()).getItemDataString(itemStack, key);
            case "v1_16_R1":
                return (new ItemMetaData_v1_16_R1()).getItemDataString(itemStack, key);
            case "v1_15_R1":
                return (new ItemMetaData_v1_15_R1()).getItemDataString(itemStack, key);
            case "v1_14_R1":
                return (new ItemMetaData_v1_14_R1()).getItemDataString(itemStack, key);
            case "v1_13_R2":
                return (new ItemMetaData_v1_13_R2()).getItemDataString(itemStack, key);
            case "v1_13_R1":
                return (new ItemMetaData_v1_13_R1()).getItemDataString(itemStack, key);
            case "v1_12_R1":
                return (new ItemMetaData_v1_12_R1()).getItemDataString(itemStack, key);
            case "v1_11_R1":
                return (new ItemMetaData_v1_11_R1()).getItemDataString(itemStack, key);
            case "v1_10_R1":
                return (new ItemMetaData_v1_10_R1()).getItemDataString(itemStack, key);
            case "v1_9_R2":
                return (new ItemMetaData_v1_9_R2()).getItemDataString(itemStack, key);
            case "v1_9_R1":
                return (new ItemMetaData_v1_9_R1()).getItemDataString(itemStack, key);
            case "v1_8_R3":
                return (new ItemMetaData_v1_8_R3()).getItemDataString(itemStack, key);
            case "v1_8_R2":
                return (new ItemMetaData_v1_8_R2()).getItemDataString(itemStack, key);
            case "v1_8_R1":
                return (new ItemMetaData_v1_8_R1()).getItemDataString(itemStack, key);
            default:
                return (new ItemMetaData_v1_18_R1()).getItemDataString(itemStack, key);
        }
    }

    public static boolean isItemArmor(ItemStack itemStack) {
        switch (FJetpack.nmsServerVersion) {
//            case "v1_17_R1":
//                return (new ItemMetaData_v1_17_R1()).isItemArmor(itemStack);
            case "v1_16_R3":
                return (new ItemMetaData_v1_16_R3()).isItemArmor(itemStack);
            case "v1_16_R2":
                return (new ItemMetaData_v1_16_R2()).isItemArmor(itemStack);
            case "v1_16_R1":
                return (new ItemMetaData_v1_16_R1()).isItemArmor(itemStack);
            case "v1_15_R1":
                return (new ItemMetaData_v1_15_R1()).isItemArmor(itemStack);
            case "v1_14_R1":
                return (new ItemMetaData_v1_14_R1()).isItemArmor(itemStack);
            case "v1_13_R2":
                return (new ItemMetaData_v1_13_R2()).isItemArmor(itemStack);
            case "v1_13_R1":
                return (new ItemMetaData_v1_13_R1()).isItemArmor(itemStack);
            case "v1_12_R1":
                return (new ItemMetaData_v1_12_R1()).isItemArmor(itemStack);
            case "v1_11_R1":
                return (new ItemMetaData_v1_11_R1()).isItemArmor(itemStack);
            case "v1_10_R1":
                return (new ItemMetaData_v1_10_R1()).isItemArmor(itemStack);
            case "v1_9_R2":
                return (new ItemMetaData_v1_9_R2()).isItemArmor(itemStack);
            case "v1_9_R1":
                return (new ItemMetaData_v1_9_R1()).isItemArmor(itemStack);
            case "v1_8_R3":
                return (new ItemMetaData_v1_8_R3()).isItemArmor(itemStack);
            case "v1_8_R2":
                return (new ItemMetaData_v1_8_R2()).isItemArmor(itemStack);
            case "v1_8_R1":
                return (new ItemMetaData_v1_8_R1()).isItemArmor(itemStack);
            default:
                return (new ItemMetaData_v1_18_R1()).isItemArmor(itemStack);
        }
    }

}
