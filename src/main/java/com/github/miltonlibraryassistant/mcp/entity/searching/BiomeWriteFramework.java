package com.github.miltonlibraryassistant.mcp.entity.searching;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
		
		//add x and z pos to json data for output
		biomeData.put("Biome", biome.biomeName); 
		
		if(!(par2World.isRemote)){
			String world = par2World.getSaveHandler().getWorldDirectoryName();
			biomeData.put("World", world); 
			
			try {
				//Read x and y pos from json
				
				JSONObject jsonFileRead = new JSONObject(); 
				
				BufferedReader br = new BufferedReader(new FileReader("biomes.json"));     
				if (!(br.readLine() == null)) {
					Object fileRead = parser.parse(new FileReader ("biomes.json"));
					jsonFileRead = (JSONObject) fileRead; 
				}
				br.close(); 

				JSONObject readQuadrantData = (JSONObject) jsonFileRead.get(biome.biomeName); 
				
				//if the quadrant is not currently stored in the file, write to the file with the newly added quadrant 
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
