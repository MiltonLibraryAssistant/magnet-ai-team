package com.github.miltonlibraryassistant.mcp;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

import com.github.miltonlibraryassistant.mcp.block.FoodBlock;
import com.github.miltonlibraryassistant.mcp.proxy.ProxyCommon;
import com.github.miltonlibraryassistant.mcp.registry.MCPEntityRegistry;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = References.modId, name = References.name, version = References.version)
public class MainMCP {
		
		public static Block blockFoodBlock; 
		
		@Instance(References.modId)
		public static MainMCP modInstance; 
		
		@SidedProxy(clientSide=References.proxy_client, serverSide=References.proxy_common)
		public static ProxyCommon theproxy;  

		
		@EventHandler
		public void preInit(FMLPreInitializationEvent event) {
			theproxy.registerRenderThings();
			MCPEntityRegistry.mainRegistry();
			
			//Registration of blocks and items
			blockFoodBlock = new FoodBlock(Material.gourd).setBlockName("Food Block").setCreativeTab(CreativeTabs.tabBlock).setBlockTextureName(References.modId.concat(":FoodBlock"));
		}
		
		@EventHandler
		public void init(FMLInitializationEvent event) {
			
			theproxy.register_renderers(); 
			
			}
		
		@EventHandler
		public void postInit(FMLPostInitializationEvent event) {
			
		}
}