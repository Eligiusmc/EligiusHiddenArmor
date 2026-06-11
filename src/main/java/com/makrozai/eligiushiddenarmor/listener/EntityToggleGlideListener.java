package com.makrozai.eligiushiddenarmor.listener;

import com.makrozai.eligiushiddenarmor.EligiusHiddenArmor;
import com.makrozai.eligiushiddenarmor.handler.ArmorUpdateHandler;
import com.makrozai.eligiushiddenarmor.domain.service.ArmorHideService;
import com.makrozai.eligiushiddenarmor.util.EventUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

public class EntityToggleGlideListener implements Listener {
    EligiusHiddenArmor plugin;
    ArmorHideService ArmorHideService;
    ArmorUpdateHandler armorUpdater;

    public EntityToggleGlideListener(EligiusHiddenArmor plugin){
        EventUtil.register(this, plugin);

        this.plugin = plugin;
        this.ArmorHideService = plugin.getArmorHideService();
        this.armorUpdater = plugin.getArmorUpdater();
    }

    @EventHandler
    public void onPlayerToggleGlide(EntityToggleGlideEvent e){
        if(!(e.getEntity() instanceof Player)) return;

        Player player = (Player) e.getEntity();
        if(ArmorHideService.isArmorVisible(player)) return;

        plugin.getPlatformPort().runTaskLater(() -> {
            armorUpdater.updatePlayer(player);
        }, 1L);
    }
}
