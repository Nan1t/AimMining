package ru.nanit.mining;

import org.bukkit.plugin.java.JavaPlugin;
import ru.nanit.mining.listeners.PlayerDiggingListener;
import ru.nanit.mining.listeners.PlayerJoinListener;
import ru.nanit.mining.listeners.PlayerPotionListener;
import ru.nanit.mining.services.DiggingService;
import ru.nanit.mining.storage.Configuration;
import ru.nanit.mining.utils.BlockHelper;

public class AimMining extends JavaPlugin {

    public void onEnable(){
        try{
            Configuration conf = new Configuration(this, "config.yml");
            DiggingService miningService = new DiggingService(this, conf);

            BlockHelper.setDropItem(conf.getConf().getBoolean("dropItem"));

            getServer().getPluginManager().registerEvents(new PlayerJoinListener(miningService), this);
            getServer().getPluginManager().registerEvents(new PlayerDiggingListener(miningService), this);
            getServer().getPluginManager().registerEvents(new PlayerPotionListener(this, miningService), this);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
