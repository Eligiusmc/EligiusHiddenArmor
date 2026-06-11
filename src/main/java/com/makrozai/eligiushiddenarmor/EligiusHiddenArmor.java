package com.makrozai.eligiushiddenarmor;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import com.makrozai.eligiushiddenarmor.command.EligiusHiddenArmorTabCompleter;
import com.makrozai.eligiushiddenarmor.command.EligiusHiddenArmorCommand;
import com.makrozai.eligiushiddenarmor.handler.ArmorPlaceholderHandler;
import com.makrozai.eligiushiddenarmor.handler.ArmorUpdateHandler;
import com.makrozai.eligiushiddenarmor.handler.MessageHandler;
import com.makrozai.eligiushiddenarmor.adapter.network.WindowItemsPacketListener;
import com.makrozai.eligiushiddenarmor.util.ConfigHolder;
import org.bstats.bukkit.Metrics;
import com.makrozai.eligiushiddenarmor.listener.EntityToggleGlideListener;
import com.makrozai.eligiushiddenarmor.listener.GameModeListener;
import com.makrozai.eligiushiddenarmor.listener.PotionEffectListener;
import com.makrozai.eligiushiddenarmor.listener.InventoryShiftClickListener;
import com.makrozai.eligiushiddenarmor.adapter.network.EntityEquipmentPacketListener;
import com.makrozai.eligiushiddenarmor.adapter.network.SetSlotPacketListener;
import com.makrozai.eligiushiddenarmor.domain.service.ArmorHideService;
import com.makrozai.eligiushiddenarmor.domain.port.DatabasePort;
import com.makrozai.eligiushiddenarmor.adapter.database.DatabaseAdapter;
import com.makrozai.eligiushiddenarmor.adapter.network.redis.RedisManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@SuppressWarnings("deprecation")
public final class EligiusHiddenArmor extends JavaPlugin {
    private ArmorHideService ArmorHideService;
    private ArmorUpdateHandler armorUpdater;
    private ArmorPlaceholderHandler armorPlaceholderHandler;
    private MessageHandler messageHandler;
    private com.makrozai.eligiushiddenarmor.domain.port.PlatformPort platformPort;
    private com.makrozai.eligiushiddenarmor.util.PlayerCache playerCache;
    private DatabasePort databasePort;
    private RedisManager redisManager;

    private List<ConfigHolder> configHolders;


    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings()
                .reEncodeByDefault(true)
                .checkForUpdates(false);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        // Default config file
        this.saveDefaultConfig();
        checkConfig();

        // Database initialization
        this.databasePort = new DatabaseAdapter(this);
        if (!this.databasePort.initialize()) {
            getLogger().severe("Could not initialize the database. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Instantiate members
        PacketEvents.getAPI().init();
        this.messageHandler = new MessageHandler(this);
        this.armorUpdater = new ArmorUpdateHandler(this);
        this.ArmorHideService = new ArmorHideService(this, databasePort);
        this.armorPlaceholderHandler = new ArmorPlaceholderHandler(this);
        this.playerCache = new com.makrozai.eligiushiddenarmor.util.PlayerCache(this);
        this.redisManager = new RedisManager(this);

        long startTime = System.currentTimeMillis();
        StartupLogger.printLogo(getDescription().getVersion(), "Spigot/Paper", getConfig().getString("database.type", "sqlite").toUpperCase());

        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            this.platformPort = new com.makrozai.eligiushiddenarmor.adapter.platform.FoliaPlatformAdapter(this);
            getLogger().info("Folia detected, using Folia scheduler.");
        } catch (ClassNotFoundException e) {
            this.platformPort = new com.makrozai.eligiushiddenarmor.adapter.platform.SpigotPlatformAdapter(this);
            getLogger().info("Using Bukkit/Spigot scheduler.");
        }

        // Enable commands
        new EligiusHiddenArmorCommand(this, "eligiushiddenarmor")
                .setPermission("eligiushiddenarmor.toggle")
                .setPermissionRequired(false)
                .setTabCompleter(new EligiusHiddenArmorTabCompleter(this));

        // Register PacketEvents listeners
        PacketEvents.getAPI().getEventManager().registerListener(new SetSlotPacketListener(this));
        PacketEvents.getAPI().getEventManager().registerListener(new WindowItemsPacketListener(this));
        PacketEvents.getAPI().getEventManager().registerListener(new EntityEquipmentPacketListener(this));

        // Register event listeners
        new InventoryShiftClickListener(this);
        new GameModeListener(this);
        new PotionEffectListener(this);
        new EntityToggleGlideListener(this);

        reloadConfig();
        if (getConfig().getBoolean("bstats.enabled", true)) {
            System.setProperty("bstats.relocatecheck", "false");
            int pluginId = 31917; 
            new Metrics(this, pluginId);
            getLogger().info("bStats metrics enabled.");
        }
        this.redisManager.connect();

        StartupLogger.printSuccess(System.currentTimeMillis() - startTime);
        if (getConfig().getBoolean("check_updates", true)) {
            com.makrozai.eligiushiddenarmor.domain.service.UpdateChecker.fetch(getDescription().getVersion());
        }
    }

    @Override
    public void onDisable() {
        if (ArmorHideService != null) {
            ArmorHideService.saveAllToDatabase();
        }
        PacketEvents.getAPI().terminate();
        if (databasePort != null) {
            databasePort.close();
        }
        if (redisManager != null) {
            redisManager.close();
        }
    }

    private void checkConfig() {
        reloadConfig();
        if(getConfig().getInt("config-version") >= getConfig().getDefaults().getInt("config-version"))
            return;
        getLogger().log(Level.WARNING, "Your EligiusHiddenArmor configuration file is outdated!");
        getLogger().log(Level.WARNING, "Please regenerate the 'config.yml' file when possible.");
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (configHolders == null)
            configHolders = new ArrayList<>();
        for (ConfigHolder c : configHolders) {
            c.loadConfig(getConfig());
        }
    }

    public void addConfigHolder(ConfigHolder configHolder) {
        configHolders.add(configHolder);
    }

    public ArmorHideService getArmorHideService() {
        return ArmorHideService;
    }

    public ArmorUpdateHandler getArmorUpdater() {
        return armorUpdater;
    }

    public ArmorPlaceholderHandler getArmorPlaceholderHandler() {
        return armorPlaceholderHandler;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public com.makrozai.eligiushiddenarmor.domain.port.PlatformPort getPlatformPort() {
        return platformPort;
    }

    public com.makrozai.eligiushiddenarmor.util.PlayerCache getPlayerCache() {
        return playerCache;
    }

    public RedisManager getRedisManager() {
        return redisManager;
    }
}
