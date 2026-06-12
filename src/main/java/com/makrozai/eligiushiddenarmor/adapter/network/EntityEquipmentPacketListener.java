package com.makrozai.eligiushiddenarmor.adapter.network;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;

import com.makrozai.eligiushiddenarmor.EligiusHiddenArmor;
import com.makrozai.eligiushiddenarmor.domain.model.ArmorPiece;
import com.makrozai.eligiushiddenarmor.util.ConfigHolder;
import com.makrozai.eligiushiddenarmor.domain.service.ArmorHideService;
import com.makrozai.eligiushiddenarmor.util.ItemUtil;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EntityEquipmentPacketListener extends PacketListenerAbstract implements ConfigHolder {
    private final ArmorHideService EligiusHiddenArmorManager;
    private final EligiusHiddenArmor plugin;

    private boolean ignoreLeatherArmor;
    private boolean ignoreTurtleHelmet;

    public EntityEquipmentPacketListener(EligiusHiddenArmor plugin) {
        plugin.addConfigHolder(this);
        this.plugin = plugin;
        this.EligiusHiddenArmorManager = plugin.getArmorHideService();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_EQUIPMENT) return;

        Player player = (Player) event.getPlayer();
        if (player == null) return;

        WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment(event);
        int entityId = wrapper.getEntityId();
        
        Player packetPlayer = plugin.getPlayerCache().getPlayer(entityId);
        if (packetPlayer == null) return;

        List<com.github.retrooper.packetevents.protocol.player.Equipment> equipmentList = wrapper.getEquipment();

        boolean isSelf = player.getEntityId() == packetPlayer.getEntityId();

        for (com.github.retrooper.packetevents.protocol.player.Equipment equipment : equipmentList) {
            com.github.retrooper.packetevents.protocol.player.EquipmentSlot slot = equipment.getSlot();
            if (slot == com.github.retrooper.packetevents.protocol.player.EquipmentSlot.MAIN_HAND || slot == com.github.retrooper.packetevents.protocol.player.EquipmentSlot.OFF_HAND) {
                continue;
            }

            ArmorPiece piece = ArmorPiece.fromEquipmentSlot(slot);
            if (piece == null || !EligiusHiddenArmorManager.isPieceHidden(packetPlayer, piece)) {
                continue;
            }

            com.github.retrooper.packetevents.protocol.item.ItemStack peItem = equipment.getItem();
            if (peItem == null || peItem.isEmpty()) continue;
            
            ItemStack bukkitItem = SpigotConversionUtil.toBukkitItemStack(peItem);
            
            if (!shouldIgnore(bukkitItem)) {
                if (isSelf) {
                    ItemStack placeholder = plugin.getArmorPlaceholderHandler().buildItemPlaceholder(bukkitItem, player);
                    if (placeholder != null && placeholder.getType() != Material.AIR) {
                        com.github.retrooper.packetevents.protocol.item.ItemStack pePlaceholder = SpigotConversionUtil.fromBukkitItemStack(placeholder);
                        com.makrozai.eligiushiddenarmor.util.PacketComponentUtil.injectTranslatedName(pePlaceholder, bukkitItem);
                        equipment.setItem(pePlaceholder);
                    } else {
                        equipment.setItem(com.github.retrooper.packetevents.protocol.item.ItemStack.EMPTY);
                    }
                } else {
                    equipment.setItem(com.github.retrooper.packetevents.protocol.item.ItemStack.EMPTY);
                }
            }
        }
        wrapper.setEquipment(equipmentList);
    }

    private boolean shouldIgnore(ItemStack itemStack) {
        Material material = itemStack.getType();

        if (material == Material.ELYTRA) return true;

        return (ignoreLeatherArmor && material.toString().startsWith("LEATHER")) ||
                (ignoreTurtleHelmet && material == Material.TURTLE_HELMET) ||
                (!ItemUtil.isArmor(itemStack));
    }

    @Override
    public void loadConfig(FileConfiguration config) {
        this.ignoreLeatherArmor = config.getBoolean("ignore.leather-armor");
        this.ignoreTurtleHelmet = config.getBoolean("ignore.turtle-helmet");
    }
}
