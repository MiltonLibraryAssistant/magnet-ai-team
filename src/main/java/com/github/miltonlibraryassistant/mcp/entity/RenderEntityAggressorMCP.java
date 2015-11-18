package com.github.miltonlibraryassistant.mcp.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import com.github.miltonlibraryassistant.mcp.References;
import com.github.miltonlibraryassistant.mcp.models.blockmodel;

public class RenderEntityAggressorMCP extends RenderLiving {
	
	private static final ResourceLocation mcpTexture = new ResourceLocation(References.modId + ":textures/entity/EntityCityBird.png");
    private blockmodel mcpModel; 
    
    public RenderEntityAggressorMCP(ModelBase par1ModelBase, float par2) {
		super(par1ModelBase, par2);
        this.mcpModel = (blockmodel)super.mainModel;
        this.setRenderPassModel(this.mcpModel);
	}

	protected ResourceLocation getEntityTexture(EntityAggressorMCP entity) {
		return mcpTexture; 
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return this.getEntityTexture((EntityAggressorMCP)entity);  
	}

}