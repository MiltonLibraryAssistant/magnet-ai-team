package com.github.miltonlibraryassistant.mcp.entity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.github.miltonlibraryassistant.mcp.entity.searching.GridSearchFramework;
import com.github.miltonlibraryassistant.mcp.entity.searching.QuadrantPoint;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityMCP extends EntityCreature {

	private int tickcount = 0; 
	
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
    	if(tickcount == 5){
        	QuadrantPoint currentQuadrant = GridSearchFramework.getQuadrant(this.posX, this.posZ);
        	getEntitiesWithinRadius(this.worldObj, 10); 
        	try {
    			GridSearchFramework.writeQuadrantToJSON(currentQuadrant, this.worldObj);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} 
        	tickcount = 0; 
    	}else{
    		tickcount++; 
    	}
    }
    
    protected boolean canDespawn()
    {
        return false;
    }
    
    public void getEntitiesWithinRadius(World world, int radius){
    	AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(this.posX - radius, this.posY - radius, this.posZ - radius, this.posX + radius, this.posY + radius, this.posZ + radius); 
    	List entityList = world.getEntitiesWithinAABB(EntityCreature.class, aabb);
    	Object[] entityArray = entityList.toArray(); 
    	if(entityArray.length > 0 && !(world.isRemote)){
        	for(int i = 0; i < entityArray.length; i++){
        		Object entity = entityArray[i]; 
        		Class entityId = entity.getClass(); 
        		String entityName = entityId.getName(); 
        	}
    	}

    }
}
