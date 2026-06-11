package com.makrozai.eligiushiddenarmor.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TranslationHelper {

    @SuppressWarnings("deprecation")
    public static String getTranslatedComponentJson(ItemStack originalItem) {
        String translationKey = originalItem.translationKey();
        
        Component translatable = Component.translatable(translationKey).color(NamedTextColor.GRAY);
        
        ItemMeta meta = originalItem.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            // Reconstruct the format: CustomName &8(TranslatedName&8)
            String oldName = meta.getDisplayName(); // Legacy string color
            Component finalComponent = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize(oldName)
                    .append(Component.text(" ").color(NamedTextColor.DARK_GRAY))
                    .append(Component.text("(").color(NamedTextColor.DARK_GRAY))
                    .append(translatable)
                    .append(Component.text(")").color(NamedTextColor.DARK_GRAY));
            return GsonComponentSerializer.gson().serialize(finalComponent);
        }
        
        return GsonComponentSerializer.gson().serialize(translatable);
    }
}
