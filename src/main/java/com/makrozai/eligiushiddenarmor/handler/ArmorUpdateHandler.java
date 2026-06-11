package com.makrozai.eligiushiddenarmor.handler;

import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;

import com.makrozai.eligiushiddenarmor.EligiusHiddenArmor;
import com.makrozai.eligiushiddenarmor.util.protocol.ProtocolUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class ArmorUpdateHandler {

    public ArmorUpdateHandler(EligiusHiddenArmor plugin) {
    }

    public void updatePlayer(Player player) {
        updateSelf(player);
        updateEquipmentPackets(player);
    }

    public void updateSelf(Player player) {
        player.updateInventory();
    }

    public void updateEquipmentPackets(Player player) {
        PlayerInventory inv = player.getInventory();
        List<Equipment> equipmentList = new ArrayList<>();
        
        equipmentList.add(new Equipment(com.github.retrooper.packetevents.protocol.player.EquipmentSlot.HELMET, SpigotConversionUtil.fromBukkitItemStack(ProtocolUtil.getArmor(ProtocolUtil.ArmorType.HELMET, inv))));
        equipmentList.add(new Equipment(com.github.retrooper.packetevents.protocol.player.EquipmentSlot.CHEST_PLATE, SpigotConversionUtil.fromBukkitItemStack(ProtocolUtil.getArmor(ProtocolUtil.ArmorType.CHEST, inv))));
        equipmentList.add(new Equipment(com.github.retrooper.packetevents.protocol.player.EquipmentSlot.LEGGINGS, SpigotConversionUtil.fromBukkitItemStack(ProtocolUtil.getArmor(ProtocolUtil.ArmorType.LEGGS, inv))));
        equipmentList.add(new Equipment(com.github.retrooper.packetevents.protocol.player.EquipmentSlot.BOOTS, SpigotConversionUtil.fromBukkitItemStack(ProtocolUtil.getArmor(ProtocolUtil.ArmorType.BOOTS, inv))));
        equipmentList.add(new Equipment(com.github.retrooper.packetevents.protocol.player.EquipmentSlot.MAIN_HAND, SpigotConversionUtil.fromBukkitItemStack(inv.getItemInMainHand().clone())));
        equipmentList.add(new Equipment(com.github.retrooper.packetevents.protocol.player.EquipmentSlot.OFF_HAND, SpigotConversionUtil.fromBukkitItemStack(inv.getItemInOffHand().clone())));

        WrapperPlayServerEntityEquipment packetOthers = new WrapperPlayServerEntityEquipment(player.getEntityId(), equipmentList);
        ProtocolUtil.broadcastPlayerPacket(packetOthers, player);
    }
}
