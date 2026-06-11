package com.makrozai.eligiushiddenarmor.adapter.network;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;

import com.makrozai.eligiushiddenarmor.EligiusHiddenArmor;
import com.makrozai.eligiushiddenarmor.handler.ArmorPlaceholderHandler;
import com.makrozai.eligiushiddenarmor.domain.service.ArmorHideService;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class SetSlotPacketListener extends PacketListenerAbstract {
    private final ArmorHideService ArmorHideService;
    private final ArmorPlaceholderHandler placeholderHandler;
    private final EligiusHiddenArmor plugin;

    public SetSlotPacketListener(EligiusHiddenArmor plugin) {
        this.ArmorHideService = plugin.getArmorHideService();
        this.placeholderHandler = plugin.getArmorPlaceholderHandler();
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.SET_SLOT) return;

        Player player = (Player) event.getPlayer();
        if (player == null) return;

        WrapperPlayServerSetSlot wrapper = new WrapperPlayServerSetSlot(event);
        if (wrapper.getWindowId() != 0) return;

        int slotNumber = wrapper.getSlot();
        if (slotNumber < 5 || slotNumber > 8) return;

        if (!ArmorHideService.isSlotHidden(player, slotNumber)) return;

        com.github.retrooper.packetevents.protocol.item.ItemStack peItem = wrapper.getItem();
        if (peItem != null && !peItem.isEmpty()) {
            ItemStack bukkitItem = SpigotConversionUtil.toBukkitItemStack(peItem);
            if (bukkitItem.getType() == Material.ELYTRA) return;
            
            ItemStack placeholder = placeholderHandler.buildItemPlaceholder(bukkitItem, player);
            
            if (placeholder == null || placeholder.getType() == Material.AIR) {
                wrapper.setItem(com.github.retrooper.packetevents.protocol.item.ItemStack.EMPTY);
            } else {
                com.github.retrooper.packetevents.protocol.item.ItemStack pePlaceholder = SpigotConversionUtil.fromBukkitItemStack(placeholder);
                net.kyori.adventure.text.Component translationComp = net.kyori.adventure.text.Component.translatable(("item.minecraft." + bukkitItem.getType().getKey().getKey())).color(net.kyori.adventure.text.format.NamedTextColor.GRAY);
                
                if (bukkitItem.getItemMeta() != null && bukkitItem.getItemMeta().hasDisplayName()) {
                    net.kyori.adventure.text.Component customName = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize(bukkitItem.getItemMeta().getDisplayName());
                    translationComp = customName.append(net.kyori.adventure.text.Component.text(" &8(").color(net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY))
                                                .append(net.kyori.adventure.text.Component.translatable(("item.minecraft." + bukkitItem.getType().getKey().getKey())).color(net.kyori.adventure.text.format.NamedTextColor.GRAY))
                                                .append(net.kyori.adventure.text.Component.text("&8)").color(net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY));
                }
                pePlaceholder.setComponent(com.github.retrooper.packetevents.protocol.component.ComponentTypes.ITEM_NAME, translationComp);
                wrapper.setItem(pePlaceholder);
            }
        }
    }
}
