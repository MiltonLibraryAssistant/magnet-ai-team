package com.github.miltonlibraryassistant.mcp.entity;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.github.miltonlibraryassistant.mcp.entity.searching.GridSearchFramework;
import com.github.miltonlibraryassistant.mcp.entity.searching.QuadrantPoint;

import net.minecraft.entity.ai.EntityAITargetNonTamed; 
import net.minecraft.entity.passive.EntityTameable;

public class EntityAggressorMCP extends EntityTameable {

	public EntityAggressorMCP(World p_i1602_1_) {
		super(p_i1602_1_);
        this.setSize(1F, 1);
		this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
        this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityMCP.class, 200, false));
        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
        this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.0D, true));
	}

    public boolean isAIEnabled()
    {
        return true;
    }
    
    public void onLivingUpdate(){
    	super.onLivingUpdate(); 
    }

	@Override
	public EntityAgeable createChild(EntityAgeable p_90011_1_) {
		return null;
	}
	
	@Override
	public boolean isSitting(){
		return false; 
	}
	
	@Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(18, Byte.valueOf((byte)0));
    }
	
	@Override
    public boolean attackEntityAsMob(Entity p_70652_1_)
    {
        return p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
    }
}
