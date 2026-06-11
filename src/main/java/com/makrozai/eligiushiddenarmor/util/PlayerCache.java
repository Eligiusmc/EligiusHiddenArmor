package com.makrozai.eligiushiddenarmor.util;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import com.makrozai.eligiushiddenarmor.EligiusHiddenArmor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Listener responsible for managing the local in-memory player session cache.
 * It caches player objects on join and removes them on quit to prevent memory leaks,
 * while also fetching initial hidden armor data from the database.
 */
public class PlayerCache implements Listener {
    private final Map<Integer, Player> cache = new ConcurrentHashMap<>();

    public PlayerCache(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            cache.put(p.getEntityId(), p);
        }
    }

    /**
     * Retrieves the Player instance associated with a specific entity ID.
     *
     * @param entityId The entity ID to search for.
     * @return The Player object if cached, null otherwise.
     */
    public Player getPlayer(int entityId) {
        return cache.get(entityId);
    }

    /**
     * Handles player connections, caching their entity ID and asynchronously
     * loading their hidden armor configuration from the database.
     *
     * @param event The PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        cache.put(p.getEntityId(), p);
        
        EligiusHiddenArmor plugin = JavaPlugin.getPlugin(EligiusHiddenArmor.class);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            java.util.EnumSet<com.makrozai.eligiushiddenarmor.domain.model.ArmorPiece> pieces = plugin.getArmorHideService().fetchFromDatabase(p.getUniqueId());
            if (pieces != null && !pieces.isEmpty()) {
                plugin.getArmorHideService().updateCacheFromRedis(p.getUniqueId(), pieces);
            }
        });
    }

    /**
     * Handles player disconnections, removing them from the cache to prevent
     * memory leaks, and triggering a final asynchronous save to the database.
     *
     * @param event The PlayerQuitEvent
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        cache.remove(p.getEntityId());
        
        EligiusHiddenArmor plugin = JavaPlugin.getPlugin(EligiusHiddenArmor.class);
        plugin.getArmorHideService().saveToDatabase(p.getUniqueId());
        plugin.getArmorHideService().removePlayerFromCache(p.getUniqueId());
    }
}
