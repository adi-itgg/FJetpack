package me.phantomxcraft.abstrak;

import org.bukkit.scheduler.BukkitTask;

public class PlayerConfig {
    private final BukkitTask flyTask, particleTask;
    private final String jetpackID;
    private boolean isDied;
    private boolean forceStopTask;

    public PlayerConfig(String jetpackID, BukkitTask flyTask, BukkitTask particleTask) {
        this.jetpackID = jetpackID;
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

    public String getJetpackID() {
        return jetpackID;
    }

    public boolean isDied() {
        return isDied;
    }

    public void setDied(boolean died) {
        isDied = died;
    }
}
