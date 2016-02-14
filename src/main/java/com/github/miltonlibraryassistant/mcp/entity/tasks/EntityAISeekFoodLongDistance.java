package com.github.miltonlibraryassistant.mcp.entity.tasks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.github.miltonlibraryassistant.mcp.References;
import com.github.miltonlibraryassistant.mcp.entity.EntityMCP;
import com.github.miltonlibraryassistant.mcp.entity.searching.BlockPosition;
import com.github.miltonlibraryassistant.mcp.entity.searching.GridSearchFramework;
import com.github.miltonlibraryassistant.mcp.entity.searching.QuadrantPoint;
import com.github.miltonlibraryassistant.mcp.entity.searching.pathing.QuadrantPathFind;
import com.github.miltonlibraryassistant.mcp.entity.searching.pathing.QuadrantPathPoint;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.world.World;

public class EntityAISeekFoodLongDistance extends EntityAIBase {
	
	private EntityMCP attachedEntity;
	
	public EntityAISeekFoodLongDistance(EntityMCP par1Entity){
		this.attachedEntity = par1Entity; 
		setMutexBits(1);
	}
	
	@Override
	public boolean shouldExecute() {
		//test whether 
		QuadrantPoint entityQuadrantPosition = GridSearchFramework.getQuadrant(this.attachedEntity.posX, this.attachedEntity.posZ); 
		int waterOrFoodInQuadrant = GridSearchFramework.isWaterOrFoodInQuadrant(entityQuadrantPosition, attachedEntity.worldObj); 
		if(waterOrFoodInQuadrant == 0 || waterOrFoodInQuadrant == 2){
			try {
				//Read quadrants from json
				JSONParser parser = new JSONParser(); 
				JSONObject jsonFileRead = new JSONObject(); 
				
				GridSearchFramework.testFileExists("quadrants.json"); 
				BufferedReader br = new BufferedReader(new FileReader("quadrants.json"));     
				if (!(br.readLine() == null)) {
					Object fileRead = parser.parse(new FileReader ("quadrants.json"));
					jsonFileRead = (JSONObject) fileRead; 
				}
				br.close(); 
				
				
				 Set keys = jsonFileRead.keySet();
				 Object[] jsonReadArray = keys.toArray(new Object[jsonFileRead.size()]); 
				 
				for(int i = 0; i < jsonFileRead.size(); i++){
					//iterates through quadrants checking if any have food or water
					Object quadrant = jsonReadArray[i]; 
					String quadrantJSON = (String) quadrant;
					JSONObject quadrantJSONData = (JSONObject) jsonFileRead.get(quadrantJSON); 
					Long readdangerRating = (Long) quadrantJSONData.get("waterandfood");  
					if(readdangerRating == 1 || readdangerRating == 3){
						//tests whether potential quadrant is within same world
						String readWorld = (String) quadrantJSONData.get("World");
						if(readWorld.equals(this.attachedEntity.worldObj.getSaveHandler().getWorldDirectoryName())){
							//1 is food, 3 is food and water
							if(attachedEntity.getFoodStats().getHunger() < attachedEntity.getFoodStats().maxFoodLevel){
								return true; 
							}
						}
					}
				}
			}
			catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
				
		}
		return false;
	}
	
	@Override
	public boolean continueExecuting()
	{	
		if(shouldExecute()){
			try {
				//Read quadrants from json
				JSONParser parser = new JSONParser(); 
				JSONObject jsonFileRead = new JSONObject(); 
				
				GridSearchFramework.testFileExists("quadrants.json"); 
				BufferedReader br = new BufferedReader(new FileReader("quadrants.json"));     
				if (!(br.readLine() == null)) {
					Object fileRead = parser.parse(new FileReader ("quadrants.json"));
					jsonFileRead = (JSONObject) fileRead; 
				}
				br.close(); 
				
				
				 Set keys = jsonFileRead.keySet();
				 Object[] jsonReadArray = keys.toArray(new Object[jsonFileRead.size()]); 
				 
				 //grabs the closest quadrant containing food
				 QuadrantPoint quadrantSearch = null; 
				 QuadrantPoint entityQuadrantPosition = GridSearchFramework.getQuadrant(this.attachedEntity.posX, this.attachedEntity.posZ); 
				for(int i = 0; i < jsonFileRead.size(); i++){
					//iterates through quadrants looking for the closest
					Object quadrant = jsonReadArray[i]; 
					String quadrantJSON = (String) quadrant;
					JSONObject quadrantJSONData = (JSONObject) jsonFileRead.get(quadrantJSON); 
					Long readdangerRating = (Long) quadrantJSONData.get("waterandfood");  
					if(readdangerRating == 1 || readdangerRating == 3){
						//1 is food, 3 is food and water
						if(quadrantSearch != null){
							QuadrantPoint tempQuadrant = new QuadrantPoint((Double) quadrantJSONData.get("XPos"), (Double) quadrantJSONData.get("ZPos"));
							//test if found quadrant is within same world
							String readWorld = (String) quadrantJSONData.get("World");
							if(readWorld.equals(this.attachedEntity.worldObj.getSaveHandler().getWorldDirectoryName())){
								double distanceToQuadrantSearch;
								double distanceToTempQuadrant; 
								double x;
								double z;
								x = Math.abs(entityQuadrantPosition.X - quadrantSearch.X); 
								z = Math.abs(entityQuadrantPosition.Z - quadrantSearch.Z); 
								distanceToQuadrantSearch = (Math.sqrt(x*x + z*z)); 
								x = Math.abs(entityQuadrantPosition.X - tempQuadrant.X); 
								z = Math.abs(entityQuadrantPosition.Z - tempQuadrant.Z); 
								distanceToTempQuadrant = (Math.sqrt(x*x + z*z)); 
								if(distanceToTempQuadrant <= distanceToQuadrantSearch){
									quadrantSearch = tempQuadrant; 
								}
							}
						}
						else{
							quadrantSearch = new QuadrantPoint((Double) quadrantJSONData.get("XPos"), (Double) quadrantJSONData.get("ZPos")); 
						}
					}
				}
				
				if(quadrantSearch != null){
					PathEntity pathOfQuadrants = QuadrantPathFind.getPath((int) entityQuadrantPosition.X, (int) entityQuadrantPosition.Z, (int) quadrantSearch.X, (int) quadrantSearch.Z, attachedEntity.worldObj);
					if(pathOfQuadrants != null){
						QuadrantPathPoint firstPoint = (QuadrantPathPoint) pathOfQuadrants.getPathPointFromIndex(0);
						QuadrantPoint firstQuadrant = new QuadrantPoint(firstPoint.xCoord, firstPoint.zCoord);
						//tries to path to valid quadrant center if one is found
						BlockPosition pathTo = GridSearchFramework.getQuadrantCenter(firstQuadrant.X, firstQuadrant.Z, this.attachedEntity.worldObj); 
						BlockPosition pathToAir = EntityAISeekFood.FindAdjacentAirBlock(attachedEntity.worldObj, pathTo); 
						System.out.println(pathToAir.x + " " + pathToAir.y + " " + pathToAir.z);
						if(pathToAir != null){
							System.out.println("trying to execute distance pathfinding");
							this.attachedEntity.tryMoveToXYZ(pathToAir);	
						}
					}
				}
				
			}catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false; 
	}
}
