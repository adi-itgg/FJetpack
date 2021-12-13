package me.phantomxcraft.kode;

import me.phantomxcraft.abstrak.CustomFuel;
import me.phantomxcraft.abstrak.Jetpack;
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
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

import static me.phantomxcraft.FJetpack.nmsServerVersion;
import static me.phantomxcraft.utils.Fungsi.*;

public class JetpackManager {
    public static Integer versiConfig = -1;

    public static String PrefixPesan = translateCodes("&7&l[&3&lFJetpack&7&l]&r ");
    public static String PesanJetpackAktif = null;
    public static String PesanJetpackMati = null;
    public static String TidakAdaAkses = null;
    public static String Jetpackdilepas = null;
    public static String BahanBakarHabis = null;
    public static String TidakAdaBensin = null;
    public static String DuniaDiBlokir = null;
    public static String OnEmptyFuelDropped, OnEmptyFuelRemoved, OnDeathDropped, OnDeathRemoved;
    public static Boolean UpdateNotification = false;

    public static Map<String, Jetpack> jetpacksLoaded = new HashMap<>();
    public static Map<String, CustomFuel> customFuelsLoaded = new HashMap<>();

    public static Server getServer() {
        return Bukkit.getServer();
    }
    public static FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }
    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin(GET_JETPACK_NAME);
    }
    public static Logger getLogger() {
        return Bukkit.getLogger();
    }

    public static void saveBawaanConfig() {
        getPlugin().saveResource(CONFIG_FILE, true);
        getPlugin().saveResource(MESSAGES_FILE, true);
        getPlugin().saveResource(JETPACKS_FILE, true);
        getPlugin().saveResource(CUSTOM_FUELS_FILE, true);
        getPlugin().reloadConfig();
    }

    public static void reloadConfig() {
        getPlugin().reloadConfig();
    }
    public static File getDataFolder() {
        return getPlugin().getDataFolder();
    }

    public static void setupMessagesConfig(CommandSender sender) {
        String filename = MESSAGES_FILE;
        File lang = new File(getDataFolder(), filename);
        try {
            if (!lang.exists())
                if (lang.getParentFile().mkdirs() || lang.getParentFile().exists()) getPlugin().saveResource(filename, false);

            String m = "Messages.";
            FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(lang);
            PrefixPesan = translateCodes(Objects.requireNonNull(msgConfig.getString(m + "Prefix")));  //.replace("&", "§");
            PesanJetpackAktif = PrefixPesan + translateCodes(Objects.requireNonNull(msgConfig.getString(m + "JetpackOn")));
            PesanJetpackMati = PrefixPesan + translateCodes(Objects.requireNonNull(msgConfig.getString(m + "JetpackOff")));
            TidakAdaAkses = PrefixPesan + translateCodes(Objects.requireNonNull(msgConfig.getString(m + "NoPerm")));
            Jetpackdilepas = PrefixPesan + translateCodes(Objects.requireNonNull(msgConfig.getString(m + "JetpackRemoved")));
            BahanBakarHabis = PrefixPesan + translateCodes(Objects.requireNonNull(msgConfig.getString(m + "NoFuel")));
            DuniaDiBlokir = PrefixPesan + Objects.requireNonNull(msgConfig.getString(m + "WorldBlocked"));
            TidakAdaBensin = PrefixPesan + translateCodes(Objects.requireNonNull(msgConfig.getString(m + "NeedFuel")));
            OnEmptyFuelDropped = PrefixPesan + translateCodes(Objects.requireNonNull(msgConfig.getString(m + "OnEmptyFuelDropped")));
            OnEmptyFuelRemoved = PrefixPesan + translateCodes(Objects.requireNonNull(msgConfig.getString(m + "OnEmptyFuelRemoved")));
            OnDeathDropped = PrefixPesan + translateCodes(Objects.requireNonNull(msgConfig.getString(m + "OnDeathDropped")));
            OnDeathRemoved = PrefixPesan + translateCodes(Objects.requireNonNull(msgConfig.getString(m + "OnDeathRemoved")));

            sender.sendMessage(PrefixPesan + ChatColor.GREEN + "Messages Config loaded.");
        } catch (Exception e) {
            if (lang.renameTo(new File(getDataFolder(), filename + ".error")))
                if (lang.getParentFile().mkdirs() || lang.getParentFile().exists()) getPlugin().saveResource(filename, false);
            sender.sendMessage(PrefixPesan + ChatColor.RED + "ERROR: Messages Config not loaded!");
            e.printStackTrace();
        }
    }

    public static void setupJetpacksConfig(CommandSender sender) {
        String filename = JETPACKS_FILE;
        File jet = new File(getDataFolder(), filename);
        try {
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
                            jetpacksConfig.getString(jps + s + ".Permission", "fjetpack." + s),
                            Objects.requireNonNull(jetpacksConfig.getString(jps + s + ".Fuel")),
                            jetpacksConfig.getInt(jps + s + ".FuelCost", 1),
                            Objects.requireNonNull(jetpacksConfig.getString(jps + s + ".DisplayName")),
                            s,
                            Objects.requireNonNull(jetpacksConfig.getString(jps + s + ".Speed")),
                            Objects.requireNonNull(jetpacksConfig.getString(jps + s + ".JetpackItem")),
                            Objects.requireNonNull(jetpacksConfig.getString(jps + s + ".ParticleEffect")),
                            jetpacksConfig.getInt(jps + s + ".ParticleAmount", 0),
                            jetpacksConfig.getInt(jps + s + ".BurnRate", 5),
                            Objects.requireNonNull(jetpacksConfig.getString(jps + s + ".ParticleDelay")),
                            jetpacksConfig.getStringList(jps + s + ".Lore"),
                            jetpacksConfig.getStringList(jps + s + ".Flags"),
                            jetpacksConfig.getStringList(jps + s + ".Enchantments"),
                            jetpacksConfig.getStringList(jps + s + ".WorldBlackList"),
                            jetpacksConfig.getBoolean(jps + s + ".Unbreakable", false),
                            jetpacksConfig.getString(jps + s + ".OnEmptyFuel", "None"),
                            jetpacksConfig.getString(jps + s + ".OnDeath", "None")
                            ));
                    sender.sendMessage(PrefixPesan + ChatColor.GREEN + "Loaded: " + s + " Jetpack");
                } catch (Exception eror) {
                    eror.printStackTrace();
                    sender.sendMessage(PrefixPesan + ChatColor.RED + "Failed Load: " + s + " Jetpack");
                }
            }
        } catch (Exception e) {
            if (jet.renameTo(new File(getDataFolder(), filename + ".error")))
                if (jet.getParentFile().mkdirs() || jet.getParentFile().exists()) getPlugin().saveResource(filename, false);
            sender.sendMessage(PrefixPesan + ChatColor.RED + "Failed to Load: jetpacks.yml");
            e.printStackTrace();
        }
    }

    public static void reloadMe(CommandSender sender) {
        try {
            if (versiConfig == -1) {
                InputStream is = getPlugin().getResource(CONFIG_FILE);
                if (is != null) {
                    StringBuilder defCf = new StringBuilder();
                    Scanner s = new Scanner(is);
                    while (s.hasNextLine())
                        defCf.append(s.nextLine());
                    s.close();
                    is.close();
                    versiConfig = getIntOnly(defCf.toString(), 0);
                }
            }
            reloadConfig();

            File cf = new File(getDataFolder(), CONFIG_FILE);
            String confVer = getConfig().getString("Version");
            if (!cf.exists() || confVer == null)
                saveBawaanConfig();
            else if (getIntOnly(confVer, 1) < versiConfig) {
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

            // Fuels Config
            loadCustomFuels(sender);

        } catch(Exception e) {
            sender.sendMessage(PrefixPesan + ChatColor.YELLOW + "WARNING!: Could not load configuration, config was incorrect! (" + e.getMessage() + ")");
            File config = new File(getDataFolder(), CONFIG_FILE);
            if (!config.renameTo(new File(getDataFolder(), CONFIG_FILE + ".error"))) e.printStackTrace();
            saveBawaanConfig();
            reloadMe(sender);
        }
    }

    public static void loadCustomFuels(CommandSender sender) {
        String filename = CUSTOM_FUELS_FILE;
        File fuelf = new File(getDataFolder(), filename);
        try {
            if (!fuelf.exists())
                if (fuelf.getParentFile().mkdirs() || fuelf.getParentFile().exists()) getPlugin().saveResource(filename, false);

            FileConfiguration fuelsConfig = YamlConfiguration.loadConfiguration(fuelf);

            String fuels = "CustomFuels";
            ConfigurationSection configurationSection = fuelsConfig.getConfigurationSection(fuels);
            if (configurationSection == null) throw new InvalidConfigurationException("Invalid Fuels Config!");
            Set<String> j = configurationSection.getKeys(false);
            fuels += ".";
            for (String s : j) {
                try {
                    customFuelsLoaded.put(s,
                            new CustomFuel(s,
                                    fuelsConfig.getString(fuels + s + ".CustomDisplay"),
                                    fuelsConfig.getString(fuels + s + ".DisplayName"),
                                    fuelsConfig.getString(fuels + s + ".Item"),
                                    fuelsConfig.getString(fuels + s + ".Permission"),
                                    fuelsConfig.getStringList(fuels + s + ".Lore"),
                                    fuelsConfig.getBoolean(fuels + s + ".Glowing")
                            ));
                    sender.sendMessage(PrefixPesan + ChatColor.GREEN + "Loaded: " + s + " Custom Fuel");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    sender.sendMessage(PrefixPesan + ChatColor.RED + "Failed Load: " + s + " Custom Fuel");
                }
            }
        } catch (Exception ex) {
            if (fuelf.renameTo(new File(getDataFolder(), filename + ".error")))
                if (fuelf.getParentFile().mkdirs() || fuelf.getParentFile().exists()) getPlugin().saveResource(filename, false);
            ex.printStackTrace();
            sender.sendMessage(PrefixPesan + ChatColor.RED + "ERROR: CustomFuels Config not loaded!");
        }
    }
}