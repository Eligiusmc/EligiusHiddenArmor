package com.makrozai.eligiushiddenarmor.handler;

import com.makrozai.eligiushiddenarmor.EligiusHiddenArmor;
import com.makrozai.eligiushiddenarmor.util.ConfigHolder;
import com.makrozai.eligiushiddenarmor.util.ItemUtil;
import com.makrozai.eligiushiddenarmor.util.StrUtil;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArmorPlaceholderHandler implements ConfigHolder {
    private final EligiusHiddenArmor plugin;

    public ArmorPlaceholderHandler(EligiusHiddenArmor plugin) {
        plugin.addConfigHolder(this);
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    public ItemStack buildItemPlaceholder(ItemStack itemStack, org.bukkit.entity.Player player) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return null;
        if (!ItemUtil.isArmor(itemStack)) return null;

        Material placeholderMaterial = Material.POLISHED_BLACKSTONE_BUTTON;
        
        ItemStack placeholder = new ItemStack(placeholderMaterial);
        ItemMeta newItemMeta = plugin.getServer().getItemFactory().getItemMeta(placeholderMaterial);
        
        if (newItemMeta != null) {
            ItemMeta oldItemMeta = itemStack.getItemMeta();
            if (oldItemMeta != null) {
                Map<Enchantment, Integer> enchantments = oldItemMeta.getEnchants();
                for (Enchantment key : enchantments.keySet()) {
                    newItemMeta.addEnchant(key, enchantments.get(key), true);
                }
            }

            if (oldItemMeta != null && oldItemMeta.hasDisplayName()) {
                newItemMeta.setDisplayName(oldItemMeta.getDisplayName());
            }

            List<String> lore = new ArrayList<>();
            if (oldItemMeta != null && oldItemMeta.hasLore()) {
                List<String> oldLore = oldItemMeta.getLore();
                if (oldLore != null) {
                    lore.addAll(oldLore);
                }
            }
            
            String durability = buildDurabilityText(itemStack);
            if (durability != null) lore.add(durability);
            
            newItemMeta.setLore(lore);
            placeholder.setItemMeta(newItemMeta);
        }

        return placeholder;
    }

    private String buildDurabilityText(ItemStack itemStack){
        int percentage = ItemUtil.getDurabilityPercentage(itemStack);
        if(percentage != -1){
            String color = "&e";
            if(percentage>=70) color = "&a";
            if(percentage<30) color = "&c";
            
            String localizedText = plugin.getMessageHandler().getLocalizedLegacyMessage("item_durability");
            if (localizedText == null) {
                localizedText = "&fDurability: %color%%percentage%%";
            }
            localizedText = localizedText.replace("%color%", color).replace("%percentage%", String.valueOf(percentage));
            
            return StrUtil.color(localizedText);
        }
        return null;
    }

    @Override
    public void loadConfig(FileConfiguration config) {
        // Any config values for placeholders can be added here
    }
}
