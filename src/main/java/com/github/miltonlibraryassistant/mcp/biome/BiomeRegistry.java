package com.github.miltonlibraryassistant.mcp.biome;

import com.github.miltonlibraryassistant.mcp.References;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager;

public class BiomeRegistry {
	public static void mainRegistry(){
		registerBiome(); 
		initializeBiome();

	}
	
	public static BiomeGenBase biomeSafeBiome; 
	
	
	private static void initializeBiome() {
		BiomeDictionary.registerBiomeType(biomeSafeBiome, Type.FOREST); 
		BiomeManager.addSpawnBiome(biomeSafeBiome); 
	}
	
	private static void registerBiome() {
		
		biomeSafeBiome = new BiomeGenSafeBiome(References.safeBiomeID).setBiomeName("SafeBiome"); 
	}


}
