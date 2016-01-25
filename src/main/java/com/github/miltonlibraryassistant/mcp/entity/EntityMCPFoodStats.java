package com.github.miltonlibraryassistant.mcp.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public class EntityMCPFoodStats {
	
	private double maxFoodLevel = 20;
	private double maxThirstLevel = 40;
    private double foodLevel = 20;
    private int foodTimer = 0;
    private int prevFoodLevel = 20;
    private double thirstLevel = 40; 
    
    public void onUpdate(EntityMCP par1EntityMCP){
    	if(foodTimer < 40){
    		foodTimer++; 
    	}
    	else{
    		System.out.println("foodlevel = " + foodLevel + "thirstLevel = " + thirstLevel);
    		if(foodLevel > 0){
    			foodLevel = foodLevel - 0.5; 
    		}
    		else{
                par1EntityMCP.attackEntityFrom(DamageSource.starve, 1.0F);
                System.out.println("starving to death");
    		}
    		if(thirstLevel > 0){
    			thirstLevel = thirstLevel - 0.5;
    		}
    		else{
    			par1EntityMCP.attackEntityFrom(DamageSource.starve, 1.0F); 
    			System.out.println("dehydrating to death");
    		}
    		foodTimer = 0; 
    	}
    }
    
    /**
     * Reads food stats from an NBT object.
     */
    public void readNBT(NBTTagCompound p_75112_1_)
    {
        if (p_75112_1_.hasKey("foodLevel", 99))
        {
            this.foodLevel = p_75112_1_.getInteger("foodLevel");
            this.thirstLevel = p_75112_1_.getInteger("thirstLevel");
            this.foodTimer = p_75112_1_.getInteger("foodTickTimer");
        }
    }

    /**
     * Writes food stats to an NBT object.
     */
    public void writeNBT(NBTTagCompound p_75117_1_)
    {
        p_75117_1_.setDouble("foodLevel", this.foodLevel);
        p_75117_1_.setDouble("thirstLevel", this.thirstLevel);
        p_75117_1_.setInteger("foodTickTimer", this.foodTimer);
    }
    
    public double getHunger(){
    	return this.foodLevel; 
    }
    
    public double getThirst(){
    	return this.thirstLevel; 
    }
    
    public void setHunger(double par1Int){
    	if(par1Int <= maxFoodLevel){
        	this.foodLevel = par1Int; 	
    	}
    }
    
    public void setThirst(double par2Thirst){
    	if(par2Thirst <= maxThirstLevel){
        	this.thirstLevel = par2Thirst;	
    	}
    }
}
