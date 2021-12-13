package me.phantomxcraft.listenevents;

import me.phantomxcraft.UpdateChecker;
import me.phantomxcraft.abstrak.CustomFuel;
import me.phantomxcraft.abstrak.Jetpack;
import me.phantomxcraft.abstrak.PlayerConfig;
import me.phantomxcraft.kode.JetpackManager;
import me.phantomxcraft.nms.ItemMetaData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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
    public static Map<UUID, Boolean> playerMsged = new HashMap<>();

    public static void PCopot(Player p) {
        PlayerConfig playerConfig = playerConfigsList.get(p.getUniqueId());
        if (playerConfig == null || !plist.contains(p)) return;
        Jetpack jp = jetpacksLoaded.get(playerConfig.getJetpackID());
        if (jp != null && !jp.getOnDeath().equalsIgnoreCase("none") && !jp.getOnEmptyFuel().equalsIgnoreCase("none"))
            return;
        if (playerConfig.isDied()) return;
        pesan(p, (!((LivingEntity) p).isOnGround() || p.isFlying()) ? Jetpackdilepas : PesanJetpackMati);
    }

    public static void DelPFly(Player p) {
        //p.setFallDistance(0.0F);
        HapusTasks(p);
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    p.setAllowFlight(false);
                    if (p.isOnline() && !p.getAllowFlight()) plist.remove(p);
                    playerConfigsList.remove(p.getUniqueId());
                    p.setFlying(false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.runTask(getPlugin());
    }

    public static boolean DelPFlyUnlod(Player p, boolean isAsync) {
        try {
            if (!isAsync) {
                if (p.getAllowFlight()) {
                    p.setFlying(false);
                    p.setAllowFlight(false);
                }
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (p.getAllowFlight()) {
                        p.setFlying(false);
                        p.setAllowFlight(false);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.runTask(getPlugin());
        return true;
    }

    public static void pesan(@NotNull CommandSender p, String msg) {
        p.sendMessage(msg);
    }

    private void onJetpackPlayerListener(@NotNull Player p, @NotNull ItemStack jpItem, boolean onEmptyListener, @NotNull String listener) {
        EntityEquipment eq = p.getEquipment();
        boolean dropped = false;
        if (eq != null) {
            ItemStack[] items = eq.getArmorContents();
            for (int i = 0; i < items.length; i++) {
                if (items[i] == null || !ItemMetaData.getItemMetaDataString(items[i], GET_JETPACK_NAME).equals(ItemMetaData.getItemMetaDataString(jpItem, GET_JETPACK_NAME)))
                    continue;
                if (listener.equalsIgnoreCase("drop")) {
                    dropped = true;
                    ItemStack itemDrop = items[i].clone();
                    getServer().getScheduler().runTask(getPlugin(), () -> p.getWorld().dropItemNaturally(p.getLocation(), itemDrop));
                }
                if (!listener.equalsIgnoreCase("none")) items[i] = null;
            }
            if (!listener.equalsIgnoreCase("none")) eq.setArmorContents(items);
        }
        if (!dropped && listener.equalsIgnoreCase("drop")) {
            ItemStack itemDrop = jpItem.clone();
            getServer().getScheduler().runTask(getPlugin(), () -> p.getWorld().dropItemNaturally(p.getLocation(), itemDrop));
        }
        if (!listener.equalsIgnoreCase("none")) {
            p.getInventory().remove(jpItem);
            p.updateInventory();
        }

        msgListener(p, listener, onEmptyListener);
    }

    private void msgListener(@NotNull Player p, @NotNull String listener, boolean onEmptyListener) {
        Boolean bool = playerMsged.get(p.getUniqueId());
        if (bool != null && bool) return;
        playerMsged.put(p.getUniqueId(), true);
        if (onEmptyListener)
            p.sendMessage(listener.equalsIgnoreCase("drop") ? JetpackManager.OnEmptyFuelDropped : JetpackManager.OnEmptyFuelRemoved);
        if (!onEmptyListener)
            p.sendMessage(listener.equalsIgnoreCase("drop") ? JetpackManager.OnDeathDropped : JetpackManager.OnDeathRemoved);
        final UUID uuid = p.getUniqueId();
        new BukkitRunnable() {
            @Override
            public void run() {
                playerMsged.put(uuid, false);
            }
        }.runTaskLaterAsynchronously(getPlugin(),  40L);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onRespawn(PlayerRespawnEvent e) {
        try {
            playerMsged.put(e.getPlayer().getUniqueId(), false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onDeath(PlayerDeathEvent e) {
        try {
            if (e.getEntity().getType() != EntityType.PLAYER) return;
            Player p = e.getEntity();

            if (plist.contains(p)) {
                PlayerConfig playerConfig = playerConfigsList.get(p.getUniqueId());
                if (playerConfig != null) {
                    Jetpack jp = jetpacksLoaded.get(playerConfig.getJetpackID());
                    if (jp != null) {
                        if (!jp.getOnEmptyFuel().equalsIgnoreCase("none") || !jp.getOnDeath().equalsIgnoreCase("none")) {
                            playerConfig.setDied(true);
                            DelPFly(p);
                            msgListener(p, jp.getOnDeath(), false);
                        }
                    }
                }
            }

            for (Jetpack jp : jetpacksLoaded.values()) {
                if (!jp.getOnDeath().equalsIgnoreCase("remove") && !jp.getOnDeath().equalsIgnoreCase("drop")) continue;
                PlayerInventory pi = e.getEntity().getInventory();
                ItemStack[] items = pi.getArmorContents();
                for (int i = 0; i < items.length; i++) {
                    if (items[i] == null) continue;
                    String id = ItemMetaData.getItemMetaDataString(items[i], GET_JETPACK_NAME);
                    if (id.length() < 1 || !id.equals(jp.getName())) continue;
                    onJetpackPlayerListener(p, items[i], false, jp.getOnDeath());
                    items[i] = null;
                }
                pi.setArmorContents(items);
                Iterator<ItemStack> itemStack = Arrays.stream(p.getInventory().getContents()).iterator();
                while (itemStack.hasNext()) {
                    ItemStack item = itemStack.next();
                    if (item == null) continue;
                    String id = ItemMetaData.getItemMetaDataString(item, GET_JETPACK_NAME);
                    if (id.length() < 1 || !id.equals(jp.getName())) continue;
                    onJetpackPlayerListener(p, item, false, jp.getOnDeath());
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onDamagedPlayerArmor(EntityDamageEvent e) {
        try {
            Entity entity = e.getEntity();
            if (nmsServerVersion.startsWith("v1_17_") || nmsServerVersion.startsWith("v1_18_") || !(entity instanceof Player))
                return;
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

    @EventHandler(priority = EventPriority.LOWEST)
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
        if (e instanceof InventoryCreativeEvent) return;
        Player p = (Player) e.getWhoClicked();
        try {
            ItemStack item = e.getCurrentItem();
            ItemStack cursorItem = e.getCursor();
            EntityEquipment eq = p.getEquipment();

            if (plist.contains(p) && p.getAllowFlight()) {
                Jetpack jp = jetpacksLoaded.get(ItemMetaData.getItemMetaDataString(item, GET_JETPACK_NAME));
                if (jp != null) {
                    boolean contains = false;
                    if (eq != null) {
                        ItemStack[] items = eq.getArmorContents();
                        for (ItemStack itemStack : items) {
                            if (itemStack == null || itemStack.equals(item)) continue;
                            String id = ItemMetaData.getItemMetaDataString(itemStack, GET_JETPACK_NAME);
                            if (jetpacksLoaded.get(id) == null) continue;
                            contains = true;
                            break;
                        }
                    }
                    if (!contains) {
                        PCopot(p);
                        DelPFly(p);
                    }
                    return;
                }
            }

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
            if (!p.hasPermission(jetpack.getPermission()) && !p.hasPermission(jetpack.getPermission() + ".refuel")) {
                pesan(p, TidakAdaAkses);
                return;
            }
            if (e.getCursor() == null || e.getCurrentItem() == null || item.getAmount() == 0) return;
            String fuelItem = jetpack.getFuel();

            if (jetpack.getFuel().startsWith("@")) {
                String idCf = ItemMetaData.getItemMetaDataString(cursorItem, GET_CUSTOM_FUEL_ID);
                if (idCf.length() < 1) return;
                CustomFuel customFuel = JetpackManager.customFuelsLoaded.get(idCf.substring(1));
                if (customFuel == null) return;
                if (!p.hasPermission(customFuel.getPermission())) {
                    p.sendMessage(JetpackManager.PrefixPesan + ChatColor.RED + JetpackManager.TidakAdaAkses);
                    return;
                }
            } else {
                Material BBM = Material.valueOf(fuelItem.toUpperCase());
                if (!e.getCursor().getType().equals(BBM)) return;
            }

            e.setCancelled(true);

            boolean isLeftClicked = e.getClick() == ClickType.LEFT;
            int fuelAdded = isLeftClicked ? cursorItem.getAmount() : 1;

            int FuelValNow = getIntOnly(ItemMetaData.getItemMetaDataString(item, GET_JETPACK_FUEL), 0);
            String newFuelVal = String.valueOf(FuelValNow + fuelAdded);
            item = updateLore(item, itemMeta, newFuelVal, jetpack);
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
                if (!e.getPlayer().hasPermission(jetpack.getPermission()) && !e.getPlayer().hasPermission(jetpack.getPermission() + ".use")) {
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
                int fuelNow = 0;
                try {
                    if (!jetpack.getFuel().startsWith("@")) {
                        Material fuelType = Material.valueOf(jetpack.getFuel().toUpperCase());
                        if (fuelType != Material.AIR) {
                            // Cek bahan bakar
                            String displayFuel = jetpack.getFuel().replace("_", " ");
                            fuelNow = getIntOnly(ItemMetaData.getItemMetaDataString(eqc, GET_JETPACK_FUEL), 0);
                            if (fuelNow < jetpack.getFuelAmout()) {
                                pesan(p, TidakAdaBensin.replaceAll("%fuel_item%", displayFuel));
                                return;
                            }
                        }
                    } else {
                        fuelNow = getIntOnly(ItemMetaData.getItemMetaDataString(eqc, GET_JETPACK_FUEL), 0);
                        if (fuelNow < jetpack.getFuelAmout()) {
                            pesan(p, TidakAdaBensin.replaceAll("%fuel_item%", translateCodes(customFuelsLoaded.get(jetpack.getFuel().substring(1)).getDisplayName())));
                            return;
                        }
                    }
                } catch (Exception ex) {
                    return;
                }

                // Aktifkan
                BukkitTask particleTask = null;
                if (!jetpack.getParticleEffect().equalsIgnoreCase("none") && !nmsServerVersion.startsWith("v1_8_"))
                    particleTask = partikelTask(p, jetpack);
                playerConfigsList.put(p.getUniqueId(), new PlayerConfig(jetpack.getName(), BakarBahanBakarTask(p, jetpack), particleTask));

                p.setAllowFlight(true);
                p.setFlySpeed(Float.parseFloat(jetpack.getSpeed()) / 10.0F);
                pesan(p, PesanJetpackAktif);
                plist.add(p);

                String burnStatus = ItemMetaData.getItemMetaDataString(eqc, GET_JETPACK_IS_BURNING);
                if (burnStatus.equals(String.valueOf(1))) {
                    fuelNow -= jetpack.getFuelAmout();
                    eqc = updateLore(eqc, eqm, String.valueOf(fuelNow), jetpack);
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
            } catch (Exception ignored) {
            }
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
                        if (eqc.getItemMeta() == null || (!p.hasPermission(jetpack.getPermission()) && !p.hasPermission(jetpack.getPermission() + ".use"))) {
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

                            int FuelValNow = getIntOnly(ItemMetaData.getItemMetaDataString(eqc, GET_JETPACK_FUEL), 0);
                            if (FuelValNow < jetpack.getFuelAmout()) {
                                pesan(p, BahanBakarHabis);
                                onJetpackPlayerListener(p, eqc, true, jetpack.getOnEmptyFuel());
                                cancel();
                                return;
                            }
                            String newFuelVal = String.valueOf(FuelValNow - jetpack.getFuelAmout());
                            eqc = updateLore(eqc, eqm, newFuelVal, jetpack);
                            if (p.getEquipment() == null || p.getEquipment().getArmorContents().length < 1) {
                                PCopot(p);
                                cancel();
                                return;
                            }
                            p.getEquipment().setArmorContents(updateItemsFJP(armors, eqc));
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
                } catch (Exception ignored) {
                }
                try {
                    DelPFly(p);
                } catch (Exception ignored) {
                }
            }
        }.runTaskTimerAsynchronously(getPlugin(), jetpack.getBurnRate() * 20L, jetpack.getBurnRate() * 20L);
    }

    public static ItemStack updateLore(ItemStack eqc, ItemMeta eqm, String newFuelVal, Jetpack jetpack) {
        String fl = jetpack.getFuel();
        if (fl.startsWith("@")) {
            fl = fl.substring(1);
            CustomFuel customFuel = customFuelsLoaded.get(fl);
            if (customFuel != null)
                fl = translateCodes(customFuel.getCustomDisplay().length() < 1 ? customFuel.getDisplayName() : customFuel.getCustomDisplay());
        } else
            fl = fl.replace("_", " ");
        List<String> newLore = new ArrayList<>();
        for (String conflore : jetpack.getLore()) {
            conflore = translateCodes(conflore);
            conflore = conflore.replace(JETPACK_FUEL_VAR, fl)
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
                            p.getWorld().spawnParticle(Partikel, p.getLocation().getX() + newZ, p.getLocation().getY() + 0.8D, p.getLocation().getZ() + newZ, jetpack.getParticleAmount(), 0.0D, -0.1D, 0.0D);
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
                } catch (Exception ignored) {
                }
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
                                        DelPFlyUnlod(p, true);
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

    public static void FlycekPL(Player p) {
        try {
            PluginManager pm = getServer().getPluginManager();
            if (pm.isPluginEnabled("CMI"))
                getServer().getScheduler().callSyncMethod(getPlugin(), () -> getServer().dispatchCommand(getServer().getConsoleSender(), "cmi fly " + p.getName() + " false -s"));
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
                if (getIntOnly(getPlugin().getDescription().getVersion(), 1) == getIntOnly(version, 1))
                    pesan(p, PrefixPesan + "§aThere is not a new update available. You are using the latest version");
                else if (getIntOnly(getPlugin().getDescription().getVersion(), 1) >= getIntOnly(version, 1))
                    pesan(p, PrefixPesan + "§aThere is not a new update available. You are using the latest dev build version");
                else {
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