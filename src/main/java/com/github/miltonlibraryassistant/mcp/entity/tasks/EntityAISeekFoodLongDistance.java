package com.github.miltonlibraryassistant.mcp.entity.tasks;

import com.github.miltonlibraryassistant.mcp.entity.EntityMCP;
import com.github.miltonlibraryassistant.mcp.entity.searching.GridSearchFramework;
import com.github.miltonlibraryassistant.mcp.entity.searching.QuadrantPoint;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

public class EntityAISeekFoodLongDistance extends EntityAIBase {
	
	private EntityMCP attachedEntity;
	
	public EntityAISeekFoodLongDistance(EntityMCP par1Entity){
		this.attachedEntity = par1Entity; 
		setMutexBits(1);
	}
	
	@Override
	public boolean shouldExecute() {
		if(attachedEntity.getFoodStats().getHunger() < attachedEntity.getFoodStats().maxFoodLevel){
			return true; 
		}
		return false;
	}

}
