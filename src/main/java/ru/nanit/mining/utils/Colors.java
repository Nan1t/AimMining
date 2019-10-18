package ru.nanit.mining.utils;

import org.bukkit.ChatColor;

public class Colors {

    public static String of(String str){
        return ChatColor.translateAlternateColorCodes('&', str);
    }

}
