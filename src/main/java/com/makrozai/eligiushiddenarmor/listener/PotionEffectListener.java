package com.makrozai.eligiushiddenarmor.listener;

import com.makrozai.eligiushiddenarmor.EligiusHiddenArmor;
import com.makrozai.eligiushiddenarmor.handler.ArmorUpdateHandler;
import com.makrozai.eligiushiddenarmor.domain.service.ArmorHideService;
import com.makrozai.eligiushiddenarmor.util.EventUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;

public class PotionEffectListener implements Listener {
    EligiusHiddenArmor plugin;
    ArmorHideService EligiusHiddenArmorManager;
    ArmorUpdateHandler armorUpdater;

    public PotionEffectListener(EligiusHiddenArmor plugin) {
        EventUtil.register(this, plugin);

        this.plugin = plugin;
        this.EligiusHiddenArmorManager = plugin.getArmorHideService();
        this.armorUpdater = plugin.getArmorUpdater();
    }

    @EventHandler
    public void onPlayerInvisibleEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        plugin.getPlatformPort().runTaskLater(() -> {
            armorUpdater.updatePlayer(player);
        }, 2L);
    }
}
