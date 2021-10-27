package me.phantomxcraft.jetpack;

import java.util.List;

public class Jetpack {
    private final String Permission, Fuel, FuelAmout, DisplayName, Name, Speed, JetpackItem, ParticleEffect, ParticleAmount, BurnRate, ParticleDelay;
    private final List<String> Lore, Flags, Enchantments, WorldBlacklist;
    private final boolean Unbreakable;

    public Jetpack(String permission, String fuel, String fuelAmout, String displayName, String name, String speed, String jetpackItem, String particleEffect, String particleAmount, String burnRate, String particleDelay, List<String> lore, List<String> flags, List<String> enchantments, List<String> worldBlacklist, boolean unbreakable) {
        Permission = permission;
        Fuel = fuel;
        FuelAmout = fuelAmout;
        DisplayName = displayName;
        Name = name;
        Speed = speed;
        JetpackItem = jetpackItem;
        ParticleEffect = particleEffect;
        ParticleAmount = particleAmount;
        BurnRate = burnRate;
        ParticleDelay = particleDelay;
        Lore = lore;
        Flags = flags;
        Enchantments = enchantments;
        WorldBlacklist = worldBlacklist;
        Unbreakable = unbreakable;
    }

    public String getPermission() {
        return Permission;
    }

    public String getFuel() {
        return Fuel;
    }

    public String getFuelAmout() {
        return FuelAmout;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public String getName() {
        return Name;
    }

    public String getSpeed() {
        return Speed;
    }

    public String getJetpackItem() {
        return JetpackItem;
    }

    public String getParticleEffect() {
        return ParticleEffect;
    }

    public String getParticleAmount() {
        return ParticleAmount;
    }

    public String getBurnRate() {
        return BurnRate;
    }

    public String getParticleDelay() {
        return ParticleDelay;
    }

    public List<String> getLore() {
        return Lore;
    }

    public List<String> getFlags() {
        return Flags;
    }

    public List<String> getEnchantments() {
        return Enchantments;
    }

    public List<String> getWorldBlacklist() {
        return WorldBlacklist;
    }

    public boolean isUnbreakable() {
        return Unbreakable;
    }
}
