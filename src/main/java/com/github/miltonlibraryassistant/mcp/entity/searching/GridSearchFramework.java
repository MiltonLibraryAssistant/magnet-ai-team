package com.github.miltonlibraryassistant.mcp.entity.searching;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.text.DecimalFormat;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.github.miltonlibraryassistant.mcp.References;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.server.FMLServerHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeManager.BiomeEntry;

public class GridSearchFramework {
	
	public static QuadrantPoint getQuadrant(double posX, double posZ){
		//Dividing by 20 then subtracting the remainder to get the quadrant. 
		double XPosRounded = Math.floor((posX / 20)); 
		double ZPosRounded = Math.floor((posZ / 20)); 
		return new QuadrantPoint(XPosRounded, ZPosRounded); 
	}
	
	public static BlockPosition getQuadrantCenter(double XPosRounded, double ZPosRounded, World par3World){
		/**Finding the quadrant's center by multiplying the quadrant # by 20 to get the actual quadrant length
		and then adding 10 to get the center.**/
		double XPosCenter;
		double ZPosCenter; 
		
		if(XPosRounded >= 0){
			XPosCenter = (20 * XPosRounded) + 10;			
		}else{
			XPosCenter = (20 * XPosRounded - 10); 
		}
		
		if(ZPosRounded >= 0){
			ZPosCenter = (20 * ZPosRounded) + 10;			
		}else{
			ZPosCenter = (20 * ZPosRounded - 10); 
		}
		return getTopBlock((int) XPosCenter, (int) ZPosCenter, par3World); 
	}
	
	public static BiomeGenBase getBiomeFromBlock(BlockPosition block, World world){
		//Retrieving biome based on coordinates
		return world.getBiomeGenForCoords((int) block.x, (int) block.z); 
	} 
	
	public static void writeQuadrantToJSON(QuadrantPoint quadrant, World par2World, boolean isDeath) throws IOException{
			//declare write object and read object 
			JSONObject quadrantData = new JSONObject(); 
			JSONParser parser = new JSONParser(); 
			
			//add x and z pos to json data for output
			quadrantData.put("XPos", quadrant.X); 
			quadrantData.put("ZPos", quadrant.Z); 
			BlockPosition quadrantCenter = getQuadrantCenter(quadrant.X, quadrant.Z, par2World); 
			BiomeGenBase biome = getBiomeFromBlock(quadrantCenter, par2World); 
			quadrantData.put("biome", biome.biomeName); 
			String blockposition = quadrantCenter.x + ", " + quadrantCenter.y + ", " + quadrantCenter.z; 
			quadrantData.put("quadrantcenter", blockposition); 
			int waterOrFood = isWaterOrFoodInQuadrant(quadrant, par2World); 
			quadrantData.put("waterandfood", waterOrFood); 
			BiomeWriteFramework.writeBiomeToJSON(biome, par2World); 
			
			if(!(par2World.isRemote)){
				String world = par2World.getSaveHandler().getWorldDirectoryName();
				quadrantData.put("World", world); 
				
				try {
					
					//Read x and y pos from json
					JSONObject jsonFileRead = new JSONObject(); 
					
					testFileExists("quadrants.json"); 
					BufferedReader br = new BufferedReader(new FileReader("quadrants.json"));     
					if (!(br.readLine() == null)) {
						Object fileRead = parser.parse(new FileReader ("quadrants.json"));
						jsonFileRead = (JSONObject) fileRead; 
					}
					br.close(); 

					JSONObject readQuadrantData = (JSONObject) jsonFileRead.get((Double.toString(quadrant.X) + "--" + Double.toString(quadrant.Z) + world)); 
					
					/**if the quadrant is not currently stored in the file, 
					write to the file with the newly added quadrant**/ 
					if(readQuadrantData == null){
						//Read biomes file to determine base danger rating of biome
						JSONObject biomesFileRead = new JSONObject(); 
						
						testFileExists("biomes.json"); 
						BufferedReader biomesbr = new BufferedReader(new FileReader("biomes.json"));     
						if (!(biomesbr.readLine() == null)) {
							Object fileRead = parser.parse(new FileReader ("biomes.json"));
							biomesFileRead = (JSONObject) fileRead; 
						}
						biomesbr.close(); 
						
						JSONObject readBiomeData = (JSONObject) biomesFileRead.get(biome.biomeName);
						Long biomeDangerRating = (Long) readBiomeData.get("Danger");
						Long modifiedDangerRating = biomeDangerRating; 
						if(isDeath){
							//if the entity has died, increase the danger rating by one from the biome rating
							modifiedDangerRating = biomeDangerRating + 1; 
						}
						quadrantData.put("Danger", modifiedDangerRating); 
						
						jsonFileRead.put((Double.toString(quadrant.X) + "--" + Double.toString(quadrant.Z) + world), quadrantData);
						writeJSON(jsonFileRead, "quadrants.json"); 
					}
					else {
						Long readDangerRating = (Long) readQuadrantData.get("Danger");
						
						if(isDeath){
							//if the entity has died, increase the danger rating by one from the rating in the file
							quadrantData.put("Danger", readDangerRating + 1); 
						}else{
							quadrantData.put("Danger", readDangerRating); 
						}
						
						if(readQuadrantData != quadrantData){
							//if the stored quadrant data mismatches the new quadrant data, overwrite
							jsonFileRead.replace((Double.toString(quadrant.X) + "--" + Double.toString(quadrant.Z) + world), quadrantData);
							writeJSON(jsonFileRead, "quadrants.json"); 
						}
					}	
					
					}catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	
	public static void testFileExists(String filename) throws IOException {
		File outputfile = new File(filename);
		if(!outputfile.exists()) {
		    outputfile.createNewFile();
		} 	
	}

	/**Writes given JSONObject to JSON file. Takes a JSONObject as a parameter.**/
	public static void writeJSON(JSONObject json, String filename) throws IOException{
		//writing data to json file
		StringWriter out = new StringWriter(); 
		json.writeJSONString(out); 
		String jsonText = out.toString(); 
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8")); 
		writer.write(jsonText); 
		writer.close(); 
	}
	
    public static BlockPosition getTopBlock(int x, int z, World par3World)
    {
        int k;

        for (k = 20; !par3World.isAirBlock(x, k + 1, z); ++k)
        {
            ;
        }

        return new BlockPosition(x, k, z);
    }
    

    public static int isTopFoodBlockOrWaterBlock(int x, int z, World world){
    	//returns 0 for nothing, 1 for food, 2 far water
    	BlockPosition topBlock = getTopBlock(x, z, world);
    	Block topBlockAsBlock = world.getBlock((int) topBlock.x, (int) topBlock.y, (int) topBlock.z); 
    	if(topBlockAsBlock.getUnlocalizedName() == References.modId + ":FoodBlock"){
    		return 1; 
    	}
    	if(topBlockAsBlock.getMaterial() == Material.water){
    		return 2; 
    	}
    	return 0; 
    }
    
    public static int isWaterOrFoodInQuadrant(QuadrantPoint quadrant, World par2World){
    	BlockPosition quadrantCorner = getQuadrantCenter(quadrant.X, quadrant.Z, par2World); 
    	int TrueReturnInt = 0; 
    	quadrantCorner.x = quadrantCorner.x - 10; 
    	quadrantCorner.z = quadrantCorner.z - 10; 
    	for(int i = 0; i <= 20; i++){
    		for(int j = 0; j <= 20; j++){
	    		int returnInt = isTopFoodBlockOrWaterBlock((int) quadrantCorner.x + j, (int) quadrantCorner.z + i, par2World);
	    		switch (TrueReturnInt) {
	    		case 0:
	    			//nothing
	    				switch (returnInt) {
	    				case 0:
	    					//nothing
	    					break;
	    				case 1: 
	    					//food
	    					TrueReturnInt = 1;
	    					break;
	    				case 2:
	    					//water
	    					TrueReturnInt = 2;
	    					break;
	    				}
	    			break;
	    		case 1: 
	    			//food
					switch (returnInt) {
					case 0:
						//nothing + food previously
						break;
					case 2:
						//water + food previously
						TrueReturnInt = 3;
						break;
					}
	    			break;
	    		case 2:
	    			//water
					switch (returnInt) {
					case 0:
						//nothing + water previously 
						break;
					case 1: 
						//food + water previously
						TrueReturnInt = 3;
						break;
					}
	    			break;
	    		}
	    		
	    		if(TrueReturnInt == 3){
	    			return TrueReturnInt; 
	    		}
    		}
    	}
    	return TrueReturnInt; 
    }
    
}
