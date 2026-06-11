package com.makrozai.eligiushiddenarmor.adapter.network.redis;

import com.makrozai.eligiushiddenarmor.EligiusHiddenArmor;
import com.makrozai.eligiushiddenarmor.util.ConfigHolder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;
import java.util.logging.Level;

public class RedisManager implements ConfigHolder {

    private final EligiusHiddenArmor plugin;
    private JedisPool jedisPool;
    private JedisPubSub pubSub;
    private boolean enabled;
    private String host;
    private int port;
    private String password;
    private String channel;

    public RedisManager(EligiusHiddenArmor plugin) {
        this.plugin = plugin;
        plugin.addConfigHolder(this);
    }

    public void connect() {
        if (!enabled) return;

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(8);
        
        if (password != null && !password.isEmpty()) {
            jedisPool = new JedisPool(poolConfig, host, port, 2000, password);
        } else {
            jedisPool = new JedisPool(poolConfig, host, port, 2000);
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Jedis jedis = jedisPool.getResource()) {
                plugin.getLogger().info("Connected to Redis for cross-server synchronization!");
                pubSub = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        if (!channel.equals(RedisManager.this.channel)) return;
                        
                        // Message format: UPDATE:UUID
                        if (message.startsWith("UPDATE:")) {
                            try {
                                UUID uuid = UUID.fromString(message.substring(7));
                                Player player = Bukkit.getPlayer(uuid);
                                if (player != null && player.isOnline()) {
                                    // Fetch new data from DB asynchronously, then update
                                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                                        java.util.EnumSet<com.makrozai.eligiushiddenarmor.domain.model.ArmorPiece> updatedPieces = plugin.getArmorHideService().fetchFromDatabase(uuid);
                                        plugin.getArmorHideService().updateCacheFromRedis(uuid, updatedPieces);
                                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                                            plugin.getArmorUpdater().updatePlayer(player);
                                        });
                                    });
                                }
                            } catch (Exception e) {
                                plugin.getLogger().log(Level.WARNING, "Failed to parse Redis message: " + message, e);
                            }
                        }
                    }
                };
                jedis.subscribe(pubSub, channel);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to connect to Redis", e);
            }
        });
    }

    public void publishUpdate(UUID uuid) {
        if (!enabled || jedisPool == null) return;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.publish(channel, "UPDATE:" + uuid.toString());
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to publish Redis update", e);
            }
        });
    }

    public void close() {
        if (pubSub != null && pubSub.isSubscribed()) {
            pubSub.unsubscribe();
        }
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
        }
    }

    @Override
    public void loadConfig(FileConfiguration config) {
        this.enabled = config.getBoolean("redis.enabled", false);
        this.host = config.getString("redis.host", "127.0.0.1");
        this.port = config.getInt("redis.port", 6379);
        this.password = config.getString("redis.password", "");
        this.channel = config.getString("redis.channel", "eligiushiddenarmor:sync");
    }
}
