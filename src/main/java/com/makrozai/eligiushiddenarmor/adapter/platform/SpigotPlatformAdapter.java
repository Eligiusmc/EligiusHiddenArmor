package com.makrozai.eligiushiddenarmor.adapter.platform;

import com.makrozai.eligiushiddenarmor.domain.port.PlatformPort;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class SpigotPlatformAdapter implements PlatformPort {
    private final Plugin plugin;

    public SpigotPlatformAdapter(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runTask(Runnable task) {
        Bukkit.getScheduler().runTask(plugin, task);
    }

    @Override
    public void runTaskAsync(Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    @Override
    public void runTaskLater(Runnable task, long delay) {
        Bukkit.getScheduler().runTaskLater(plugin, task, delay);
    }

    @Override
    public void runTaskTimer(Runnable task, long delay, long period) {
        Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
    }

    @Override
    public boolean isFolia() {
        return false;
    }
}
