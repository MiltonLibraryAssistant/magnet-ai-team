package com.github.miltonlibraryassistant.mcp.entity.tasks;

import com.github.miltonlibraryassistant.mcp.References;
import com.github.miltonlibraryassistant.mcp.entity.EntityMCP;
import com.github.miltonlibraryassistant.mcp.entity.searching.BlockPosition;
import com.github.miltonlibraryassistant.mcp.entity.searching.GridSearchFramework;
import com.github.miltonlibraryassistant.mcp.entity.searching.QuadrantPoint;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

public class EntityAISeekFood extends EntityAIBase {

	static EntityMCP attachedEntity; 
	static BlockPosition foodPosition;
	
	public EntityAISeekFood(EntityMCP par1Entity){
		this.attachedEntity = par1Entity; 
		setMutexBits(1);
	}
	
	@Override
	public boolean shouldExecute() {
		//System.out.println(attachedEntity.getFoodStats().getHunger());
		System.out.println(attachedEntity.worldObj.isRemote);
		if(attachedEntity.getFoodStats().getHunger() < attachedEntity.getFoodStats().maxFoodLevel){
			QuadrantPoint quadrant = GridSearchFramework.getQuadrant(attachedEntity.posX, attachedEntity.posZ);
			World world = attachedEntity.worldObj; 
			if(isWaterOrFoodInQuadrant(quadrant, world) != null){
				System.out.println("trying to reach food in quadrant...");
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean continueExecuting()
	{
		attachedEntity.getNavigator().tryMoveToXYZ(foodPosition.x, foodPosition.y, foodPosition.z, 6);
		if(attachedEntity.getFoodStats().getHunger() == attachedEntity.getFoodStats().maxFoodLevel){
			return false; 
		}
		return true; 
	}
	
    public static BlockPosition isWaterOrFoodInQuadrant(QuadrantPoint quadrant, World par2World){
    	BlockPosition quadrantCorner = GridSearchFramework.getQuadrantCenter(quadrant.X, quadrant.Z, par2World); 
    	quadrantCorner.x = quadrantCorner.x - 10; 
    	quadrantCorner.z = quadrantCorner.z - 10; 
    	for(int i = 0; i <= 20; i++){
    		for(int j = 0; j <= 20; j++){
	    		BlockPosition topBlock = GridSearchFramework.getTopBlock((int) quadrantCorner.x + j, (int) quadrantCorner.z + i, par2World);
	        	Block topBlockAsBlock = par2World.getBlock((int) topBlock.x, (int) topBlock.y, (int) topBlock.z); 
	    		if(topBlockAsBlock.getUnlocalizedName() == References.modId + ":FoodBlock"){
	    			foodPosition = topBlock;
	    			return topBlock; 
	    		}
    		}
    	}
    	return null; 
    }

}
