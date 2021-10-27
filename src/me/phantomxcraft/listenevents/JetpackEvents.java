package me.phantomxcraft.listenevents;

import me.phantomxcraft.UpdateChecker;
import me.phantomxcraft.jetpack.Jetpack;
import me.phantomxcraft.jetpack.PlayerConfig;
import me.phantomxcraft.kode.JetpackManager;
import me.phantomxcraft.nms.ItemMetaData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

import static me.phantomxcraft.FJetpack.nmsServerVersion;
import static me.phantomxcraft.utils.Fungsi.*;

public class JetpackEvents extends JetpackManager implements Listener {
    public static ArrayList<Player> plist = new ArrayList<>();
    public static Map<UUID, PlayerConfig> playerConfigsList = new HashMap<>();

    public static void PCopot(Player p) {
        PlayerConfig playerConfig = playerConfigsList.get(p.getUniqueId());
        if (playerConfig == null || !plist.contains(p)) return;
        pesan(p, (!((LivingEntity) p).isOnGround() || p.isFlying()) ? Jetpackdilepas : PesanJetpackMati);
    }

    public static void DelPFly(Player p) {
        try {
            //p.setFallDistance(0.0F);
            HapusTasks(p);
            p.setAllowFlight(false);
            if (p.isOnline() && !p.getAllowFlight()) plist.remove(p);
            playerConfigsList.remove(p.getUniqueId());
            p.setFlying(false);
        } catch (Exception ignored) {
        }
    }

    public static @NotNull Boolean DelPFlyUnlod(Player p) {
        try {
            if (p.getAllowFlight()) {
                p.setFlying(false);
                p.setAllowFlight(false);
            }
            return true;
        } catch (Exception ignored) {}
        return false;
    }

    public static void pesan(@NotNull CommandSender p, String msg) {
        p.sendMessage(msg);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onDamagedPlayerArmor(EntityDamageEvent e) {
        try {
            Entity entity = e.getEntity();
            if (nmsServerVersion.startsWith("v1_17_") || !(entity instanceof Player)) return;
            Player p = (Player) entity;

            EntityEquipment eq = p.getEquipment();
            if (eq == null) return;
            ItemStack[] armors = eq.getArmorContents();
            if (armors.length < 1) return;
            for (ItemStack eqc : armors) {
                if (eqc == null || eqc.getType() == Material.AIR) continue;
                Jetpack jetpack = JetpackManager.jetpacksLoaded.get(ItemMetaData.getItemMetaDataString(eqc, GET_JETPACK_NAME));
                if (jetpack == null) continue;
                eqc.setDurability((short) 0);
                eq.setArmorContents(updateItemsFJP(armors, eqc));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onInventoryClose(final @NotNull InventoryCloseEvent e) {
        try {
            Player p = (Player) e.getPlayer();
            if (!plist.contains(p)) return;
            EntityEquipment eq = p.getEquipment();
            if (eq == null) return;
            if (eq.getArmorContents().length < 1) {
                if (plist.contains(p)) pesan(p, Jetpackdilepas);
                DelPFly(p);
            }
        } catch (Exception ignored) {

        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onInventoryClick(final @NotNull InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        try {
            ItemStack item = e.getCurrentItem();
            ItemStack cursorItem = e.getCursor();
            EntityEquipment eq = p.getEquipment();
            if (eq != null && eq.getArmorContents().length < 1 && plist.contains(p)) {
                if (plist.contains(p)) pesan(p, Jetpackdilepas);
                DelPFly(p);
                return;
            }
            if (item == null || cursorItem == null || cursorItem.equals(new ItemStack(Material.AIR))) return;
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta == null || itemMeta.getLore() == null) return;


            if ((e.getClick() != ClickType.LEFT && e.getClick() != ClickType.RIGHT && e.getClick() == ClickType.WINDOW_BORDER_LEFT && e.getClick() == ClickType.WINDOW_BORDER_RIGHT)/* && e.getSlotType() != InventoryType.SlotType.ARMOR && e.getRawSlot() != 6*/)
                return;

            Jetpack jetpack = JetpackManager.jetpacksLoaded.get(ItemMetaData.getItemMetaDataString(item, GET_JETPACK_NAME));
            if (jetpack == null) return;
            if (!p.hasPermission(jetpack.getPermission())) {
                pesan(p, TidakAdaAkses);
                return;
            }
            if (e.getCursor() == null || e.getCurrentItem() == null || item.getAmount() == 0) return;
            String fuelItem = jetpack.getFuel();
            Material BBM = Material.valueOf(fuelItem.toUpperCase());
            if (!e.getCursor().getType().equals(BBM)) return;

            e.setCancelled(true);

            boolean isLeftClicked = e.getClick() == ClickType.LEFT;
            int fuelAdded = isLeftClicked ? cursorItem.getAmount() : 1;

            int FuelValNow = getIntOnly(ItemMetaData.getItemMetaDataString(item, GET_JETPACK_FUEL));
            String newFuelVal = String.valueOf(FuelValNow + fuelAdded);
            item = updateLore(item, itemMeta, fuelItem, newFuelVal, jetpack);
            e.setCurrentItem(item);
            if (isLeftClicked)
                p.setItemOnCursor(new ItemStack(Material.AIR));
            else {
                cursorItem.setAmount(cursorItem.getAmount() - 1);
                p.setItemOnCursor(cursorItem);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onShift(@NotNull PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();

        try {
            EntityEquipment eq = p.getEquipment();
            if (eq == null) return;
            ItemStack[] armors = eq.getArmorContents();
            if (armors.length < 1) return;
            for (ItemStack eqc : armors) {

                if (eqc == null || eqc.getType() == Material.AIR) continue;
                ItemMeta eqm = eqc.getItemMeta();

                if (p.isSneaking() || eqm == null || !eqm.hasLore() || eqm.getLore() == null ||
                        !((LivingEntity) p).isOnGround() || p.isSneaking())
                    continue;


                Jetpack jetpack = JetpackManager.jetpacksLoaded.get(ItemMetaData.getItemMetaDataString(eqc, GET_JETPACK_NAME));
                if (jetpack == null) continue;
                // new Method
                // Cek Permis Player
                if (!e.getPlayer().hasPermission(jetpack.getPermission())) {
                    pesan(p, TidakAdaAkses);
                    return;
                }

                // Cek kalo udh On bakal di offin
                if (p.getAllowFlight() && plist.contains(p)) {
                    HapusTasks(p);
                    PCopot(p);
                    DelPFly(p);

                    eqc = ItemMetaData.setItemMetaDataString(eqc, GET_JETPACK_IS_BURNING, String.valueOf(0));
                    eq.setArmorContents(updateItemsFJP(armors, eqc));
                    return;
                }

                // Cek Dunia di block
                for (String blockWorld : jetpack.getWorldBlacklist()) {
                    if (!blockWorld.equals(p.getWorld().getName())) continue;
                    pesan(p, DuniaDiBlokir);
                    return;
                }

                // Cek bahan bakar kalo angin bakal unlimited?
                Material fuelType = Material.valueOf(jetpack.getFuel().toUpperCase());
                int fuelNow = 0;
                if (fuelType != Material.AIR) {
                    // Cek bahan bakar
                    String displayFuel = jetpack.getFuel().replace("_", " ");
                    fuelNow = getIntOnly(ItemMetaData.getItemMetaDataString(eqc, GET_JETPACK_FUEL));
                    if (fuelNow < 1) {
                        pesan(p, TidakAdaBensin.replaceAll("%fuel_item%", displayFuel));
                        return;
                    }
                }

                // Aktifkan
                BukkitTask particleTask = null;
                if (!jetpack.getParticleEffect().equalsIgnoreCase("none") && !nmsServerVersion.startsWith("v1_8_"))
                    particleTask = partikelTask(p, jetpack);
                playerConfigsList.put(p.getUniqueId(), new PlayerConfig(BakarBahanBakarTask(p, jetpack), particleTask));

                p.setAllowFlight(true);
                p.setFlySpeed(Float.parseFloat(jetpack.getSpeed()) / 10.0F);
                pesan(p, PesanJetpackAktif);
                plist.add(p);

                String burnStatus = ItemMetaData.getItemMetaDataString(eqc, GET_JETPACK_IS_BURNING);
                if (burnStatus.equals(String.valueOf(1))) {
                    fuelNow--;
                    eqc = updateLore(eqc, eqm, jetpack.getFuel(), String.valueOf(fuelNow), jetpack);
                }
                eqc = ItemMetaData.setItemMetaDataString(eqc, GET_JETPACK_IS_BURNING, String.valueOf(1));
                eq.setArmorContents(updateItemsFJP(armors, eqc));
                return;
            }
        } catch (Exception eroror) {
            eroror.printStackTrace();
        }

    }

    private ItemStack[] updateItemsFJP(ItemStack[] itemStacks, ItemStack jpItem) {
        int index = -1;
        for (ItemStack itemStack : itemStacks) {
            index++;
            try {
                if (!ItemMetaData.getItemMetaDataString(jpItem, GET_JETPACK_NAME).equalsIgnoreCase(ItemMetaData.getItemMetaDataString(itemStack, GET_JETPACK_NAME))
                        || itemStack.getType() != jpItem.getType())
                    continue;
                itemStacks[index] = jpItem;
            } catch (Exception ignored) {}
        }
        return itemStacks;
    }

    private BukkitTask BakarBahanBakarTask(Player p, @NotNull Jetpack jetpack) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                PlayerConfig playerConfig = playerConfigsList.get(p.getUniqueId());
                if (playerConfig == null || playerConfig.isForceStopTask()) {
                    cancel();
                    return;
                }
                try {
                    EntityEquipment eq = p.getEquipment();
                    if (eq == null) {
                        PCopot(p);
                        cancel();
                        return;
                    }
                    ItemStack[] armors = eq.getArmorContents();
                    if (armors.length < 1) {
                        PCopot(p);
                        cancel();
                        return;
                    }

                    for (ItemStack eqc : armors) {
                        if (eqc == null || eqc.getType() == Material.AIR) continue;
                        String jpNameItem = ItemMetaData.getItemMetaDataString(eqc, GET_JETPACK_FUEL);
                        if (jpNameItem.equalsIgnoreCase(STRING_EMPTY)) continue;
                        if (eqc.getItemMeta() == null || !p.hasPermission(jetpack.getPermission())) {
                            PCopot(p);
                            cancel();
                            return;
                        }
                        ItemMeta eqm = eqc.getItemMeta();
                        if (eqm == null || eqm.getLore() == null || !eqm.hasLore()) {
                            PCopot(p);
                            cancel();
                            return;
                        }

                        if (((LivingEntity) p).isOnGround() && !p.isFlying()) return;
                        try {
                            for (String worldBlock : jetpack.getWorldBlacklist()) {
                                if (!p.getWorld().getName().equalsIgnoreCase(worldBlock)) continue;
                                pesan(p, DuniaDiBlokir);
                                cancel();
                                return;
                            }


                            String fuelItem = jetpack.getFuel();

                            int FuelValNow = getIntOnly(ItemMetaData.getItemMetaDataString(eqc, GET_JETPACK_FUEL));
                            if (FuelValNow < 1) {
                                pesan(p, BahanBakarHabis);
                                cancel();
                                return;
                            }
                            String newFuelVal = String.valueOf(FuelValNow - getIntOnly(jetpack.getFuelAmout()));
                            eqc = updateLore(eqc, eqm, fuelItem, newFuelVal, jetpack);
                            if (p.getEquipment() == null || p.getEquipment().getArmorContents().length < 1) {
                                PCopot(p);
                                cancel();
                                return;
                            }
                            p.getEquipment().setArmorContents(updateItemsFJP(armors, eqc));
                            //eq.setChestplate(eqc);
                        } catch (Exception e) {
                            e.printStackTrace();
                            //PCopot(p);
                            cancel();
                        }
                        return;
                    }
                    PCopot(p);
                    cancel();
                } catch (Exception eroror) {
                    //eroror.printStackTrace();
                    cancel();
                }

            }

            @Override
            public synchronized void cancel() {
                try {
                    super.cancel();
                } catch (Exception ignored) {}
                try {
                    DelPFly(p);
                } catch (Exception ignored) {}
            }
        }.runTaskTimerAsynchronously(getPlugin(), Integer.parseInt(jetpack.getBurnRate()) * 20L, Integer.parseInt(jetpack.getBurnRate()) * 20L);
    }

    private ItemStack updateLore(ItemStack eqc, ItemMeta eqm, String fuelItem, String newFuelVal, Jetpack jetpack) {
        List<String> newLore = new ArrayList<>();
        for (String conflore : jetpack.getLore()) {
            conflore = translateCodes(conflore);
            conflore = conflore.replace(JETPACK_FUEL_VAR, fuelItem.replace("_", " "))
                    .replace(JETPACK_FUEL_ITEM_VAR, newFuelVal);
            newLore.add(conflore);
        }
        eqm.setLore(newLore);
        eqc.setItemMeta(eqm);
        eqc = ItemMetaData.setItemMetaDataString(eqc, GET_JETPACK_FUEL, newFuelVal);
        return eqc;
    }

    private BukkitTask partikelTask(Player p, Jetpack jetpack) {
        return new BukkitRunnable() {

            @Override
            public void run() {
                PlayerConfig playerConfig = playerConfigsList.get(p.getUniqueId());
                if (playerConfig == null || playerConfig.isForceStopTask()) {
                    cancel();
                    return;
                }
                try {
                    if (!p.getAllowFlight() || !plist.contains(p) || !p.isOnline()) {
                        cancel();
                        return;
                    }
                    if (!((LivingEntity) p).isOnGround() && p.isFlying()) {
                        float newZ = (float) (0.2D * Math.sin(Math.toRadians((p.getLocation().getYaw() + 270.0F))));
                        try {
                            Particle Partikel = Particle.valueOf(jetpack.getParticleEffect().toUpperCase());
                            p.getWorld().spawnParticle(Partikel, p.getLocation().getX() + newZ, p.getLocation().getY() + 0.8D, p.getLocation().getZ() + newZ, Integer.parseInt(jetpack.getParticleAmount()), 0.0D, -0.1D, 0.0D);
                        } catch (Exception ignored) {
                        }
                    }
                } catch (Exception er) {
                    cancel();
                }
            }

            @Override
            public synchronized void cancel() {
                try {
                    super.cancel();
                } catch (Exception ignored) {}
            }
        }.runTaskTimerAsynchronously(getPlugin(), Long.parseLong(jetpack.getParticleDelay()), Long.parseLong(jetpack.getParticleDelay()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerJoin(PlayerJoinEvent e) {
        MatiFly(e.getPlayer());

        CekUpdate(e.getPlayer());
    }

    public static void MatiFly(final Player pJ) {
        if (!plist.isEmpty()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (plist.contains(pJ)) {
                        DelPFly(pJ);
                        FlycekPL(pJ);
                    } else {
                        Iterator<Player> iter = plist.iterator();
                        while (iter.hasNext()) {
                            Player p = iter.next();
                            if (p.getUniqueId().equals(pJ.getUniqueId())) {
                                FlycekPL(p);
                                if (!p.getAllowFlight()) {
                                    if (p.isOnline()) {
                                        DelPFlyUnlod(p);
                                        iter.remove();
                                    }
                                }
                            }
                        }
                    }
                }
            }.runTaskLaterAsynchronously(getPlugin(), 20L);
        }
    }

    public  static  void FlycekPL(Player p) {
        try {
            PluginManager pm = getServer().getPluginManager();
            if (pm.isPluginEnabled("CMI")) getServer().getScheduler().callSyncMethod(getPlugin(), () -> getServer().dispatchCommand(getServer().getConsoleSender(), "cmi fly " + p.getName() + " false -s"));
        } catch (Exception ignored) {

        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerQuit(PlayerQuitEvent e) {
        Player p1 = e.getPlayer();
        HapusTasks(p1);
        MatiFly(p1);
    }

    public static void HapusTasks(Player p) {
        PlayerConfig playerConfig = playerConfigsList.get(p.getUniqueId());
        if (playerConfig == null) return;
        playerConfig.stopAll();
    }

    public static void CekUpdate(final CommandSender p) {
        if (!p.hasPermission("FJetpack.update") || !UpdateNotification) return;
        pesan(p, PrefixPesan + "§aPlease wait, Checking update...");
        try {
            new UpdateChecker(getPlugin(), 78318).getVersion(version -> {
                if (getIntOnly(getPlugin().getDescription().getVersion()) == getIntOnly(version))
                    pesan(p, PrefixPesan + "§aThere is not a new update available. You are using the latest version");
                else if (getIntOnly(getPlugin().getDescription().getVersion()) >= getIntOnly(version))
                    pesan(p, PrefixPesan + "§aThere is not a new update available. You are using the latest dev build version");else {
                    pesan(p, PrefixPesan + "§bThere is a new update available! v" + version);
                    pesan(p, PrefixPesan + "§bhttps://www.spigotmc.org/resources/fjetpack.78318/");
                }
            });
        } catch (NullPointerException ex) {
            getLogger().log(Level.WARNING, JetpackManager.PrefixPesan + ChatColor.RED + "Failed to check update plugin! (" + ex.getMessage() + ")");
            ex.printStackTrace();
        }
    }

}