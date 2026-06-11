package com.makrozai.eligiushiddenarmor.handler;

import com.makrozai.eligiushiddenarmor.EligiusHiddenArmor;
import com.makrozai.eligiushiddenarmor.util.ConfigHolder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageHandler implements ConfigHolder {
    private final EligiusHiddenArmor plugin;
    private FileConfiguration langConfig;
    private String prefix = "";
    private String currentLang = "es";

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<gradient:(#[A-Fa-f0-9]{6}):(#[A-Fa-f0-9]{6})>(.*?)</gradient>");
    private static final Pattern MINIMESSAGE_TAG_PATTERN = Pattern.compile("<(/?)(black|dark_blue|dark_green|dark_aqua|dark_red|dark_purple|gold|gray|dark_gray|blue|green|aqua|red|light_purple|yellow|white|obfuscated|bold|strikethrough|underline|italic|reset)>");

    public MessageHandler(EligiusHiddenArmor plugin) {
        this.plugin = plugin;
        plugin.addConfigHolder(this);
        reloadLocales();
    }

    public void reloadLocales() {
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }
        String[] defaultLangs = {"de.yml", "en.yml", "es.yml", "fr.yml", "pt.yml", "ru.yml"};
        for (String lang : defaultLangs) {
            File langFile = new File(langDir, lang);
            if (!langFile.exists()) {
                try {
                    plugin.saveResource("lang/" + lang, false);
                } catch (IllegalArgumentException ignored) {
                    // Resource not found in jar yet
                }
            }
        }

        File langFile = new File(langDir, currentLang + ".yml");
        if (!langFile.exists()) {
            plugin.getLogger().warning("Language file " + currentLang + ".yml not found. Falling back to en.yml");
            langFile = new File(langDir, "en.yml");
        }
        
        if (langFile.exists()) {
            langConfig = YamlConfiguration.loadConfiguration(langFile);
        } else {
            langConfig = new YamlConfiguration();
        }
    }

    public void message(CommandSender sender, String messageKey) {
        message(sender, messageKey, true, null);
    }

    public void message(CommandSender sender, String messageKey, boolean usePrefix) {
        message(sender, messageKey, usePrefix, null);
    }

    public void message(CommandSender sender, String messageKey, boolean usePrefix, Map<String, String> placeholderMap) {
        String message = getFormattedMessage(messageKey, usePrefix, placeholderMap);
        if (message == null) return;
        
        sender.sendMessage(parseColors(message));
    }

    @SuppressWarnings("deprecation")
    public void messageActionBar(org.bukkit.entity.Player player, String messageKey, boolean usePrefix, Map<String, String> placeholderMap) {
        String message = getFormattedMessage(messageKey, usePrefix, placeholderMap);
        if (message == null) return;
        
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(parseColors(message)));
    }

    private String getFormattedMessage(String messageKey, boolean usePrefix, Map<String, String> placeholderMap) {
        String message = langConfig.getString(messageKey.replace("%", ""), "");
        if (message == null || message.trim().isEmpty()) {
            return null;
        }

        if (placeholderMap != null) {
            for (Map.Entry<String, String> entry : placeholderMap.entrySet()) {
                String val = langConfig.getString(entry.getValue().replace("%", ""), entry.getValue());
                message = message.replace("{" + entry.getKey() + "}", val);
            }
        }

        if (usePrefix && !prefix.isEmpty()) {
            message = prefix + message;
        }
        return message;
    }
    
    public String getLocalizedLegacyMessage(String messageKey) {
        String val = langConfig.getString(messageKey, "");
        return parseColors(val);
    }

    public static String parseColors(String message) {
        if (message == null) return null;
        
        // Remove gradient formatting simply by taking the text inside for Spigot compatibility
        // Alternatively, you could implement a full gradient generator here. For now, we strip gradients and apply basic color.
        Matcher gradientMatcher = GRADIENT_PATTERN.matcher(message);
        while (gradientMatcher.find()) {
            String hexStart = gradientMatcher.group(1);
            String text = gradientMatcher.group(3);
            message = message.replace(gradientMatcher.group(), "&" + hexStart + text);
        }

        // Replace MiniMessage tags with bukkit codes
        Matcher tagMatcher = MINIMESSAGE_TAG_PATTERN.matcher(message);
        StringBuffer sb = new StringBuffer();
        while (tagMatcher.find()) {
            boolean isClosing = tagMatcher.group(1).equals("/");
            if (isClosing) {
                tagMatcher.appendReplacement(sb, "&r");
                continue;
            }
            
            String tag = tagMatcher.group(2);
            String replacement = "";
            switch (tag) {
                case "black": replacement = "&0"; break;
                case "dark_blue": replacement = "&1"; break;
                case "dark_green": replacement = "&2"; break;
                case "dark_aqua": replacement = "&3"; break;
                case "dark_red": replacement = "&4"; break;
                case "dark_purple": replacement = "&5"; break;
                case "gold": replacement = "&6"; break;
                case "gray": replacement = "&7"; break;
                case "dark_gray": replacement = "&8"; break;
                case "blue": replacement = "&9"; break;
                case "green": replacement = "&a"; break;
                case "aqua": replacement = "&b"; break;
                case "red": replacement = "&c"; break;
                case "light_purple": replacement = "&d"; break;
                case "yellow": replacement = "&e"; break;
                case "white": replacement = "&f"; break;
                case "obfuscated": replacement = "&k"; break;
                case "bold": replacement = "&l"; break;
                case "strikethrough": replacement = "&m"; break;
                case "underline": replacement = "&n"; break;
                case "italic": replacement = "&o"; break;
                case "reset": replacement = "&r"; break;
            }
            tagMatcher.appendReplacement(sb, replacement);
        }
        tagMatcher.appendTail(sb);
        message = sb.toString();

        // Hex to Bungee
        Matcher hexMatcher = HEX_PATTERN.matcher(message);
        StringBuffer hexSb = new StringBuffer();
        while (hexMatcher.find()) {
            String hexColor = hexMatcher.group(1);
            hexMatcher.appendReplacement(hexSb, ChatColor.of("#" + hexColor).toString());
        }
        hexMatcher.appendTail(hexSb);
        message = hexSb.toString();

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @Override
    public void loadConfig(FileConfiguration config) {
        this.prefix = config.getString("prefix", "<dark_gray>[<gradient:#9b59b6:#8e44ad>EHiddenArmor</gradient>]</dark_gray> ");
        this.currentLang = config.getString("language", "es");
    }
}
