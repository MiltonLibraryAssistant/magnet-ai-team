package com.github.miltonlibraryassistant.mcp.worldgen;

import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.PUMPKIN;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraftforge.event.terraingen.TerrainGen;

public class FoodBiomeDecorator extends BiomeDecorator {
	
	@Override
	protected void genDecorations(BiomeGenBase par1GenBase){
		super.genDecorations(par1GenBase); 
		
        boolean doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, PUMPKIN);
        if (doGen && this.randomGenerator.nextInt(32) != 0 )
        {
            int j = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            int k = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            int l = nextInt(this.currentWorld.getHeightValue(j, k) * 2);
            (new WorldGenFoodBlock()).generate(this.currentWorld, this.randomGenerator, j, l, k);
        }
	}
	
    private int nextInt(int i) {
        if (i <= 1)
            return 0;
        return this.randomGenerator.nextInt(i);
	}
}
