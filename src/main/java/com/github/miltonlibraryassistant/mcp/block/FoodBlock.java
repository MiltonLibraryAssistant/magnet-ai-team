package com.github.miltonlibraryassistant.mcp.block;

import com.github.miltonlibraryassistant.mcp.References;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class FoodBlock extends Block {

	public FoodBlock(Material material) {
		super(material); 
	}
	
    public String getUnlocalizedName()
    {
        return References.modId + ":FoodBlock"; 
    }

}
