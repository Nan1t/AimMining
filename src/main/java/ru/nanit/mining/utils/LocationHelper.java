package ru.nanit.mining.utils;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.Random;

public class LocationHelper {

    private static Random random = new Random();

    public static Location getRandParticleLoc(Location block, BlockFace face){
        Location loc = getBlockCenter(block.clone());

        switch (face){
            default:
                break;
            case UP:
                loc.add(getRand(), 0.6, getRand());
                break;
            case DOWN:
                loc.add(getRand(), -0.6, getRand());
                break;
            case EAST:
                loc.add(0.6, getRand(), getRand());
                break;
            case WEST:
                loc.add(-0.6, getRand(), getRand());
                break;
            case SOUTH:
                loc.add(getRand(), getRand(), 0.6);
                break;
            case NORTH:
                loc.add(getRand(), getRand(), -0.6);
                break;
        }

        return loc;
    }

    public static boolean equalsLocation(Location loc1, Location loc2){
        if(loc1 == null || loc2 == null){
            return false;
        }
        return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
    }

    public static Location getBlockCenter(Location loc){
        return loc.add(0.5, 0.5, 0.5);
    }

    private static double getRand(){
        return Math.random() / ((random.nextBoolean()) ? 2.5 : -2.5);
    }

}
