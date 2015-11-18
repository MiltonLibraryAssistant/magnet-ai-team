package com.github.miltonlibraryassistant.mcp.world;

import net.minecraft.world.WorldType;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerBiomeEdge;
import net.minecraft.world.gen.layer.GenLayerZoom;

public class WorldTypeTestField extends WorldType {

	public WorldTypeTestField(int par1, String name) {
		super(name);
	}
	
    public static double MAX_ENTITY_RADIUS = 10.0D;
    
    public GenLayer getBiomeLayer(long worldSeed, GenLayer parentLayer)
    {
        GenLayer ret = new TestFieldGenLayerBiome(200L, parentLayer, this);
        ret = GenLayerZoom.magnify(1000L, ret, 2);
        ret = new GenLayerBiomeEdge(1000L, ret);
        return ret;
    }

	/**
	 * setHeight in BiomeGenBase can be used for the flat bits, 
	 * and then the other bits can be registered like a normal biome.
	 * the 'decorate' method can be used for the buildings.
	 */
}
