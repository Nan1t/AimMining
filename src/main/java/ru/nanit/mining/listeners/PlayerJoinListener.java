package ru.nanit.mining.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffectType;
import ru.nanit.mining.data.Digger;
import ru.nanit.mining.services.DiggingService;

public class PlayerJoinListener implements Listener {

    private DiggingService service;

    public PlayerJoinListener(DiggingService service){
        this.service = service;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Digger player = service.loadPlayer(event.getPlayer());

        if(service.hasAllowedWorld(event.getPlayer().getWorld().getName())){
            player.setMiningFatique();
            return;
        }

        event.getPlayer().removePotionEffect(PotionEffectType.SLOW_DIGGING);
    }

    @EventHandler
    public void onJoinWorld(PlayerChangedWorldEvent event){
        if(service.hasAllowedWorld(event.getPlayer().getWorld().getName())){
            Digger player = service.getPlayer(event.getPlayer().getUniqueId());
            player.setMiningFatique();
            return;
        }

        event.getPlayer().removePotionEffect(PotionEffectType.SLOW_DIGGING);
    }

}
