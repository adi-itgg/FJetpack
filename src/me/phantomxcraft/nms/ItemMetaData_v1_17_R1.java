package me.phantomxcraft.nms;

//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.world.item.ItemArmor;
//import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
//import org.bukkit.inventory.ItemStack;
//
//import static me.phantomxcraft.utils.Fungsi.STRING_EMPTY;
//
//public class ItemMetaData_v1_17_R1 {
//
//    public ItemStack setItemDataString(ItemStack itemStack, String key, String value) {
//        net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(itemStack);
//        NBTTagCompound nbtTagCompound = item.getTag();
//        if (nbtTagCompound == null) nbtTagCompound = new NBTTagCompound();
//        if (value == null)
//            nbtTagCompound.remove(key);
//        else
//            nbtTagCompound.setString(key, value);
//        return CraftItemStack.asBukkitCopy(item);
//    }
//
//    public String getItemDataString(ItemStack itemStack, String key) {
//        net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(itemStack);
//        NBTTagCompound nbtTagCompound = item.getTag();
//        if (nbtTagCompound == null) return STRING_EMPTY;
//        String r = nbtTagCompound.getString(key);
//        return r == null ? STRING_EMPTY : r;
//    }
//
//    public boolean isItemArmor(ItemStack itemStack) {
//        return CraftItemStack.asNMSCopy(itemStack).getItem() instanceof ItemArmor;
//    }
//
//}
