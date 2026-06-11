package com.makrozai.eligiushiddenarmor.command;

import com.makrozai.eligiushiddenarmor.EligiusHiddenArmor;
import com.makrozai.eligiushiddenarmor.util.ConfigHolder;
import com.makrozai.eligiushiddenarmor.util.PermissionUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class EligiusHiddenArmorTabCompleter implements TabCompleter, ConfigHolder {
    private boolean defaultPermissionToggle;

    public EligiusHiddenArmorTabCompleter(EligiusHiddenArmor plugin) {
        plugin.addConfigHolder(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("eligiushiddenarmor")) return null;
        
        List<String> options = new ArrayList<>();
        
        if (args.length == 1) {
            if(PermissionUtil.canUse(sender ,"eligiushiddenarmor.toggle") || defaultPermissionToggle) {
                options.add("toggle");
                options.add("hide");
                options.add("show");
            }
            if(PermissionUtil.canUse(sender, "eligiushiddenarmor.reload")) {
                options.add("reload");
            }
            options.add("help");
            return filterPrefix(options, args[0]);
        }
        
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("toggle") || sub.equals("hide") || sub.equals("show")) {
                options.add("all");
                options.add("helmet");
                options.add("chestplate");
                options.add("leggings");
                options.add("boots");
                
                // Add players if they have permission
                if (PermissionUtil.canUse(sender, "eligiushiddenarmor.toggle.other")) {
                    for (org.bukkit.entity.Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
                        options.add(p.getName());
                    }
                }
                return filterPrefix(options, args[1]);
            }
        }
        
        if (args.length == 3) {
            String sub = args[0].toLowerCase();
            if (sub.equals("toggle") || sub.equals("hide") || sub.equals("show")) {
                if (PermissionUtil.canUse(sender, "eligiushiddenarmor.toggle.other")) {
                    for (org.bukkit.entity.Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
                        options.add(p.getName());
                    }
                }
                return filterPrefix(options, args[2]);
            }
        }

        return null;
    }

    private List<String> filterPrefix(List<String> list, String prefix) {
        if (prefix == null || prefix.isEmpty()) return list;
        List<String> result = new ArrayList<>();
        for (String s : list) {
            if (s.toLowerCase().startsWith(prefix.toLowerCase())) {
                result.add(s);
            }
        }
        return result;
    }

    @Override
    public void loadConfig(FileConfiguration config) {
        this.defaultPermissionToggle = config.getBoolean("default-permissions.toggle");
    }
}
