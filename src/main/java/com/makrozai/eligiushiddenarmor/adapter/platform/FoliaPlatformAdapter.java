package com.makrozai.eligiushiddenarmor.adapter.platform;

import com.makrozai.eligiushiddenarmor.domain.port.PlatformPort;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class FoliaPlatformAdapter implements PlatformPort {
    private final Plugin plugin;

    public FoliaPlatformAdapter(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runTask(Runnable task) {
        Bukkit.getServer().getGlobalRegionScheduler().execute(plugin, task);
    }

    @Override
    public void runTaskAsync(Runnable task) {
        Bukkit.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> task.run());
    }

    @Override
    public void runTaskLater(Runnable task, long delay) {
        Bukkit.getServer().getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> task.run(), Math.max(1, delay));
    }

    @Override
    public void runTaskTimer(Runnable task, long delay, long period) {
        Bukkit.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> task.run(), Math.max(1, delay), Math.max(1, period));
    }

    @Override
    public boolean isFolia() {
        return true;
    }
}
