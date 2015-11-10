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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.server.FMLServerHandler;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class GridSearchFramework {
	
	public static QuadrantPoint getQuadrant(double posX, double posZ){
		//Dividing by 20 then subtracting the remainder to get the quadrant. 
		double XPosRounded = Math.round((posX / 20)) * 1d / 1d; 
		double ZPosRounded = Math.round((posZ / 20)) * 1d / 1d; 
		return new QuadrantPoint(XPosRounded, ZPosRounded); 
	}
	
	public static Block getQuadrantCenter(double XPosRounded, double ZPosRounded, World par3World){
		//Finding the quadrant's center by multiplying the quadrant # by 20 to get the actual quadrant length, and then adding 10 to get the center. 
		double XPosCenter = (20 * XPosRounded) + 10;
		double ZPosCenter = (20 * ZPosRounded) + 10;
		return par3World.getTopBlock((int) XPosRounded, (int) ZPosRounded); 
	}
	
	public static void writeQuadrantToJSON(QuadrantPoint quadrant, World par2World) throws IOException{
			//declare write object and read object 
			JSONObject quadrantData = new JSONObject(); 
			JSONParser parser = new JSONParser(); 
			
			//add x and z pos to json data for output
			quadrantData.put("XPos", quadrant.X); 
			quadrantData.put("ZPos", quadrant.Z); 
			if(!(par2World.isRemote)){
				String world = par2World.getSaveHandler().getWorldDirectoryName();
				quadrantData.put("World", world); 
				
				try {
					//Read x and y pos from json
					
					JSONObject jsonFileRead = new JSONObject(); 
					
					BufferedReader br = new BufferedReader(new FileReader("quadrants.json"));     
					if (!(br.readLine() == null)) {
						Object fileRead = parser.parse(new FileReader ("quadrants.json"));
						jsonFileRead = (JSONObject) fileRead; 
					}
					br.close(); 

					Double readXPos = (Double) jsonFileRead.get("XPos"); 
					Double readZPos = (Double) jsonFileRead.get("ZPos");
					String readWorld = (String) jsonFileRead.get("World"); 
					
					//if the quadrant is not currently stored in the file, write to the file with the newly added quadrant 
					if(quadrantData.get("World") != jsonFileRead.get("World")){
						if(readXPos != null && readZPos != null){
							if(!(readXPos == quadrant.X && readZPos == quadrant.Z)){
								jsonFileRead.put((Double.toString(quadrant.X) + "--" + Double.toString(quadrant.Z) + world), quadrantData);
								writeJSON(jsonFileRead); 
							}
						}
						else{
							jsonFileRead.put((Double.toString(quadrant.X) + "--" + Double.toString(quadrant.Z) + world), quadrantData);
							writeJSON(jsonFileRead); 
						}	
					}
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}


	}
	
	/**Writes given JSONObject to JSON file. Takes a JSONObject as a parameter.**/
	public static void writeJSON(JSONObject json) throws IOException{
		//writing data to json file
		StringWriter out = new StringWriter(); 
		json.writeJSONString(out); 
		String jsonText = out.toString(); 
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("quadrants.json"), "utf-8")); 
		writer.write(jsonText); 
		writer.close(); 
	}
}
