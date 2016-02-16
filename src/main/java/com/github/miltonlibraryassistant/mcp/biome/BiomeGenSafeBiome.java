package com.github.miltonlibraryassistant.mcp.biome;

import java.util.List;

import com.github.miltonlibraryassistant.mcp.entity.EntityMCP;
import com.github.miltonlibraryassistant.mcp.worldgen.FoodBiomeDecorator;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.terraingen.DeferredBiomeDecorator;

public class BiomeGenSafeBiome extends BiomeGenBase {
    
	public BiomeGenSafeBiome(int id){
		super(id); 
		this.heightVariation = 0.0F; 
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
        this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityMCP.class, 12, 0, 2));
	}
	
	@Override
    public BiomeDecorator getModdedBiomeDecorator(BiomeDecorator original)
    {
        return new FoodBiomeDecorator();
    }
	
}