package com.github.miltonlibraryassistant.mcp;

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
		
		@Instance(References.modId)
		public static MainMCP modInstance; 
		
		@SidedProxy(clientSide=References.proxy_client, serverSide=References.proxy_common)
		public static ProxyCommon theproxy;  

		
		@EventHandler
		public void preInit(FMLPreInitializationEvent event) {
			theproxy.registerRenderThings();
			MCPEntityRegistry.mainRegistry();
		}
		
		@EventHandler
		public void init(FMLInitializationEvent event) {
			
			theproxy.register_renderers(); 
			
			}
		
		@EventHandler
		public void postInit(FMLPostInitializationEvent event) {
			
		}
}