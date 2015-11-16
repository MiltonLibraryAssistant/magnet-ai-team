package com.github.miltonlibraryassistant.mcp.registry;

import com.github.miltonlibraryassistant.mcp.MainMCP;
import com.github.miltonlibraryassistant.mcp.entity.EntityAggressorMCP;
import com.github.miltonlibraryassistant.mcp.entity.EntityMCP;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import cpw.mods.fml.common.registry.EntityRegistry;

public class MCPEntityRegistry {

	public static void mainRegistry(){
		registerMCPEntity(); 
	}

	private static void registerMCPEntity() {
		
		createEntityMCP(EntityMCP.class, "entitymcp", 0x53DBB0, 0x1AD248); 
		createEntityAggressorMCP(EntityAggressorMCP.class, "entityaggressormcp", 0xFF7E3C, 0x7F5743); 
		
	}
	
	public static void createEntityMCP(Class entityClass, String entityName, int solidColor, int spotColor) {
		
		int entityId = EntityRegistry.findGlobalUniqueEntityId(); 
		EntityRegistry.registerGlobalEntityID(entityClass, entityName, entityId); 
		EntityRegistry.registerModEntity(entityClass, entityName, entityId, MainMCP.modInstance, 128, 2, true);
		EntityRegistry.addSpawn(entityClass, 2, 0, 2, EnumCreatureType.creature, BiomeGenBase.desert);
		
		createEgg(entityId, solidColor, spotColor); 
		
	}
	
	public static void createEntityAggressorMCP(Class entityClass, String entityName, int solidColor, int spotColor) {
		
		int entityId = EntityRegistry.findGlobalUniqueEntityId(); 
		EntityRegistry.registerGlobalEntityID(entityClass, entityName, entityId); 
		EntityRegistry.registerModEntity(entityClass, entityName, entityId, MainMCP.modInstance, 128, 2, true);
		EntityRegistry.addSpawn(entityClass, 2, 0, 2, EnumCreatureType.monster, BiomeGenBase.desert);
		
		createEgg(entityId, solidColor, spotColor); 
		
	}
	
	private static void createEgg(int id, int solidColor, int spotColor){
		EntityList.entityEggs.put(Integer.valueOf(id), new EntityList.EntityEggInfo(id, solidColor, spotColor)); 
		
	}
	
}
