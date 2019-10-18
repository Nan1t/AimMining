package ru.nanit.mining.services;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ru.nanit.mining.data.DigBlock;
import ru.nanit.mining.data.DigResult;
import ru.nanit.mining.data.Digger;
import ru.nanit.mining.data.DropData;
import ru.nanit.mining.storage.Configuration;
import ru.nanit.mining.utils.Colors;

import java.util.*;

public class DiggingService {

    private Plugin plugin;

    private static Map<Material, DropData> dropData = new HashMap<>();

    private Map<UUID, Digger> diggers = new HashMap<>();
    private Set<Material> blackList = new HashSet<>();
    private Set<String> allowedWorlds;

    private float startSpeed;
    private float critStreakBonus;

    public static float CRIT_TEMP_BONUS;

    private String critMessage;
    private String critFailMessage;
    private String uselessToolMessage;

    public DiggingService(Plugin plugin, Configuration conf){
        this.plugin = plugin;
        this.startSpeed = conf.getFloat("mining", "startSpeed");
        this.critStreakBonus = conf.getFloat("mining", "critStreakBonus");
        this.allowedWorlds = conf.getStringSet("mining", "worlds");

        CRIT_TEMP_BONUS = conf.getFloat("mining", "critTempBonus");

        this.critMessage = Colors.of(conf.getString("messages", "critMessage"));
        this.critFailMessage = Colors.of(conf.getString("messages", "critFailMessage"));
        this.uselessToolMessage = Colors.of(conf.getString("messages", "uselessTool"));

        loadDropData(conf);
        loadBlockBlackList(conf);
    }

    public boolean hasAllowedWorld(String name){
        return allowedWorlds.contains(name);
    }

    public static DropData getDropData(Material material){
        return dropData.get(material);
    }

    public boolean isAllowedToDig(Material material){
        return !blackList.contains(material);
    }

    public void setNewBlock(Digger player, Block block, BlockFace face){
        DigBlock digBlock = player.getCurrentBlock();

        if (digBlock != null){
            if(digBlock.getBlock().equals(block)){
                digBlock.start(plugin);
                return;
            }

            digBlock.stop();
        }

        DigBlock newBlock = new DigBlock(player);
        newBlock.setBlockLoc(block, face);
        newBlock.start(plugin);

        player.setDigBlock(newBlock);
    }

    public void digBlock(DigBlock block){
        DigResult result = block.damageBlock();
        Digger player = block.getPlayer();

        switch (result){
            default:
                break;
            case BROKEN:
                if(block.isCritEnabled()){
                    player.addCritStreak();
                    player.addDigSpeedPercent(critStreakBonus);
                    sendActionBarMessage(player.getPlayer(), critMessage, "count", player.getCritStreak(), "speed", player.getDigSpeedPercent());
                } else{
                    player.setCritStreak(0);
                    player.setDigSpeedPercent(startSpeed);
                    sendActionBarMessage(player.getPlayer(), critFailMessage, "count", player.getCritStreak(), "speed", player.getDigSpeedPercent());
                }
                break;
            case USELESS_TOOL:
                sendActionBarMessage(player.getPlayer(), uselessToolMessage, "count", player.getCritStreak(), "speed", player.getDigSpeedPercent());
                break;
        }
    }

    private void sendActionBarMessage(Player player, String msg, Object... data){
        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < data.length; i += 2){
            String key = data[i].toString();
            String value = data[i+1].toString();
            map.putIfAbsent(key, value);
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(StrSubstitutor.replace(msg, map)));
    }

    private void loadDropData(Configuration conf){
        dropData.clear();

        ConfigurationSection section = conf.getConf().getConfigurationSection("drop");
        Set<String> keys = section.getKeys(false);

        for(String key : keys){
            ConfigurationSection data = section.getConfigurationSection(key);
            Material mat = Material.getMaterial(key);
            Material drop = Material.getMaterial(data.getString("drop"));
            Material replace = Material.AIR;
            int amount = data.getInt("amount");

            if(data.getString("replaceBlock") != null){
                replace = Material.getMaterial(data.getString("replaceBlock"));
            }

            dropData.put(mat, new DropData(drop, replace, amount));
        }
    }

    private void loadBlockBlackList(Configuration conf){
        List<String> list = conf.getConf().getStringList("blockBlackList");

        for(String s : list){
            blackList.add(Material.getMaterial(s));
        }
    }

    public Digger getPlayer(UUID uuid){
        return diggers.get(uuid);
    }

    public Digger loadPlayer(Player template){
        Digger player = getPlayer(template.getUniqueId());

        if(player == null){
            return createPlayer(template);
        }

        player.setPlayer(template);
        return player;
    }

    private Digger createPlayer(Player template){
        Digger player = new Digger(template);
        player.setDigSpeedPercent(startSpeed);
        diggers.put(template.getUniqueId(), player);
        return player;
    }

}
