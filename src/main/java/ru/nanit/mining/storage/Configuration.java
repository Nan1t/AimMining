package ru.nanit.mining.storage;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Configuration {

    private Plugin plugin;
    private String assetName;
    private YamlConfiguration config;
    private File configDir;
    private File file;

    public YamlConfiguration getConf() {
        return config;
    }

    public Configuration(Plugin plugin, String assetName) {
        this.plugin = plugin;
        this.assetName = assetName;
        this.configDir = plugin.getDataFolder();

        reload();
    }

    public Configuration(Plugin plugin, String assetName, File folder){
        this.plugin = plugin;
        this.assetName = assetName;
        this.configDir = folder;

        reload();
    }

    public void reload() {
        this.file = new File(configDir, assetName);

        if(!configDir.exists()) {
            configDir.mkdir();
        }

        if(!file.exists()) {
            plugin.saveResource(assetName, false);
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getString(String... path){
        return getConf().getString(join(path));
    }

    public int getInt(String... path){
        return getConf().getInt(join(path));
    }

    public double getDouble(String... path){
        return getConf().getDouble(join(path));
    }

    public float getFloat(String... path){
        return (float) getConf().getDouble(join(path));
    }

    public List<String> getStringList(String... path){
        return getConf().getStringList(join(path));
    }

    public Set<String> getStringSet(String... path){
        return new HashSet<>(getConf().getStringList(join(path)));
    }

    public List<Integer> getIntegerList(String... path){
        return getConf().getIntegerList(join(path));
    }

    private String join(String... path){
        return String.join(".", path);
    }
}
