package com.makrozai.eligiushiddenarmor.adapter.database;

import com.makrozai.eligiushiddenarmor.domain.model.ArmorPiece;
import com.makrozai.eligiushiddenarmor.domain.port.DatabasePort;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class DatabaseAdapter implements DatabasePort {
    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private HikariDataSource hikariDataSource;
    private boolean isMySQL;

    public DatabaseAdapter(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public boolean initialize() {
        this.isMySQL = "mysql".equalsIgnoreCase(config.getString("database.type", "sqlite"));
        if (isMySQL) {
            return setupMySQL();
        } else {
            return setupSQLite();
        }
    }

    private boolean setupMySQL() {
        try {
            HikariConfig hc = new HikariConfig();
            String host = config.getString("database.host", "127.0.0.1");
            String port = config.getString("database.port", "3306");
            String name = config.getString("database.name", "eligiushiddenarmor");
            hc.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + name + "?useSSL=false&autoReconnect=true");
            hc.setUsername(config.getString("database.username", "root"));
            hc.setPassword(config.getString("database.password", "password"));
            hc.addDataSourceProperty("cachePrepStmts", "true");
            hc.addDataSourceProperty("prepStmtCacheSize", "250");
            hc.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            hc.setMaximumPoolSize(10);
            
            hikariDataSource = new HikariDataSource(hc);
            
            try (Connection conn = hikariDataSource.getConnection();
                 Statement statement = conn.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS player_hidden_armor (" +
                             "uuid VARCHAR(36) PRIMARY KEY," +
                             "hide_helmet BOOLEAN NOT NULL DEFAULT FALSE," +
                             "hide_chestplate BOOLEAN NOT NULL DEFAULT FALSE," +
                             "hide_leggings BOOLEAN NOT NULL DEFAULT FALSE," +
                             "hide_boots BOOLEAN NOT NULL DEFAULT FALSE" +
                             ");";
                statement.execute(sql);
            }
            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to setup MySQL", e);
            return false;
        }
    }

    private boolean setupSQLite() {
        try {
            Class.forName("org.sqlite.JDBC");
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            File dbFile = new File(dataFolder, "database.db");
            
            HikariConfig hc = new HikariConfig();
            hc.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
            hc.setPoolName("SQLite-Pool-HiddenArmor");
            hc.setMaximumPoolSize(1); // SQLite only supports 1 writer thread
            
            hikariDataSource = new HikariDataSource(hc);
            
            try (Connection conn = hikariDataSource.getConnection();
                 Statement statement = conn.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS player_hidden_armor (" +
                             "uuid VARCHAR(36) PRIMARY KEY," +
                             "hide_helmet BOOLEAN NOT NULL DEFAULT 0," +
                             "hide_chestplate BOOLEAN NOT NULL DEFAULT 0," +
                             "hide_leggings BOOLEAN NOT NULL DEFAULT 0," +
                             "hide_boots BOOLEAN NOT NULL DEFAULT 0" +
                             ");";
                statement.execute(sql);
            }
            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to setup SQLite", e);
            return false;
        }
    }

    private Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }

    @Override
    public EnumSet<ArmorPiece> getHiddenPieces(UUID uuid) {
        EnumSet<ArmorPiece> hiddenPieces = EnumSet.noneOf(ArmorPiece.class);
        String sql = "SELECT hide_helmet, hide_chestplate, hide_leggings, hide_boots FROM player_hidden_armor WHERE uuid = ?;";
        try {
            Connection conn = getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, uuid.toString());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getBoolean("hide_helmet")) hiddenPieces.add(ArmorPiece.HELMET);
                        if (rs.getBoolean("hide_chestplate")) hiddenPieces.add(ArmorPiece.CHESTPLATE);
                        if (rs.getBoolean("hide_leggings")) hiddenPieces.add(ArmorPiece.LEGGINGS);
                        if (rs.getBoolean("hide_boots")) hiddenPieces.add(ArmorPiece.BOOTS);
                    }
                }
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        } catch (SQLException | NullPointerException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to query hidden pieces for " + uuid, e);
        }
        return hiddenPieces;
    }

    @Override
    public void setHiddenPieces(UUID uuid, EnumSet<ArmorPiece> hiddenPieces) {
        String sql;
        if (isMySQL) {
            sql = "INSERT INTO player_hidden_armor (uuid, hide_helmet, hide_chestplate, hide_leggings, hide_boots) " +
                  "VALUES (?, ?, ?, ?, ?) " +
                  "ON DUPLICATE KEY UPDATE hide_helmet = VALUES(hide_helmet), hide_chestplate = VALUES(hide_chestplate), " +
                  "hide_leggings = VALUES(hide_leggings), hide_boots = VALUES(hide_boots);";
        } else {
            sql = "INSERT INTO player_hidden_armor (uuid, hide_helmet, hide_chestplate, hide_leggings, hide_boots) " +
                  "VALUES (?, ?, ?, ?, ?) " +
                  "ON CONFLICT(uuid) DO UPDATE SET hide_helmet = excluded.hide_helmet, " +
                  "hide_chestplate = excluded.hide_chestplate, hide_leggings = excluded.hide_leggings, hide_boots = excluded.hide_boots;";
        }
        
        try {
            Connection conn = getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, uuid.toString());
                pstmt.setBoolean(2, hiddenPieces.contains(ArmorPiece.HELMET));
                pstmt.setBoolean(3, hiddenPieces.contains(ArmorPiece.CHESTPLATE));
                pstmt.setBoolean(4, hiddenPieces.contains(ArmorPiece.LEGGINGS));
                pstmt.setBoolean(5, hiddenPieces.contains(ArmorPiece.BOOTS));
                pstmt.executeUpdate();
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        } catch (SQLException | NullPointerException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to update hidden pieces for " + uuid, e);
        }
    }

    @Override
    public Map<UUID, EnumSet<ArmorPiece>> getAllHiddenPieces() {
        Map<UUID, EnumSet<ArmorPiece>> allData = new HashMap<>();
        String sql = "SELECT * FROM player_hidden_armor;";
        try {
            Connection conn = getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        UUID uuid = UUID.fromString(rs.getString("uuid"));
                        EnumSet<ArmorPiece> pieces = EnumSet.noneOf(ArmorPiece.class);
                        if (rs.getBoolean("hide_helmet")) pieces.add(ArmorPiece.HELMET);
                        if (rs.getBoolean("hide_chestplate")) pieces.add(ArmorPiece.CHESTPLATE);
                        if (rs.getBoolean("hide_leggings")) pieces.add(ArmorPiece.LEGGINGS);
                        if (rs.getBoolean("hide_boots")) pieces.add(ArmorPiece.BOOTS);
                        
                        if (!pieces.isEmpty()) {
                            allData.put(uuid, pieces);
                        }
                    } catch (IllegalArgumentException ignored) {}
                }
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        } catch (SQLException | NullPointerException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to fetch all hidden pieces", e);
        }
        return allData;
    }

    @Override
    public void close() {
        if (hikariDataSource != null && !hikariDataSource.isClosed()) {
            hikariDataSource.close();
        }
    }
}
