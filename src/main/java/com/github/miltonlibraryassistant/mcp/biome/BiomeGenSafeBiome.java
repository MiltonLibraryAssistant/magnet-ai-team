package com.github.miltonlibraryassistant.mcp.biome;

import com.github.miltonlibraryassistant.mcp.worldgen.FoodBiomeDecorator;

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
	}
	
	@Override
    public BiomeDecorator getModdedBiomeDecorator(BiomeDecorator original)
    {
        return new FoodBiomeDecorator();
    }
	

}