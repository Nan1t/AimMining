package ru.nanit.mining.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;

import org.bukkit.event.player.PlayerInteractEvent;
import ru.nanit.mining.data.DigBlock;
import ru.nanit.mining.data.Digger;
import ru.nanit.mining.services.DiggingService;

import java.util.HashSet;
import java.util.Set;

public class PlayerDiggingListener implements Listener {

    private DiggingService miningService;
    private Set<Material> transparentBlocks = new HashSet<>();

    public PlayerDiggingListener(DiggingService miningService){
        this.miningService = miningService;

        transparentBlocks.add(Material.WATER);
        transparentBlocks.add(Material.AIR);
        transparentBlocks.add(Material.LAVA);
    }

    @EventHandler
    public void onBlockClick(PlayerInteractEvent event){
        if(miningService.hasAllowedWorld(event.getPlayer().getWorld().getName())){
            if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)){
                if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
                    if(miningService.isAllowedToDig(event.getClickedBlock().getType())){
                        Digger player = miningService.getPlayer(event.getPlayer().getUniqueId());
                        miningService.setNewBlock(player, event.getClickedBlock(), event.getBlockFace());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onAnimation(PlayerAnimationEvent event){
        if(miningService.hasAllowedWorld(event.getPlayer().getWorld().getName())) {
            if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                Digger player = miningService.getPlayer(event.getPlayer().getUniqueId());
                DigBlock block = player.getCurrentBlock();

                if (block != null && !block.isBroken()) {
                    if (block.getBlock().equals(event.getPlayer().getTargetBlock(transparentBlocks, 5))) {
                        if(miningService.isAllowedToDig(block.getBlock().getType())){
                            miningService.digBlock(block);
                        }
                    }
                }
            }
        }
    }

}
