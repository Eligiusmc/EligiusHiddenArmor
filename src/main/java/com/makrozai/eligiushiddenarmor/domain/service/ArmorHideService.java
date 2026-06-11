package com.makrozai.eligiushiddenarmor.domain.service;

import com.makrozai.eligiushiddenarmor.EligiusHiddenArmor;
import com.makrozai.eligiushiddenarmor.domain.model.ArmorPiece;
import com.makrozai.eligiushiddenarmor.domain.port.DatabasePort;
import com.makrozai.eligiushiddenarmor.handler.ArmorUpdateHandler;
import com.makrozai.eligiushiddenarmor.handler.MessageHandler;
import com.makrozai.eligiushiddenarmor.util.ConfigHolder;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Domain service responsible for managing the hidden armor state of players.
 * This class coordinates the in-memory cache, database persistence, and network
 * synchronization via Redis, following Hexagonal Architecture principles.
 */
public class ArmorHideService implements ConfigHolder {
    private final EligiusHiddenArmor plugin;
    private final ArmorUpdateHandler armorUpdater;
    private final MessageHandler messageHandler;
    private final DatabasePort databasePort;

    private boolean invisibleAlwaysHideGear;

    private final Map<UUID, EnumSet<ArmorPiece>> hiddenPiecesCache = new ConcurrentHashMap<>();
    private final Set<Predicate<Player>> forceDisablePredicates = new HashSet<>();
    private final Set<Predicate<Player>> forceEnablePredicates = new HashSet<>();

    public ArmorHideService(EligiusHiddenArmor plugin, DatabasePort databasePort) {
        this.plugin = plugin;
        this.databasePort = databasePort;
        plugin.addConfigHolder(this);
        this.armorUpdater = plugin.getArmorUpdater();
        this.messageHandler = plugin.getMessageHandler();
        registerDefaultPredicates();
        
        // Load data only for currently online players (e.g. on /reload)
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                EnumSet<ArmorPiece> pieces = databasePort.getHiddenPieces(p.getUniqueId());
                if (pieces != null && !pieces.isEmpty()) {
                    hiddenPiecesCache.put(p.getUniqueId(), pieces);
                }
            }
        });
    }

    /**
     * Toggles a specific armor piece for the player.
     *
     * @param player The player whose armor piece visibility will be toggled.
     * @param piece  The specific ArmorPiece to toggle.
     * @param inform Whether to send a notification message to the player.
     */
    public void togglePiece(Player player, ArmorPiece piece, boolean inform) {
        EnumSet<ArmorPiece> pieces = hiddenPiecesCache.getOrDefault(player.getUniqueId(), EnumSet.noneOf(ArmorPiece.class));
        if (pieces.contains(piece)) {
            showPiece(player, piece, inform);
        } else {
            hidePiece(player, piece, inform);
        }
    }

    public void hidePiece(Player player, ArmorPiece piece, boolean inform) {
        EnumSet<ArmorPiece> pieces = hiddenPiecesCache.computeIfAbsent(player.getUniqueId(), k -> EnumSet.noneOf(ArmorPiece.class));
        if (pieces.contains(piece)) return;
        
        pieces.add(piece);
        
        if (inform) {
            Map<String, String> placeholderMap = new HashMap<>();
            placeholderMap.put("piece", getPieceName(piece));
            placeholderMap.put("visibility", messageHandler.getLocalizedLegacyMessage("visibility_hidden_word"));
            messageHandler.messageActionBar(player, "armor_piece_visibility", false, placeholderMap);
        }

        armorUpdater.updatePlayer(player);
    }

    public void showPiece(Player player, ArmorPiece piece, boolean inform) {
        EnumSet<ArmorPiece> pieces = hiddenPiecesCache.get(player.getUniqueId());
        if (pieces == null || !pieces.contains(piece)) return;
        
        pieces.remove(piece);
        if (pieces.isEmpty()) {
            hiddenPiecesCache.remove(player.getUniqueId());
        }
        
        if (inform) {
            Map<String, String> placeholderMap = new HashMap<>();
            placeholderMap.put("piece", getPieceName(piece));
            placeholderMap.put("visibility", messageHandler.getLocalizedLegacyMessage("visibility_shown_word"));
            messageHandler.messageActionBar(player, "armor_piece_visibility", false, placeholderMap);
        }

        armorUpdater.updatePlayer(player);
    }

    public void saveToDatabase(UUID uuid) {
        EnumSet<ArmorPiece> pieces = hiddenPiecesCache.get(uuid);
        EnumSet<ArmorPiece> clone = (pieces == null || pieces.isEmpty()) ? EnumSet.noneOf(ArmorPiece.class) : EnumSet.copyOf(pieces);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            databasePort.setHiddenPieces(uuid, clone);
            if (plugin.getRedisManager() != null) {
                plugin.getRedisManager().publishUpdate(uuid);
            }
        });
    }

    /**
     * Fetches the hidden armor pieces for a specific player from the database.
     *
     * @param uuid The UUID of the player.
     * @return An EnumSet containing the currently hidden armor pieces.
     */
    public EnumSet<ArmorPiece> fetchFromDatabase(UUID uuid) {
        return databasePort.getHiddenPieces(uuid);
    }

    /**
     * Updates the local in-memory cache with new data received from Redis Pub/Sub.
     * 
     * @param uuid   The UUID of the player.
     * @param pieces The updated set of hidden armor pieces.
     */
    public void updateCacheFromRedis(UUID uuid, EnumSet<ArmorPiece> pieces) {
        if (pieces == null || pieces.isEmpty()) {
            hiddenPiecesCache.remove(uuid);
        } else {
            hiddenPiecesCache.put(uuid, pieces);
        }
    }

    /**
     * Removes a player from the local memory cache to prevent memory leaks.
     * Expected to be called when a player leaves the server.
     *
     * @param uuid The UUID of the player to remove.
     */
    public void removePlayerFromCache(UUID uuid) {
        hiddenPiecesCache.remove(uuid);
    }

    public void saveAllToDatabase() {
        for (UUID uuid : hiddenPiecesCache.keySet()) {
            EnumSet<ArmorPiece> pieces = hiddenPiecesCache.get(uuid);
            EnumSet<ArmorPiece> clone = (pieces == null || pieces.isEmpty()) ? EnumSet.noneOf(ArmorPiece.class) : EnumSet.copyOf(pieces);
            databasePort.setHiddenPieces(uuid, clone);
        }
    }

    /**
     * Toggles the visibility of all armor pieces for a player.
     * If the player has any hidden pieces, this will show them all.
     * If all pieces are visible, this will hide them all.
     *
     * @param player The player to toggle.
     * @param inform Whether to send a notification message to the player.
     */
    public void togglePlayer(Player player, boolean inform) {
        if (isEnabled(player)) {
            disablePlayer(player, inform);
        } else {
            enablePlayer(player, inform);
        }
    }

    public void enablePlayer(Player player, boolean inform) {
        EnumSet<ArmorPiece> all = EnumSet.allOf(ArmorPiece.class);
        hiddenPiecesCache.put(player.getUniqueId(), all);
        
        if (inform) {
            Map<String, String> placeholderMap = new HashMap<>();
            placeholderMap.put("visibility", messageHandler.getLocalizedLegacyMessage("visibility_hidden_word"));
            messageHandler.messageActionBar(player, "armor_visibility", false, placeholderMap);
        }
        armorUpdater.updatePlayer(player);
    }

    public void disablePlayer(Player player, boolean inform) {
        hiddenPiecesCache.remove(player.getUniqueId());
        
        if (inform) {
            Map<String, String> placeholderMap = new HashMap<>();
            placeholderMap.put("visibility", messageHandler.getLocalizedLegacyMessage("visibility_shown_word"));
            messageHandler.messageActionBar(player, "armor_visibility", false, placeholderMap);
        }
        armorUpdater.updatePlayer(player);
    }

    // Returns true if AT LEAST ONE piece is hidden
    public boolean isEnabled(Player player) {
        return hiddenPiecesCache.containsKey(player.getUniqueId()) && !hiddenPiecesCache.get(player.getUniqueId()).isEmpty();
    }

    public boolean isPieceHidden(Player player, ArmorPiece piece) {
        boolean hidden = false;
        EnumSet<ArmorPiece> pieces = hiddenPiecesCache.get(player.getUniqueId());
        if (pieces != null && pieces.contains(piece)) {
            hidden = true;
        }

        for (Predicate<Player> predicate : forceDisablePredicates) {
            if (predicate.test(player)) {
                hidden = false;
                break;
            }
        }
        for (Predicate<Player> predicate : forceEnablePredicates) {
            if (predicate.test(player)) {
                hidden = true;
                break;
            }
        }
        return hidden;
    }

    public boolean isSlotHidden(Player player, int slotId) {
        ArmorPiece piece = ArmorPiece.fromSlotId(slotId);
        if (piece == null) return false;
        return isPieceHidden(player, piece);
    }

    // Maintained for backward compatibility / global checks
    public boolean isArmorVisible(Player player) {
        boolean hidden = isEnabled(player);
        for (Predicate<Player> predicate : forceDisablePredicates) {
            if (predicate.test(player)) {
                hidden = false;
                break;
            }
        }
        for (Predicate<Player> predicate : forceEnablePredicates) {
            if (predicate.test(player)) {
                hidden = true;
                break;
            }
        }
        return !hidden;
    }

    private String getPieceName(ArmorPiece piece) {
        return messageHandler.getLocalizedLegacyMessage("piece_" + piece.name().toLowerCase());
    }

    private void registerDefaultPredicates() {
        forceDisablePredicates.add(player -> player.isInvisible() && !invisibleAlwaysHideGear);
        forceEnablePredicates.add(player -> player.isInvisible() && invisibleAlwaysHideGear);
    }

    @Override
    public void loadConfig(FileConfiguration config) {
        this.invisibleAlwaysHideGear = config.getBoolean("invisibility-potion.always-hide-gear");
    }
}
