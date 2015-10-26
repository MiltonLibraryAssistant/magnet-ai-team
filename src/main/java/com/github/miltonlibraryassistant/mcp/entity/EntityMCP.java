package com.github.miltonlibraryassistant.mcp.entity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.github.miltonlibraryassistant.mcp.entity.searching.GridSearchFramework;
import com.github.miltonlibraryassistant.mcp.entity.searching.QuadrantPoint;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.world.World;

public class EntityMCP extends EntityCreature {

	public EntityMCP(World p_i1602_1_) {
		super(p_i1602_1_);
		this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
	}

    public boolean isAIEnabled()
    {
        return true;
    }
    
    public void onLivingUpdate(){
    	super.onLivingUpdate(); 
    	QuadrantPoint currentQuadrant = GridSearchFramework.getQuadrant(this.posX, this.posZ);
    	try {
			GridSearchFramework.writeQuadrantToJSON(currentQuadrant);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }
}
