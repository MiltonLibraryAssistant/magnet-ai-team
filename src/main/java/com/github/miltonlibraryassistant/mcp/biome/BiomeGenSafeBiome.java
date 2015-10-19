package com.github.miltonlibraryassistant.mcp.biome;

import net.minecraft.world.biome.BiomeGenBase;

public class BiomeGenSafeBiome extends BiomeGenBase {
    
	public BiomeGenSafeBiome(int id){
		super(id); 
		this.heightVariation = 0.0F; 
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
	}
	

}