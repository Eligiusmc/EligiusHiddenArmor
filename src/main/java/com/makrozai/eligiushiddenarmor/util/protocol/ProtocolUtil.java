package com.makrozai.eligiushiddenarmor.util.protocol;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ProtocolUtil {

    public static void broadcastPlayerPacket(PacketWrapper<?> packet, Player player) {
        double maxDistSq = Math.pow(Bukkit.getViewDistance() * 16, 2);
        for(Player p : Bukkit.getOnlinePlayers()){
            if(!(p.getWorld().equals(player.getWorld()) && p.getLocation().distanceSquared(player.getLocation()) < maxDistSq)) continue;
            PacketEvents.getAPI().getPlayerManager().sendPacket(p, packet);
        }
    }

    public enum ArmorType {
        HELMET(5, EquipmentSlot.HELMET), 
        CHEST(6, EquipmentSlot.CHEST_PLATE), 
        LEGGS(7, EquipmentSlot.LEGGINGS), 
        BOOTS(8, EquipmentSlot.BOOTS);

        private final int slotId;
        private final EquipmentSlot equipmentSlot;

        ArmorType(int slotId, EquipmentSlot equipmentSlot){
            this.slotId = slotId;
            this.equipmentSlot = equipmentSlot;
        }

        public static ArmorType getType(int slotId){
            for(ArmorType type : values()){
                if(type.getSlotId() == slotId) return type;
            }
            return null;
        }

        public int getSlotId(){
            return slotId;
        }

        public EquipmentSlot getEquipmentSlot() {
            return equipmentSlot;
        }
    }

    public static ItemStack getArmor(ArmorType type, PlayerInventory inv) {
        if (type == null) return null;
        switch (type) {
            case HELMET: if(inv.getHelmet()!=null && inv.getHelmet().getType() != Material.AIR) return inv.getHelmet().clone();
                break;
            case CHEST: if(inv.getChestplate()!=null && inv.getChestplate().getType() != Material.AIR) return inv.getChestplate().clone();
                break;
            case LEGGS: if(inv.getLeggings()!=null && inv.getLeggings().getType() != Material.AIR) return inv.getLeggings().clone();
                break;
            case BOOTS: if(inv.getBoots()!=null && inv.getBoots().getType() != Material.AIR) return inv.getBoots().clone();
                break;
        }
        return null;
    }
}
