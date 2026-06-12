package com.makrozai.eligiushiddenarmor.util;

import com.github.retrooper.packetevents.protocol.component.ComponentType;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PacketComponentUtil {

    private static final Logger LOGGER = Logger.getLogger("EligiusHiddenArmor-Reflection");
    private static Class<?> adventureSerializerClass;
    private static Method parseComponentMethod;
    private static Method setComponentMethod;

    static {
        try {
            adventureSerializerClass = Class.forName("com.github.retrooper.packetevents.util.adventure.AdventureSerializer");
            parseComponentMethod = adventureSerializerClass.getMethod("parseComponent", String.class);
            setComponentMethod = ItemStack.class.getMethod("setComponent", ComponentType.class, Object.class);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not initialize PacketEvents Adventure reflection. Custom item names might not be displayed properly.", e);
        }
    }

    public static void setItemName(ItemStack peItem, Component component) {
        if (parseComponentMethod == null || setComponentMethod == null) return;
        try {
            // Serialize our shadowed Kyori Component to JSON
            String json = GsonComponentSerializer.gson().serialize(component);
            // Parse the JSON into PacketEvents' internal Kyori Component using reflection
            Object peComponent = parseComponentMethod.invoke(null, json);
            // Inject the component into the PacketEvents ItemStack using reflection
            setComponentMethod.invoke(peItem, ComponentTypes.ITEM_NAME, peComponent);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to inject custom item name into PacketEvents item", e);
        }
    }

    public static void injectTranslatedName(ItemStack pePlaceholder, org.bukkit.inventory.ItemStack bukkitItem) {
        net.kyori.adventure.text.Component translationComp = net.kyori.adventure.text.Component.translatable(("item.minecraft." + bukkitItem.getType().getKey().getKey())).color(net.kyori.adventure.text.format.NamedTextColor.GRAY);
        if (bukkitItem.getItemMeta() != null && bukkitItem.getItemMeta().hasDisplayName()) {
            net.kyori.adventure.text.Component customName = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize(bukkitItem.getItemMeta().getDisplayName());
            translationComp = customName.append(net.kyori.adventure.text.Component.text(" &8(").color(net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY))
                                        .append(net.kyori.adventure.text.Component.translatable(("item.minecraft." + bukkitItem.getType().getKey().getKey())).color(net.kyori.adventure.text.format.NamedTextColor.GRAY))
                                        .append(net.kyori.adventure.text.Component.text("&8)").color(net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY));
        }
        setItemName(pePlaceholder, translationComp);
    }
}
