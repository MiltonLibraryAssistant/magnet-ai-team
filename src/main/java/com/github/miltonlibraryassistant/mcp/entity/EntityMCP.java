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
    	//getEntitiesWithinRadius(this.worldObj, 10, 0); 
    	try {
			GridSearchFramework.writeQuadrantToJSON(currentQuadrant, this.worldObj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }
    
    protected boolean canDespawn()
    {
        return false;
    }
    
    public void getEntitiesWithinRadius(World world, int radiusmax, int radiusmin){
    	AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(radiusmin, radiusmin, radiusmin, radiusmax, radiusmax, radiusmax); 
    	List entityList = world.getEntitiesWithinAABB(EntityCreature.class, aabb);
    	Object[] entityArray = entityList.toArray(); 
    	if(entityArray.length > 0){
        	for(int i = 1; i == entityArray.length; i++){
        		Entity entity = (Entity) entityArray[i]; 
        		int entityId = entity.getEntityId(); 
        		System.out.println(entityId); 
        	}
    	}

    }
}
