package com.makrozai.eligiushiddenarmor.listener;

import com.makrozai.eligiushiddenarmor.EligiusHiddenArmor;
import com.makrozai.eligiushiddenarmor.handler.ArmorUpdateHandler;
import com.makrozai.eligiushiddenarmor.domain.service.ArmorHideService;
import com.makrozai.eligiushiddenarmor.util.EventUtil;
import com.makrozai.eligiushiddenarmor.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryShiftClickListener implements Listener {
    private final EligiusHiddenArmor plugin;
    private final ArmorHideService EligiusHiddenArmorManager;
    private final ArmorUpdateHandler armorUpdater;

    public InventoryShiftClickListener(EligiusHiddenArmor plugin){
        EventUtil.register(this, plugin);

        this.plugin = plugin;
        this.EligiusHiddenArmorManager = plugin.getArmorHideService();
        this.armorUpdater = plugin.getArmorUpdater();
    }

    @EventHandler
    public void onArmorClick(InventoryClickEvent event){
        if(EligiusHiddenArmorManager.isArmorVisible((Player) event.getWhoClicked())) return;

        Player player = (Player) event.getWhoClicked();

        boolean shouldUpdate = false;
        
        if (event.getSlotType() == org.bukkit.event.inventory.InventoryType.SlotType.ARMOR) {
            shouldUpdate = true;
        } else if (event.isShiftClick()) {
            ItemStack item = event.getCurrentItem();
            if (item != null && (ItemUtil.isArmor(item) || item.getType() == Material.ELYTRA)) {
                shouldUpdate = true;
            }
        }
        
        if (shouldUpdate) {
            plugin.getPlatformPort().runTaskLater(() -> {
                armorUpdater.updateSelf(player);
            }, 1L);
        }
    }
}
