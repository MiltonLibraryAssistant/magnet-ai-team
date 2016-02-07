package com.github.miltonlibraryassistant.mcp.entity.tasks;

import com.github.miltonlibraryassistant.mcp.References;
import com.github.miltonlibraryassistant.mcp.entity.EntityMCP;
import com.github.miltonlibraryassistant.mcp.entity.searching.BlockPosition;
import com.github.miltonlibraryassistant.mcp.entity.searching.GridSearchFramework;
import com.github.miltonlibraryassistant.mcp.entity.searching.QuadrantPoint;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

public class EntityAISeekFood extends EntityAIBase {

	private EntityMCP attachedEntity; 
	static BlockPosition foodPosition;
	
	public EntityAISeekFood(EntityMCP par1Entity){
		this.attachedEntity = par1Entity; 
		setMutexBits(1);
	}
	
	@Override
	public boolean shouldExecute() {
		if(attachedEntity.getFoodStats().getHunger() < attachedEntity.getFoodStats().maxFoodLevel){
			QuadrantPoint quadrant = GridSearchFramework.getQuadrant(attachedEntity.posX, attachedEntity.posZ);
			World world = attachedEntity.worldObj; 
			if(isWaterOrFoodInQuadrant(quadrant, world) != null){
				foodPosition = isWaterOrFoodInQuadrant(quadrant,world);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean continueExecuting()
	{	
		if(shouldExecute()){
			if(!(isWithinXBlocksOf((int) this.attachedEntity.posX, (int) foodPosition.x, 1) && isWithinXBlocksOf((int) this.attachedEntity.posY, (int) foodPosition.y, 1) && isWithinXBlocksOf((int) this.attachedEntity.posZ, (int) foodPosition.z, 1))){
				BlockPosition relativeFoodPosition = FindAdjacentAirBlock(attachedEntity.worldObj, foodPosition);
				if(relativeFoodPosition != null){
					this.attachedEntity.tryMoveToXYZ(relativeFoodPosition);	
				}		
			}
			if(attachedEntity.getFoodStats().getHunger() == attachedEntity.getFoodStats().maxFoodLevel){
				return false; 
			}
			return true; 
		}
		return false; 
	}
	
    public static BlockPosition isWaterOrFoodInQuadrant(QuadrantPoint quadrant, World par2World){
    	BlockPosition quadrantCorner = GridSearchFramework.getQuadrantCenter(quadrant.X, quadrant.Z, par2World); 
    	quadrantCorner.x = quadrantCorner.x - 10; 
    	quadrantCorner.z = quadrantCorner.z - 10; 
    	for(int i = 0; i <= 20; i++){
    		int j = 0;
    		for(j = 0; j <= 20; j++){
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

    /** returns whether a number is within a certain distance from another number.**/
    public boolean isWithinXBlocksOf(int posSrc1, int posTarget2, int threshold){
    	if(posSrc1 <= posTarget2 + threshold && posSrc1 >= posTarget2 - threshold){
    		return true; 
    	}
    	return false; 
    }
    
    public BlockPosition FindAdjacentAirBlock(World world, BlockPosition block){
    	//iterates through adjacent blocks until it finds one that is air
    	//there's probably a better way to do this considering this is copy/paste from my entity class
    	Block blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x - 1, block.y, block.z);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x - 1, block.y - 1, block.z);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x - 1, block.y - 1, block.z - 1);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x - 1, block.y - 1, block.z + 1);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x - 1, block.y + 1, block.z);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x - 1, block.y + 1, block.z - 1);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x - 1, block.y + 1, block.z + 1);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x + 1, block.y, block.z);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x + 1, block.y - 1, block.z);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x + 1, block.y - 1, block.z - 1);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x + 1, block.y - 1, block.z + 1);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x + 1, block.y + 1, block.z);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x + 1, block.y + 1, block.z + 1);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x + 1, block.y + 1, block.z - 1);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x, block.y - 1, block.z);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x, block.y - 1, block.z + 1);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x, block.y - 1, block.z - 1);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x, block.y + 1, block.z);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x, block.y + 1, block.z + 1);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x, block.y + 1, block.z - 1);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x, block.y, block.z + 1);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x, block.y, block.z - 1);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	//begin new
    	block = new BlockPosition(block.x + 1, block.y, block.z - 1);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x + 1, block.y, block.z + 1);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x - 1, block.y, block.z - 1);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	block = new BlockPosition(block.x - 1, block.y, block.z + 1);
    	blockAsBlock = world.getBlock((int) block.x, (int) block.y, (int) block.z); 
    	if(blockAsBlock.getMaterial() == Material.air){
    		return block; 
    	}
    	return null; 
    }
    
    public void updateTask()
    {
    	shouldExecute();
    }
}
