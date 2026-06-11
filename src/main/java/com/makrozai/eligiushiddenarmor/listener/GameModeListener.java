package com.makrozai.eligiushiddenarmor.listener;

import com.makrozai.eligiushiddenarmor.EligiusHiddenArmor;
import com.makrozai.eligiushiddenarmor.domain.service.ArmorHideService;
import com.makrozai.eligiushiddenarmor.util.EventUtil;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class GameModeListener implements Listener {
    EligiusHiddenArmor plugin;
    ArmorHideService EligiusHiddenArmorManager;

    public GameModeListener(EligiusHiddenArmor plugin){
        EventUtil.register(this, plugin);

        this.plugin = plugin;
        this.EligiusHiddenArmorManager = plugin.getArmorHideService();
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event){
        if(!EligiusHiddenArmorManager.isEnabled(event.getPlayer())) return;
        if(event.getNewGameMode() == GameMode.CREATIVE) {
            EligiusHiddenArmorManager.disablePlayer(event.getPlayer(), false);
            plugin.getMessageHandler().message(event.getPlayer(), "&cTu armadura se ha vuelto visible porque has entrado al Modo Creativo.");
            plugin.getArmorUpdater().updatePlayer(event.getPlayer());
        } else {
            plugin.getPlatformPort().runTaskLater(() -> {
                plugin.getArmorUpdater().updatePlayer(event.getPlayer());
            }, 1L);
        }
    }


}
