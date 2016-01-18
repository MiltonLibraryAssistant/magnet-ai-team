package com.github.miltonlibraryassistant.mcp.entity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.github.miltonlibraryassistant.mcp.entity.searching.BiomeWriteFramework;
import com.github.miltonlibraryassistant.mcp.entity.searching.BlockPosition;
import com.github.miltonlibraryassistant.mcp.entity.searching.GridSearchFramework;
import com.github.miltonlibraryassistant.mcp.entity.searching.QuadrantPoint;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

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
    	if(tickcount == 40){
        	QuadrantPoint currentQuadrant = GridSearchFramework.getQuadrant(this.posX, this.posZ);
        	try {
				getEntitiesWithinRadius(this.worldObj, 10);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
	
	public boolean attackEntityFrom(DamageSource dmgsrc, float flt){
		Entity attackerEntity = dmgsrc.getEntity(); 
		if(attackerEntity != null){
			Class entityId = attackerEntity.getClass();
			World world = attackerEntity.worldObj; 
			try {
        		BlockPosition currentPosition = new BlockPosition(this.posX, this.posY, this.posZ);
				writeEntityToJSON(entityId.getName(), world, 1, currentPosition);
				System.out.println("entity written"); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		return super.attackEntityFrom(dmgsrc, flt); 
		
	}
}
