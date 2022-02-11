package me.phantomxcraft.data;

import java.util.List;

public class Jetpack {
    private final String Permission, Fuel, DisplayName, Name, Speed, JetpackItem, ParticleEffect, ParticleDelay, OnEmptyFuel, OnDeath;
    private final int FuelAmout, FuelCostFlySprint, ParticleAmount, BurnRate;
    private final List<String> Lore, Flags, Enchantments, WorldBlacklist;
    private final boolean CanBypass, CanSprintBypass, Unbreakable;

    public Jetpack(String permission, boolean canBypass, boolean canSprintBypass, String fuel, int fuelAmout, int fuelCostFlySprint, String displayName, String name, String speed, String jetpackItem, String particleEffect, int particleAmount, int burnRate, String particleDelay, List<String> lore, List<String> flags, List<String> enchantments, List<String> worldBlacklist, boolean unbreakable, String onEmptyFuel, String onDeath) {
        Permission = permission;
        CanBypass = canBypass;
        CanSprintBypass = canSprintBypass;
        Fuel = fuel;
        FuelAmout = fuelAmout;
        FuelCostFlySprint = fuelCostFlySprint;
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
        OnEmptyFuel = onEmptyFuel;
        OnDeath = onDeath;
    }

    public String getPermission() {
        return Permission;
    }

    public String getFuel() {
        return Fuel;
    }

    public int getFuelAmout() {
        return FuelAmout;
    }

    public int getFuelCostFlySprint() {
        return FuelCostFlySprint;
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

    public int getParticleAmount() {
        return ParticleAmount;
    }

    public int getBurnRate() {
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

    public String getOnEmptyFuel() {
        return OnEmptyFuel;
    }

    public String getOnDeath() {
        return OnDeath;
    }

    public boolean isCanBypass() {
        return CanBypass;
    }

    public boolean isCanSprintBypass() {
        return CanSprintBypass;
    }
}
