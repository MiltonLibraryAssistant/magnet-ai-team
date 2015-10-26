package com.github.miltonlibraryassistant.mcp.entity.searching;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.json.simple.JSONObject;

import net.minecraft.block.Block;

public class GridSearchFramework {
	
	public static QuadrantPoint getQuadrant(double posX, double posZ){
		double XPosRounded = Math.round((posX % 20) + 1) * 1d / 1d; 
		double ZPosRounded = Math.round((posZ % 20) + 1) * 1d / 1d; 
		return new QuadrantPoint(XPosRounded, ZPosRounded); 
	}
	
	public static void writeQuadrantToJSON(QuadrantPoint quadrant) throws IOException{
		JSONObject quadrantData = new JSONObject(); 
		quadrantData.put("XPos", quadrant.X); 
		quadrantData.put("ZPos", quadrant.Z); 
		StringWriter out = new StringWriter(); 
		quadrantData.writeJSONString(out); 
		String jsonText = out.toString(); 
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("quadrants.json"), "utf-8")); 
		writer.write(jsonText); 
		writer.close(); 
	}
}
