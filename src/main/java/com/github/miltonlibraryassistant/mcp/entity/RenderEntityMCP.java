package com.github.miltonlibraryassistant.mcp.entity;

import com.github.miltonlibraryassistant.mcp.References;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderEntityMCP extends RenderLiving {
	
	private static final ResourceLocation mcpTexture = new ResourceLocation(References.modId + ":textures/entity/EntityCityBird.png");
    private neoncitybirdmodel mcpModel;
    
    public RenderEntityMCP(ModelBase par1ModelBase, float par2) {
		super(par1ModelBase, par2);
        this.mcpModel = (neoncitybirdmodel)super.mainModel;
        this.setRenderPassModel(this.mcpModel);
	}

	protected ResourceLocation getEntityTexture(EntityMCP entity) {
		return mcpTexture; 
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return this.getEntityTexture((EntityMCP)entity);  
	}

}
