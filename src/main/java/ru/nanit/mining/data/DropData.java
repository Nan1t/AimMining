package ru.nanit.mining.data;

import org.bukkit.Material;

public class DropData {

    private Material dropMaterial;
    private int amount;
    private Material replaceBlock;

    public DropData(Material dropMaterial, Material replaceBlock, int amount){
        this.dropMaterial = dropMaterial;
        this.replaceBlock = replaceBlock;
        this.amount = amount;
    }

    public Material getDropMaterial() {
        return dropMaterial;
    }

    public int getAmount() {
        return amount;
    }

    public Material getReplaceBlock() {
        return replaceBlock;
    }
}
