package com.github.miltonlibraryassistant.mcp.entity.searching;

import java.io.BufferedWriter;
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
import java.text.DecimalFormat;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.minecraft.block.Block;

public class GridSearchFramework {
	
	public static QuadrantPoint getQuadrant(double posX, double posZ){
		//Dividing by 20 then subtracting the remainder to get the quadrant. 
		double XPosRounded = Math.round((posX / 20) - (posX % 20)) * 1d / 1d; 
		double ZPosRounded = Math.round((posZ / 20) - (posX % 20)) * 1d / 1d; 
		return new QuadrantPoint(XPosRounded, ZPosRounded); 
	}
	
	public static QuadrantCenter getQuadrantCenter(XPosRounded,ZPosRounded){
		//Finding the quadrant's center by multiplying the quadrant # by 20 to get the actual quadrant length, and then adding 10 to get the center. 
		XPosCenter = (20 * XPosRounded) + 10;
		ZPosCenter = (20 * ZPosRounded) + 10;
		return new QuadrantCenter(XPosCenter, ZPosCenter);
	}
	
	public static void writeQuadrantToJSON(QuadrantPoint quadrant) throws IOException{
		//declare write object and read object 
		JSONObject quadrantData = new JSONObject(); 
		JSONParser parser = new JSONParser(); 
		
		//add x and z pos to json data for output
		quadrantData.put("XPos", quadrant.X); 
		quadrantData.put("ZPos", quadrant.Z); 
		
		try {
			//Read x and y pos from json
			Object fileRead = parser.parse(new FileReader ("quadrants.json"));
			JSONObject jsonFileRead = (JSONObject) fileRead; 
			Double readXPos = (Double) jsonFileRead.get("XPos"); 
			Double readZPos = (Double) jsonFileRead.get("ZPos"); 
			
			if(!(readXPos == quadrant.X && readZPos == quadrant.Z)){
				//if the quadrant is not currently stored in the file, write to the file with the newly added quadrant
				writeJSON(quadrantData); 
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
