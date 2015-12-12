package com.github.miltonlibraryassistant.mcp.entity.searching;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class BiomeWriteFramework {
	
	public static void writeBiomeToJSON(BiomeGenBase biome, World par2World) throws IOException{
		//declare write object and read object 
		JSONObject biomeData = new JSONObject(); 
		JSONParser parser = new JSONParser(); 
		
		int dangerRating = 0;  
		
		if(!(par2World.isRemote)){
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
				
				
				 Set keys = jsonFileRead.keySet();
				 Object[] jsonReadArray = keys.toArray(new Object[jsonFileRead.size()]); 
				for(int i = 0; i < jsonFileRead.size(); i++){
					Object entity = jsonReadArray[i]; 
					String entityJSON = (String) entity;
					JSONObject entityJSONData = (JSONObject) jsonFileRead.get(entityJSON); 
					String entityBiome = (String) entityJSONData.get("biome"); 
					if(entityBiome.equals(biome.biomeName)){
						Long readdangerRating = (Long) entityJSONData.get("danger");  
						if(readdangerRating > 0){
							dangerRating++; 
						}
					}
				}
				
				}catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//add biome to json data for output
		biomeData.put("Biome", biome.biomeName);
		biomeData.put("Danger", dangerRating);
		
		if(!(par2World.isRemote)){
			String world = par2World.getSaveHandler().getWorldDirectoryName();
			biomeData.put("World", world); 
			
			try {
				//Read biome from json
				
				JSONObject jsonFileRead = new JSONObject(); 
				
				GridSearchFramework.testFileExists("biomes.json"); 
				
				BufferedReader br = new BufferedReader(new FileReader("biomes.json"));     
				if (!(br.readLine() == null)) {
					Object fileRead = parser.parse(new FileReader ("biomes.json"));
					jsonFileRead = (JSONObject) fileRead; 
				}
				br.close(); 

				JSONObject readQuadrantData = (JSONObject) jsonFileRead.get(biome.biomeName); 
				
				//if the biome is not currently stored in the file, write to the file with the newly added biome 
				if(readQuadrantData == null){
						jsonFileRead.put(biome.biomeName, biomeData);
						GridSearchFramework.writeJSON(jsonFileRead, "biomes.json"); 
				}
				else{
					//saving this part of the method for later
				}	
				
				}catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
