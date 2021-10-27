package me.phantomxcraft.jetpack;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class PlayerConfig {
    private final BukkitTask flyTask, particleTask;
    private boolean forceStopTask;

    public PlayerConfig(BukkitTask flyTask, BukkitTask particleTask) {
        this.flyTask = flyTask;
        this.particleTask = particleTask;
    }

    public boolean isForceStopTask() {
        return forceStopTask;
    }

    public void stopAll() {
        forceStopTask = true;
        if (flyTask != null) flyTask.cancel();
        if (particleTask != null) particleTask.cancel();
    }
}
