package me.phantomxcraft;


import me.phantomxcraft.abstrak.CustomFuel;
import me.phantomxcraft.abstrak.Jetpack;
import me.phantomxcraft.kode.JetpackManager;
import me.phantomxcraft.listenevents.JetpackEvents;
import me.phantomxcraft.nms.ItemMetaData;
import me.phantomxcraft.utils.Fungsi;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static me.phantomxcraft.TapTab.ListCommand;
import static me.phantomxcraft.kode.JetpackManager.customFuelsLoaded;
import static me.phantomxcraft.kode.JetpackManager.jetpacksLoaded;
import static me.phantomxcraft.listenevents.JetpackEvents.updateLore;
import static me.phantomxcraft.utils.Fungsi.*;

public class FJetpack extends JavaPlugin implements Listener {

    public static String nmsServerVersion = "UNKNOWN";
    public static int serverVersion = 1;

    public void onEnable() {
        ConsoleCommandSender consoleCommandSender = getServer().getConsoleSender();

        try {
            nmsServerVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            serverVersion = Integer.parseInt(nmsServerVersion.split("_")[1]);
            consoleCommandSender.sendMessage(ChatColor.GOLD + "Detected Server: " + ChatColor.GREEN + Bukkit.getName() + " " + Bukkit.getVersion());
        } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
            whatVersionAreYouUsingException.printStackTrace();
            consoleCommandSender.sendMessage(ChatColor.RED + "Unknown Server: " + ChatColor.GREEN + Bukkit.getName() + " " + Bukkit.getVersion());
            consoleCommandSender.sendMessage(ChatColor.RED + "This plugin will not work because this server has unknown version!");
        }

        JetpackEvents.CekUpdate(consoleCommandSender);
        JetpackManager.reloadMe(consoleCommandSender);
        getServer().getPluginManager().registerEvents(new JetpackEvents(), this);
        PluginCommand pluginCommand = getCommand(GET_JETPACK_NAME.substring(0, 2));
        if (pluginCommand == null) {
            getLogger().log(Level.WARNING, "Failed To Set Tab Completer!");
            return;
        }
        pluginCommand.setTabCompleter(new TapTab());
    }

    public void onDisable() {
        Iterator<Player> iter = JetpackEvents.plist.iterator();
        while (iter.hasNext()) {
            Player p = iter.next();
            p.sendMessage(JetpackManager.PrefixPesan + "§cThis plugin has been unloaded!");
            JetpackEvents.HapusTasks(p);
            if (p.isOnline() && JetpackEvents.DelPFlyUnlod(p, false)) iter.remove();
        }
        JetpackManager.jetpacksLoaded.clear();
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!label.equalsIgnoreCase(GET_JETPACK_NAME) && !label.equalsIgnoreCase(GET_JETPACK_NAME.substring(0, 2)))
            return false;


        boolean notContainsCmd = true;
        if (args.length > 0)
            for (String cmd : ListCommand) {
                if (!cmd.equalsIgnoreCase(args[0])) continue;
                notContainsCmd = false;
                break;
            }


        if (args.length == 0 || args[0].equalsIgnoreCase("help") || notContainsCmd) {
            if (sender.hasPermission(PERM_STRING + "Help")) {
                InputStream is = getResource("help.txt");
                if (is == null) return true;
                Scanner s = new Scanner(is);
                while(s.hasNextLine())
                    sender.sendMessage(String.format(translateCodes(s.nextLine()), this.getDescription().getVersion()));
                s.close();
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else
                sender.sendMessage(JetpackManager.PrefixPesan + JetpackManager.TidakAdaAkses);
            return true;
        }

        if (args[0].equalsIgnoreCase(ListCommand.get(3))) {
            if (sender.hasPermission(PERM_STRING + ListCommand.get(3))) {
                JetpackManager.reloadMe(sender);
                sender.sendMessage(JetpackManager.PrefixPesan + "§aReload Config Success!");
            } else
                sender.sendMessage(JetpackManager.PrefixPesan + JetpackManager.TidakAdaAkses);
            return true;
        }

        if (args[0].equalsIgnoreCase(ListCommand.get(1)) || args[0].equalsIgnoreCase(ListCommand.get(2))) {
            if (sender.hasPermission(PERM_STRING + ListCommand.get(1)) || sender.hasPermission(PERM_STRING + ListCommand.get(2))) {

                try {
                    if (args.length == 2 || args.length == 3) {
                        Jetpack jp = jetpacksLoaded.get(args[1]);
                        if (args.length == 2 && jp == null)
                            throw new InvalidConfigurationException(String.valueOf(true));
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + "You can't run this command from Console!");
                            return true;
                        }
                        if (jp == null)
                            jp = jetpacksLoaded.get(args[2]);
                        if (jp != null) {
                            AmbilJP(sender, (Player) sender, jp, args.length == 3 ? getIntOnly(args[2], 0) : 0);
                            return true;
                        }
                    }

                    if (args.length == 4) {
                        Jetpack jetpack = JetpackManager.jetpacksLoaded.get(args[2]);
                        if (jetpack != null) {
                            try {
                                AmbilJP(sender, Bukkit.getPlayerExact(args[1]), jetpack, Integer.parseInt(args[3]));
                                return true;
                            } catch (NumberFormatException ex) {
                                sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + "Invalid fuel amount " + args[3]);
                                return true;
                            }
                        }
                    }
                } catch (Exception ignored) {
                }

                StringBuilder sB = new StringBuilder();
                for (String jetpackName : JetpackManager.jetpacksLoaded.keySet())
                    sB.append(sB.length() < 1 ? STRING_EMPTY : ", ").append(jetpackName);
                sender.sendMessage(JetpackManager.PrefixPesan + "§bUsage: §3/fj get (Player) [Jetpack] <Fuel> §b[" + sB + "]");
            } else
                sender.sendMessage(JetpackManager.PrefixPesan + JetpackManager.TidakAdaAkses);

            return true;
        }

        if (args[0].equalsIgnoreCase(ListCommand.get(4))) {
            JetpackEvents.CekUpdate(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase(ListCommand.get(0))) {
            if (!sender.hasPermission(PERM_STRING + ListCommand.get(0))) {
                sender.sendMessage(JetpackManager.PrefixPesan + JetpackManager.TidakAdaAkses);
                return true;
            }
            if (args.length == 1) {
                sender.sendMessage(JetpackManager.PrefixPesan + translateCodes("&8&l- &3/fj Set [Jetpack] <Fuel>"));
                return true;
            }
            if (sender instanceof Player) {
                Player p = (Player) sender;
                ItemStack item = getIntOnly(nmsServerVersion.split(Pattern.quote("_"))[1], 1) > 8 ? p.getInventory().getItemInMainHand() : p.getItemInHand();
                ItemMeta im = item.getItemMeta();
                if (item.getType() == Material.AIR || im == null) {
                    sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + "You not holding any item in hand.");
                    return true;
                }

                if (!ItemMetaData.isItemArmor(item)) {
                    sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + "This item is not armor item!");
                    return true;
                }

                Jetpack jetpack = JetpackManager.jetpacksLoaded.get(args[1]);
                if (jetpack == null) {
                    sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + String.format("Jetpack %s didn't exist", args[1]));
                    return true;
                }

                try {
                    item = getItemStackJetpack(sender, item, im, jetpack, args.length == 3 ? getIntOnly(args[2], 0) : 0);
                } catch (NumberFormatException ex) {
                    sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + "Invalid fuel amount!");
                    return true;
                }

                if (getIntOnly(nmsServerVersion.split(Pattern.quote("_"))[1], 1) > 11)
                    p.getInventory().setItemInMainHand(item);
                else
                    p.setItemInHand(item);

                sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.GREEN + "Success set item to jetpack " + args[1] + translateCodes(" with fuel amount &6&lx") + (args.length == 3 ? getIntOnly(args[2], 0) : 0));
                return true;
            }
            sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + "This command can run only in game as player!");
            return true;
        }

        if (args[0].equalsIgnoreCase(ListCommand.get(5))) {
            if (!sender.hasPermission(PERM_STRING + ListCommand.get(5))) {
                sender.sendMessage(JetpackManager.PrefixPesan + JetpackManager.TidakAdaAkses);
                return true;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + "This command can run only in game as player!");
                return true;
            }
            if (args.length == 1 || getIntOnly(args[1], -1) == -1) {
                sender.sendMessage(JetpackManager.PrefixPesan + translateCodes("&8&l- &3/fj SetFuel <Amount>"));
                return true;
            }
            int setVal = getIntOnly(args[1], 0);
            Player p = (Player) sender;
            ItemStack item = getIntOnly(nmsServerVersion.split(Pattern.quote("_"))[1], 1) > 8 ? p.getInventory().getItemInMainHand() : p.getItemInHand();
            ItemMeta im = item.getItemMeta();
            if (item.getType() == Material.AIR || im == null) {
                sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + "You not holding any item in hand.");
                return true;
            }

            if (!ItemMetaData.isItemArmor(item)) {
                sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + "This item is not armor item!");
                return true;
            }

            String idJP = ItemMetaData.getItemMetaDataString(item, GET_JETPACK_NAME);
            Jetpack jetpack = JetpackManager.jetpacksLoaded.get(idJP);
            if (jetpack == null) {
                sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + "This item is not Jetpack item!");
                return true;
            }

            item = updateLore(item, item.getItemMeta(), String.valueOf(setVal), jetpack);

            if (getIntOnly(nmsServerVersion.split(Pattern.quote("_"))[1], 1) > 11)
                p.getInventory().setItemInMainHand(item);
            else
                p.setItemInHand(item);

            sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.GREEN + "Success set fuel jetpack to " + setVal);
            return true;
        }

        if (args[0].equalsIgnoreCase(ListCommand.get(6)) || args[0].equalsIgnoreCase(ListCommand.get(7))) {
            if (!sender.hasPermission(PERM_STRING + ListCommand.get(6)) && !sender.hasPermission(PERM_STRING + ListCommand.get(7))) {
                sender.sendMessage(JetpackManager.PrefixPesan + JetpackManager.TidakAdaAkses);
                return true;
            }
            try {
                if (args.length == 2 || args.length == 3) {
                    CustomFuel cf = customFuelsLoaded.get(args[1]);
                    if (args.length == 2 && cf == null)
                        throw new InvalidConfigurationException(String.valueOf(true));
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + "You can't run this command from Console!");
                        return true;
                    }
                    if (cf == null)
                        cf = customFuelsLoaded.get(args[2]);
                    if (cf != null) {
                        giveCustomFuel(sender, (Player) sender, cf, args.length == 3 ? getIntOnly(args[2], 1) : 1);
                        return true;
                    }
                }

                if (args.length == 4) {
                    CustomFuel cf = customFuelsLoaded.get(args[2]);
                    if (cf != null) {
                        try {
                            giveCustomFuel(sender, Bukkit.getPlayerExact(args[1]), cf, Integer.parseInt(args[3]));
                            return true;
                        } catch (NumberFormatException ex) {
                            sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + "Invalid fuel amount " + args[3]);
                            return true;
                        }
                    }
                }
            } catch (Exception ignored) {}

            sender.sendMessage(JetpackManager.PrefixPesan + translateCodes("&8&l- &3/fj GetFuel <CustomFuel>"));
            return true;
        }

        return false;
    }

    public void giveCustomFuel(@NotNull CommandSender sender, Player p, @NotNull CustomFuel customFuel, int amount) {
        Material material;
        try {
            material = Material.valueOf(customFuel.getItem().toUpperCase());
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + "Failed to get Custom Fuel " + customFuel.getID() + " Invalid item material name: " + customFuel.getItem());
            return;
        }
        ItemStack item = new ItemStack(material);
        if (customFuel.isGlowing()) item = ItemMetaData.setItemMetaDataString(item, "ench", null);
        ItemMeta im = item.getItemMeta();
        if (im == null) {
            sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + "Failed to get Custom Fuel " + customFuel.getID());
            return;
        }
        im.setDisplayName(translateCodes(customFuel.getDisplayName()));
        customFuel.getLore().replaceAll(Fungsi::translateCodes);
        im.setLore(customFuel.getLore());

        if (customFuel.isGlowing()) {
            im.addEnchant(Enchantment.LUCK, 1, false);
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(im);

        item = ItemMetaData.setItemMetaDataString(item, GET_CUSTOM_FUEL_ID, "@" + customFuel.getID());
        item.setAmount(amount);
        p.getInventory().addItem(item);
        p.updateInventory();
        if (sender.equals(p)) {
            sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.GREEN + "Give item " + ChatColor.GOLD + customFuel.getID() + ChatColor.GREEN + translateCodes(String.format(" item &6x%s&r &ato your self success!", amount)));
            return;
        }
        sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.GREEN + "Give item " + ChatColor.GOLD + customFuel.getID() + ChatColor.GREEN + translateCodes(String.format(" item &6x%s&r &ato player ", amount)) + ChatColor.YELLOW + p.getDisplayName() + ChatColor.GREEN + " success!");
        p.sendMessage(JetpackManager.PrefixPesan + ChatColor.GREEN + "You have been given a " + ChatColor.GOLD + customFuel.getID() + ChatColor.GREEN + " item from " + sender.getName());
    }

    public ItemStack getItemStackJetpack(@NotNull CommandSender sender, ItemStack item, @NotNull ItemMeta im, @NotNull Jetpack jetpack, int fuel) {
        String fd = jetpack.getFuel();
        if (jetpack.getFuel().startsWith("@")) {
            CustomFuel customFuel = customFuelsLoaded.get(jetpack.getFuel().substring(1));
            if (customFuel == null) {
                fd += " " + ChatColor.RED + "Invalid";
                sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + "No Custom Fuel with ID: " + jetpack.getFuel().substring(1));
            } else
                fd = translateCodes(customFuel.getCustomDisplay().length() < 1 ? customFuel.getDisplayName() : customFuel.getCustomDisplay());
        } else
            fd = fd.replace("_", " ");
        im.setDisplayName(translateCodes(jetpack.getDisplayName()));
        List<String> newLore = new ArrayList<>();
        for (String lore : jetpack.getLore())
            newLore.add(translateCodes(lore
                    .replace(JETPACK_FUEL_VAR, fd)
                    .replace(JETPACK_FUEL_ITEM_VAR, String.valueOf(fuel == -1 ? jetpack.getFuelAmout() : fuel))));
        im.setLore(newLore);

        if (jetpack.getFlags().size() != 0)
            for (String itemflag : jetpack.getFlags())
                try {
                    im.addItemFlags(ItemFlag.valueOf(itemflag));
                } catch (Exception ignored) {
                    sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + "Invalid flag " + itemflag);
                }

        if (nmsServerVersion.startsWith("v1_17_") || nmsServerVersion.startsWith("v1_18_"))
            im.setUnbreakable(jetpack.isUnbreakable());

        item.setItemMeta(im);

        item = ItemMetaData.setItemMetaDataString(item, GET_JETPACK_NAME, jetpack.getName());
        item = ItemMetaData.setItemMetaDataString(item, GET_JETPACK_FUEL, String.valueOf(0));

        enchantItem(sender, item, jetpack.getEnchantments());
        return ItemMetaData.setItemMetaDataString(item, GET_JETPACK_FUEL, String.valueOf(fuel == -1 ? jetpack.getFuelAmout() : fuel));
    }

    public static void enchantItem(@NotNull CommandSender sender, ItemStack item, List<String> enchantments) {
        if (enchantments.size() != 0)
            for (String enchant : enchantments)
                try {
                    String enchantname = enchant.split(":")[0];
                    int enchantlvl = getIntOnly(enchant.split(":")[1], 1);
                    Enchantment enchantment = getIntOnly(nmsServerVersion.split(Pattern.quote("_"))[1], 1) > 16 ? Enchantment.getByKey(NamespacedKey.minecraft(enchantname.toLowerCase())) : Enchantment.getByName(enchantname.toUpperCase());
                    if (enchantment == null) continue;
                    item.addUnsafeEnchantment(enchantment, enchantlvl);
                } catch (Exception ignored) {
                    sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + "Invalid enchant " + enchant);
                }
    }

    public void AmbilJP(@NotNull CommandSender sender, Player p, @NotNull Jetpack jetpack, int fuel) {
        try {
            ItemStack item = new ItemStack(Material.valueOf(jetpack.getJetpackItem().toUpperCase()));
            ItemMeta im = item.getItemMeta();
            if (im == null) {
                sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + "Give item " + ChatColor.GOLD + jetpack.getName() + ChatColor.GREEN + " Jetpack Failed!");
                return;
            }
            item = getItemStackJetpack(sender, item, im, jetpack, fuel);
            p.getInventory().addItem(item);
            p.updateInventory();
            if (sender.equals(p)) {
                sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.GREEN + "Give item " + ChatColor.GOLD + jetpack.getName() + ChatColor.GREEN + translateCodes(String.format(" Jetpack with fuel &6&lx%s&r &ato your self success!", fuel)));
                return;
            }
            sender.sendMessage(JetpackManager.PrefixPesan + ChatColor.GREEN + "Give item " + ChatColor.GOLD + jetpack.getName() + ChatColor.GREEN + translateCodes(String.format(" Jetpack with fuel &6&lx%s&r &ato player ", fuel)) + ChatColor.YELLOW + p.getDisplayName() + ChatColor.GREEN + " success!");
            p.sendMessage(JetpackManager.PrefixPesan + ChatColor.GREEN + "You have been given a " + ChatColor.GOLD + jetpack.getName() + ChatColor.GREEN + translateCodes(String.format(" Jetpack with fuel &6&lx%s&r &afrom ", fuel)) + sender.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
