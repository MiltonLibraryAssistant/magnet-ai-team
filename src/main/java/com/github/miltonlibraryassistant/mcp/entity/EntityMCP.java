package com.github.miltonlibraryassistant.mcp.entity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.github.miltonlibraryassistant.mcp.References;
import com.github.miltonlibraryassistant.mcp.entity.searching.BiomeWriteFramework;
import com.github.miltonlibraryassistant.mcp.entity.searching.BlockPosition;
import com.github.miltonlibraryassistant.mcp.entity.searching.GridSearchFramework;
import com.github.miltonlibraryassistant.mcp.entity.searching.QuadrantPoint;
import com.github.miltonlibraryassistant.mcp.entity.tasks.EntityAISeekFood;
import com.github.miltonlibraryassistant.mcp.entity.tasks.EntityAISeekFoodLongDistance;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class EntityMCP extends EntityCreature {

	private int tickcount = 0; 
    protected EntityMCPFoodStats foodStats = new EntityMCPFoodStats();
	
	public EntityMCP(World par1World) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		super(par1World);
        this.setSize(0.7F, 1.0F);
        
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAISeekFood(this));
        this.tasks.addTask(3, new EntityAISeekFoodLongDistance(this));
		this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
		//tasks for water will be here eventually, those will be lower priority than food
		
		if(!(par1World.isRemote)){
			try {
				//Read entities from json
				JSONParser parser = new JSONParser(); 
				JSONObject jsonFileRead = new JSONObject(); 
				
				GridSearchFramework.testFileExists("entities.json"); 
				BufferedReader br = new BufferedReader(new FileReader("entities.json"));     
				if (!(br.readLine() == null)) {
					Object fileRead = parser.parse(new FileReader ("entities.json"));
					jsonFileRead = (JSONObject) fileRead; 
				}
				br.close(); 
				
				
				 Set keys = jsonFileRead.keySet();
				 Object[] jsonReadArray = keys.toArray(new Object[jsonFileRead.size()]); 
				for(int i = 0; i < jsonFileRead.size(); i++){
					Object entity = jsonReadArray[i]; 
					String entityJSON = (String) entity;
					JSONObject entityJSONData = (JSONObject) jsonFileRead.get(entityJSON); 
					Long readdangerRating = (Long) entityJSONData.get("danger");  
					String entityName = (String) entityJSONData.get("entityname"); 
					if(readdangerRating > 0){
						//for every dangerous entity found, an avoidance task is added
						this.tasks.addTask(1, new EntityAIAvoidEntity(this, (Class) Class.forName(entityName), 8.0F, 0.6D, 0.6D)); 
					}
				}
				
				}catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
    public boolean isAIEnabled()
    {
        return true;
    }
    
	@Override
    protected void entityInit()
    {
        super.entityInit();
        //I don't remember why this exists, but I'm going to leave it here in case removing it breaks something. It might be related to NBT
        this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
    }
    
	@Override
    public void onLivingUpdate(){
    	if(tickcount == 40){
        	QuadrantPoint currentQuadrant = GridSearchFramework.getQuadrant(this.posX, this.posZ);
        	try {
				getEntitiesWithinRadius(this.worldObj, 10);
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
        	try {
    			GridSearchFramework.writeQuadrantToJSON(currentQuadrant, this.worldObj, false);
    		} catch (IOException e) {
    			e.printStackTrace();
    		} 
        	tickcount = 0; 
    	}else{
    		tickcount++; 
    	}
    	super.onLivingUpdate(); 
    }
   
	@Override
    protected boolean canDespawn()
    {
        return false;
    }
    
    public void getEntitiesWithinRadius(World world, int radius) throws IOException{
    	AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(this.posX - radius, this.posY - radius, this.posZ - radius, this.posX + radius, this.posY + radius, this.posZ + radius); 
    	List entityList = world.getEntitiesWithinAABB(EntityCreature.class, aabb);
    	Object[] entityArray = entityList.toArray(); 
    	if(entityArray.length > 0 && !(world.isRemote)){
        	for(int i = 0; i < entityArray.length; i++){
        		Object entity = entityArray[i]; 
        		Class entityId = entity.getClass(); 
        		String entityName = entityId.getName(); 
        		BlockPosition currentPosition = new BlockPosition(this.posX, this.posY, this.posZ); 
        		writeEntityToJSON(entityName, world, 0, currentPosition); 
        	}
    	}

    }
    
	public static void writeEntityToJSON(String entityName, World par2World, int entityDanger, BlockPosition currentposition) throws IOException{
		//declare write object and read object 
		JSONObject entityData = new JSONObject(); 
		JSONParser parser = new JSONParser(); 
		
		//add entity to json data for output
		entityData.put("entityname", entityName); 
		BiomeGenBase biome = GridSearchFramework.getBiomeFromBlock(currentposition, par2World); 
		entityData.put("biome", biome.biomeName); 
		
		if(!(par2World.isRemote)){
			String world = par2World.getSaveHandler().getWorldDirectoryName();
			entityData.put("World", world); 
			
			try {
				//Read entities from json
				JSONObject jsonFileRead = new JSONObject(); 
				
				GridSearchFramework.testFileExists("entities.json"); 
				BufferedReader br = new BufferedReader(new FileReader("entities.json"));     
				if (!(br.readLine() == null)) {
					Object fileRead = parser.parse(new FileReader ("entities.json"));
					jsonFileRead = (JSONObject) fileRead; 
				}
				br.close(); 

				//grabs specific entity name from json
				JSONObject readQuadrantData = (JSONObject) jsonFileRead.get(entityName);
				Long readDangerData = null; 
				
				//grabs entity danger rating from file
				if(readQuadrantData != null){
					readDangerData = (Long) readQuadrantData.get("danger"); 	
				}
				
				/**if the entity is not currently stored in the file, or the entity's recorded danger rating is lower than the input, 
				write to the file with the newly added entity**/ 
				if(readDangerData == null || readDangerData < entityDanger){
					//if the entity does not exist within the file or it has attacked and not been previously recorded
					entityData.put("danger", entityDanger); 
					jsonFileRead.put(entityName, entityData);
					GridSearchFramework.writeJSON(jsonFileRead, "entities.json"); 
				}
				else if(readDangerData >= entityDanger && entityDanger == 1){
					//if the entity has attacked repeatedly and should be marked as more dangerous
					entityData.put("danger", readDangerData + 1); 
					jsonFileRead.put(entityName, entityData);
					GridSearchFramework.writeJSON(jsonFileRead, "entities.json"); 	
				}	
				
				}catch (ParseException e) {
				e.printStackTrace();
			}
		}
}
	
	@Override
	public boolean attackEntityFrom(DamageSource dmgsrc, float flt){
		Entity attackerEntity = dmgsrc.getEntity(); 
		if(attackerEntity != null){
			Class entityId = attackerEntity.getClass();
			World world = attackerEntity.worldObj; 
			try {
        		BlockPosition currentPosition = new BlockPosition(this.posX, this.posY, this.posZ);
				writeEntityToJSON(entityId.getName(), world, 1, currentPosition); 
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		return super.attackEntityFrom(dmgsrc, flt); 
		
	}
	
	@Override
	public void onDeath(DamageSource par1DmgSrc){
    	QuadrantPoint currentQuadrant = GridSearchFramework.getQuadrant(this.posX, this.posZ);
    	try {
			GridSearchFramework.writeQuadrantToJSON(currentQuadrant, this.worldObj, true);
		} catch (IOException e) {
			e.printStackTrace();
		} 
    	super.onDeath(par1DmgSrc);
	}
    
    @Override
    public void onUpdate(){
    	super.onUpdate();
        if (!this.worldObj.isRemote)
        {
        	this.foodStats.onUpdate(this);
        }
        if(isAdjacentBlockFoodOrWater(this.worldObj, 1) == 1){
        	this.foodStats.setHunger(this.foodStats.getHunger() + 1);
        }
        if(isAdjacentBlockFoodOrWater(this.worldObj, 2) == 2){
        	this.foodStats.setThirst(this.foodStats.getThirst() + 1);
        }
    }
	
    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBT)
    {
    	super.readEntityFromNBT(par1NBT);
    	this.foodStats.readNBT(par1NBT);
        if (par1NBT.hasKey("tickCount", 99))
        {
            this.tickcount = par1NBT.getInteger("tickCount");
        }
    }
    
    public int isAdjacentBlockFoodOrWater(World world, int foodorwaterblock){
    	//iterates through blocks until it finds one that matches the value passed
    	BlockPosition entityPosition = new BlockPosition(this.posX, this.posY, this.posZ);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX - 1, this.posY, this.posZ);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX - 1, this.posY - 1, this.posZ);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX - 1, this.posY - 1, this.posZ - 1);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX - 1, this.posY - 1, this.posZ + 1);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX - 1, this.posY + 1, this.posZ);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX - 1, this.posY + 1, this.posZ - 1);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX - 1, this.posY + 1, this.posZ + 1);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX + 1, this.posY, this.posZ);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX + 1, this.posY - 1, this.posZ);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX + 1, this.posY - 1, this.posZ - 1);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX + 1, this.posY - 1, this.posZ + 1);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX + 1, this.posY + 1, this.posZ);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX + 1, this.posY + 1, this.posZ + 1);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX + 1, this.posY + 1, this.posZ - 1);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX, this.posY - 1, this.posZ);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX, this.posY - 1, this.posZ + 1);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX, this.posY - 1, this.posZ - 1);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX, this.posY + 1, this.posZ);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX, this.posY + 1, this.posZ + 1);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX, this.posY + 1, this.posZ - 1);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX, this.posY, this.posZ + 1);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX, this.posY, this.posZ - 1);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	//begin new
    	entityPosition = new BlockPosition(this.posX + 1, this.posY, this.posZ - 1);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX + 1, this.posY, this.posZ + 1);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX - 1, this.posY, this.posZ - 1);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	entityPosition = new BlockPosition(this.posX - 1, this.posY, this.posZ + 1);
    	if(isFoodBlockOrWaterBlock(entityPosition, world) == foodorwaterblock){
    		return isFoodBlockOrWaterBlock(entityPosition, world); 
    	}
    	return 0; 
    }
    
    public static int isFoodBlockOrWaterBlock(BlockPosition par1blockposition, World world){
    	//returns 0 for nothing, 1 for food, 2 far water
    	Block topBlockAsBlock = world.getBlock((int) par1blockposition.x, (int) par1blockposition.y, (int) par1blockposition.z); 
    	if(topBlockAsBlock.getUnlocalizedName() == References.modId + ":FoodBlock"){
    		return 1; 
    	}
    	if(topBlockAsBlock.getMaterial() == Material.water){
    		return 2; 
    	}
    	return 0; 
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound p_70014_1_){
    	super.writeEntityToNBT(p_70014_1_);
    	this.foodStats.writeNBT(p_70014_1_);
    	p_70014_1_.setInteger("tickCount", tickcount);
    }
    
    //To prevent annoying drowning while testing. 
    @Override
    public boolean canBreatheUnderwater()
    {
        return true;
    }
    
    public EntityMCPFoodStats getFoodStats(){
    	return this.foodStats; 
    }
    
    public void tryMoveToXYZ(BlockPosition blockposition){
		this.getNavigator().tryMoveToXYZ(blockposition.x, blockposition.y, blockposition.z, 1);
    }
    
    @Override
    protected void applyEntityAttributes(){
    	super.applyEntityAttributes();
    	
    	getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(200.0D);
    }
    
    public int getTickCount(){
    	return this.tickcount; 
    }
}
