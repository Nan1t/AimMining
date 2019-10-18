package ru.nanit.mining.utils;

import net.minecraft.server.v1_12_R1.*;
import net.minecraft.server.v1_12_R1.SoundCategory;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import ru.nanit.mining.data.DropData;
import ru.nanit.mining.services.DiggingService;

import java.util.Arrays;
import java.util.Collection;

public class BlockHelper {

    private static boolean dropItem = false;

    public static void setDropItem(boolean value){
        dropItem = value;
    }

    public static void sendBreakAnimation(Block block, int animation){
        DedicatedPlayerList list = ((CraftServer) Bukkit.getServer()).getHandle();
        int dimension = ((CraftWorld) block.getWorld()).getHandle().dimension;
        PacketPlayOutBlockBreakAnimation animPacket = new PacketPlayOutBlockBreakAnimation(getBlockEntityId(block), getBlockPosition(block), animation);
        list.sendPacketNearby(null, block.getX(), block.getY(), block.getZ(), 120, dimension, animPacket);
    }

    public static boolean sendBreakBlock(Player player, Block block) {
        BlockBreakEvent event = new BlockBreakEvent(block, player);
        Bukkit.getPluginManager().callEvent(event);

        if(!event.isCancelled()){
            DropData dropData = DiggingService.getDropData(block.getType());
            Location loc = LocationHelper.getBlockCenter(block.getLocation());
            loc.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 20, new MaterialData(block.getType()));
            sendBlockBreakSound(block);

            if(dropData == null){
                dropBlock(player, block, Material.AIR, block.getDrops());
            } else{
                ItemStack drop = new ItemStack(dropData.getDropMaterial(), dropData.getAmount());
                dropBlock(player, block, dropData.getReplaceBlock(), Arrays.asList(drop));
            }

            return true;
        }

        return false;
    }

    private static void dropBlock(Player player, Block block, Material replace, Collection<ItemStack> items){
        if(replace == null){
            replace = Material.AIR;
        }
        if(dropItem){
            for (ItemStack item : items){
                block.getWorld().dropItem(block.getLocation(), item);
            }
        } else{
            player.getInventory().addItem(items.toArray(new ItemStack[items.size()]));
            player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
        }

        block.setType(replace);
    }

    public static void sendBlockBreakSound(Block block) {
        SoundEffect soundEffect = CraftMagicNumbers.getBlock(block).getStepSound().e();

        double x = block.getX() + 0.5;
        double y = block.getY() + 0.5;
        double z = block.getZ() + 0.5;

        DedicatedPlayerList list = ((CraftServer) Bukkit.getServer()).getHandle();
        int dimension = ((CraftWorld) block.getWorld()).getHandle().dimension;

        PacketPlayOutNamedSoundEffect sound = new PacketPlayOutNamedSoundEffect(soundEffect, SoundCategory.BLOCKS, x, y, z, 0.95f, 0.8f);
        list.sendPacketNearby(null, block.getX(), block.getY(), block.getZ(), 10, dimension, sound);
    }

    public static float getBlockHardness(Block block){
        net.minecraft.server.v1_12_R1.Block mineBlock = CraftMagicNumbers.getBlock(block);
        return (mineBlock != null) ? mineBlock.a(null, null, getBlockPosition(block)) : 0.0f;
    }

    public static boolean isUsableTool(ItemStack tool, Material blockMaterial) {
        net.minecraft.server.v1_12_R1.Block nmsBlock = CraftMagicNumbers.getBlock(blockMaterial);

        if (nmsBlock == null) {
            return false;
        }

        IBlockData data = nmsBlock.getBlockData();
        return data.getMaterial().isAlwaysDestroyable() || tool != null && tool.getType() != Material.AIR && CraftMagicNumbers.getItem(tool.getType()).canDestroySpecialBlock(data);
    }

    public static float getDestroySpeed(ItemStack tool, Material block){
        net.minecraft.server.v1_12_R1.Block nmsBlock = CraftMagicNumbers.getBlock(block);
        IBlockData data = nmsBlock.getBlockData();
        Item item = CraftMagicNumbers.getItem(tool.getType());
        return item.getDestroySpeed(CraftItemStack.asNMSCopy(tool), data);
    }

    private static BlockPosition getBlockPosition(Block block){
        return new BlockPosition(block.getX(), block.getY(), block.getZ());
    }

    private static int getBlockEntityId(Block block){
        return ((block.getX() & 0xFFF) << 20 | (block.getZ() & 0xFFF) << 8) | (block.getY() & 0xFF);
    }

}
