package com.github.miltonlibraryassistant.mcp.proxy;

import com.github.miltonlibraryassistant.mcp.entity.EntityMCP;
import com.github.miltonlibraryassistant.mcp.entity.RenderEntityMCP;
import com.github.miltonlibraryassistant.mcp.models.blockmodel;

import cpw.mods.fml.client.registry.RenderingRegistry;

public class ProxyClient extends ProxyCommon {

		public void register_renderers(){
		}
		
		public void registerRenderThings(){
			RenderingRegistry.registerEntityRenderingHandler(EntityMCP.class, new RenderEntityMCP(new blockmodel(), 1));
		}
		
		public void registerTileEntitySpecialRenderer(){
			
		}
}
