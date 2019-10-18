package ru.nanit.mining.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.nanit.mining.data.Digger;
import ru.nanit.mining.services.DiggingService;

public class PlayerPotionListener implements Listener {

    private Plugin plugin;
    private DiggingService miningService;

    public PlayerPotionListener(Plugin plugin, DiggingService miningService){
        this.plugin = plugin;
        this.miningService = miningService;
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event){
        if(miningService.hasAllowedWorld(event.getPlayer().getWorld().getName())){
            if(event.getItem().getType().equals(Material.MILK_BUCKET)){
                Digger player = miningService.getPlayer(event.getPlayer().getUniqueId());

                if(player != null){
                    new BukkitRunnable(){
                        public void run(){
                            player.setMiningFatique();
                        }
                    }.runTaskLater(plugin, 5);
                }
            }
        }
    }

}
