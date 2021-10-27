package me.phantomxcraft.kode;

import me.phantomxcraft.jetpack.Jetpack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import static me.phantomxcraft.FJetpack.nmsServerVersion;
import static me.phantomxcraft.utils.Fungsi.*;

public class JetpackManager {
    public static Integer versiConfig = 14;

    public static String PrefixPesan = ChatColor.translateAlternateColorCodes(AND_SYMBOL, "&7&l[&3&lFJetpack&7&l]&r ") ;
    public static String PesanJetpackAktif = null;
    public static String PesanJetpackMati = null;
    public static String TidakAdaAkses = null;
    public static String Jetpackdilepas = null;
    public static String BahanBakarHabis = null;
    public static String TidakAdaBensin = null;
    public static String DuniaDiBlokir = null;
    public static Boolean UpdateNotification = false;

    public static Map<String, Jetpack> jetpacksLoaded = new HashMap<>();


    public static Server getServer() {
        return Bukkit.getServer();
    }
    public static FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }
    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("FJetpack");
    }
    public static Logger getLogger() {
        return Bukkit.getLogger();
    }
    public static void saveBawaanConfig() {
        getPlugin().saveResource(CONFIG_FILE, true);
        getPlugin().reloadConfig();
    }

    public static void reloadConfig() {
        getPlugin().reloadConfig();
    }
    public static File getDataFolder() {
        return getPlugin().getDataFolder();
    }

    public static void setupMessagesConfig(CommandSender sender) {
        try {
            String filename = MESSAGES_FILE;
            File lang = new File(getDataFolder(), filename);
            if (!lang.exists())
                if (lang.getParentFile().mkdirs() || lang.getParentFile().exists()) getPlugin().saveResource(filename, false);


            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(lang);
            PrefixPesan = ChatColor.translateAlternateColorCodes(AND_SYMBOL, Objects.requireNonNull(msgConfig.getString("Messages.Prefix")));  //.replace("&", "§");
            PesanJetpackAktif = PrefixPesan + ChatColor.translateAlternateColorCodes(AND_SYMBOL, Objects.requireNonNull(msgConfig.getString("Messages.JetpackOn")));
            PesanJetpackMati = PrefixPesan + ChatColor.translateAlternateColorCodes(AND_SYMBOL, Objects.requireNonNull(msgConfig.getString("Messages.JetpackOff")));
            TidakAdaAkses = PrefixPesan + ChatColor.translateAlternateColorCodes(AND_SYMBOL, Objects.requireNonNull(msgConfig.getString("Messages.NoPerm")));
            Jetpackdilepas = PrefixPesan + ChatColor.translateAlternateColorCodes(AND_SYMBOL, Objects.requireNonNull(msgConfig.getString("Messages.JetpackRemoved")));
            BahanBakarHabis = PrefixPesan + ChatColor.translateAlternateColorCodes(AND_SYMBOL, Objects.requireNonNull(msgConfig.getString("Messages.NoFuel")));
            DuniaDiBlokir = PrefixPesan + Objects.requireNonNull(msgConfig.getString("Messages.WorldBlocked"));
            TidakAdaBensin = PrefixPesan + ChatColor.translateAlternateColorCodes(AND_SYMBOL, Objects.requireNonNull(msgConfig.getString("Messages.NeedFuel")));

            sender.sendMessage(PrefixPesan + ChatColor.GREEN + "Messages Config loaded.");
        } catch (Exception e) {
            sender.sendMessage(PrefixPesan + ChatColor.RED + "ERROR: Messages Config not loaded!");
            e.printStackTrace();
        }
    }

    public static void setupJetpacksConfig(CommandSender sender) {
        try {
            String filename = JETPACKS_FILE;
            File jet = new File(getDataFolder(), filename);
            if (!jet.exists())
                if (jet.getParentFile().mkdirs() || jet.getParentFile().exists()) getPlugin().saveResource(filename, false);

            FileConfiguration jetpacksConfig = YamlConfiguration.loadConfiguration(jet);

            jetpacksLoaded.clear();
            String jps = "Jetpacks";
            ConfigurationSection configurationSection = jetpacksConfig.getConfigurationSection(jps);
            if (configurationSection == null) throw new InvalidConfigurationException("Invalid Jetpacks Config!");
            Set<String> j = configurationSection.getKeys(false);
            jps += ".";
            for (String s : j) {
                if (!nmsServerVersion.startsWith("v1_8_")) {
                    try {
                        if (!Objects.requireNonNull(jetpacksConfig.getString(jps + s + ".ParticleEffect")).equalsIgnoreCase("none")) {
                            @SuppressWarnings("unused")
                            Particle Partikel = Particle.valueOf(Objects.requireNonNull(jetpacksConfig.getString(jps + s + ".ParticleEffect")).toUpperCase());
                        }
                    } catch (Exception partikelgagal) {
                        sender.sendMessage(PrefixPesan + ChatColor.YELLOW + "WARNING!: Invalid particle name! [" + s + " Jetpack] Server version doesn't support particle name!");
                        sender.sendMessage(PrefixPesan + ChatColor.YELLOW + "WARNING!: Invalid particle automatic set to Cloud");
                        jetpacksConfig.set(jps + s + ".ParticleEffect", "CLOUD");
                    }
                }
                try {
                    jetpacksLoaded.put(s, new Jetpack(
                            Objects.requireNonNull(jetpacksConfig.getString(jps + s + ".Permission")),
                            Objects.requireNonNull(jetpacksConfig.getString(jps + s + ".Fuel")),
                            Objects.requireNonNull(jetpacksConfig.getString(jps + s + ".FuelCost")),
                            Objects.requireNonNull(jetpacksConfig.getString(jps + s + ".DisplayName")),
                            s,
                            Objects.requireNonNull(jetpacksConfig.getString(jps + s + ".Speed")),
                            Objects.requireNonNull(jetpacksConfig.getString(jps + s + ".JetpackItem")),
                            Objects.requireNonNull(jetpacksConfig.getString(jps + s + ".ParticleEffect")),
                            Objects.requireNonNull(jetpacksConfig.getString(jps + s + ".ParticleAmount")),
                            Objects.requireNonNull(jetpacksConfig.getString(jps + s + ".BurnRate")),
                            Objects.requireNonNull(jetpacksConfig.getString(jps + s + ".ParticleDelay")),
                            jetpacksConfig.getStringList(jps + s + ".Lore"),
                            jetpacksConfig.getStringList(jps + s + ".Flags"),
                            jetpacksConfig.getStringList(jps + s + ".Enchantments"),
                            jetpacksConfig.getStringList(jps + s + ".WorldBlackList"),
                            jetpacksConfig.getBoolean(jps + s + ".Unbreakable")));
                    sender.sendMessage(PrefixPesan + ChatColor.GREEN + "Loaded: " + s + " Jetpack");
                } catch (Exception eror) {
                    sender.sendMessage(PrefixPesan + ChatColor.RED + "Failed Load: " + s + " Jetpack");
                    eror.printStackTrace();
                }
            }
        } catch (Exception e) {
            sender.sendMessage(PrefixPesan + ChatColor.RED + "Failed to Load: jetpacks.yml");
            e.printStackTrace();
        }
    }

    public static void reloadMe(CommandSender sender) {
        try {
            reloadConfig();

            File cf = new File(getDataFolder(), CONFIG_FILE);
            String confVer = getConfig().getString("Version");
            if (!cf.exists() || confVer == null)
                saveBawaanConfig();
            else if (getIntOnly(confVer) < versiConfig) {
                sender.sendMessage(PrefixPesan + ChatColor.YELLOW + "WARNING!: Config doesn't support!");
                File dirOldver = new File(getDataFolder(), "configs-v" + getConfig().getString("Version") + "-backup");
                if (dirOldver.mkdirs() && cf.exists() && cf.renameTo(new File(dirOldver, CONFIG_FILE)))
                    sender.sendMessage(PrefixPesan + ChatColor.YELLOW + "WARNING!: config.yml has been updated!");

                File lang = new File(getDataFolder(), MESSAGES_FILE);
                if (lang.exists() && lang.renameTo(new File(dirOldver, MESSAGES_FILE)))
                    sender.sendMessage(PrefixPesan + ChatColor.YELLOW + "WARNING!: messages.yml has been updated!");

                File jet = new File(getDataFolder(), JETPACKS_FILE);
                if (jet.exists() && jet.renameTo(new File(dirOldver, JETPACKS_FILE)))
                    sender.sendMessage(PrefixPesan + ChatColor.YELLOW + "WARNING!: jetpacks.yml has been updated!");

                saveBawaanConfig();
            }

            // Config
            UpdateNotification = getConfig().getBoolean("UpdateNotification");

            // Messages Config
            setupMessagesConfig(sender);

            // Jetpacks Config
            setupJetpacksConfig(sender);
        } catch(Exception e) {
            sender.sendMessage(PrefixPesan + ChatColor.YELLOW + "WARNING!: Could not load configuration, config was incorrect! (" + e.getMessage() + ")");
            File config = new File(getDataFolder(), CONFIG_FILE);
            if (!config.renameTo(new File(getDataFolder(), CONFIG_FILE + ".error"))) e.printStackTrace();
            saveBawaanConfig();
            reloadMe(sender);
        }
    }
}