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

    public SetSlotPacketListener(EligiusHiddenArmor plugin) {
        this.ArmorHideService = plugin.getArmorHideService();
        this.placeholderHandler = plugin.getArmorPlaceholderHandler();
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
                com.makrozai.eligiushiddenarmor.util.PacketComponentUtil.injectTranslatedName(pePlaceholder, bukkitItem);
                wrapper.setItem(pePlaceholder);
            }
        }
    }
}
