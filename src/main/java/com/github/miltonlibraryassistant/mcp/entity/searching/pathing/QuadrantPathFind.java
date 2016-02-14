package com.github.miltonlibraryassistant.mcp.entity.searching.pathing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

import org.apache.commons.lang3.ArrayUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.github.miltonlibraryassistant.mcp.entity.searching.GridSearchFramework;

public class QuadrantPathFind {

	public static QuadrantPathPoint[] arrayOpenPoints = new QuadrantPathPoint[1000]; 
	public static QuadrantPathPoint[] arrayClosedPoints = new QuadrantPathPoint[1000]; 
	public static int openPointsLength = 0;
	public static int closedPointsLength = 0;
	public static int incrementing = 0; 
 
	public static PathEntity getPath(int targeterXCoord, int targeterZCoord, int targetXCoord, int targetZCoord, World world) throws IOException, ParseException{
		Arrays.fill(arrayOpenPoints, null);
		Arrays.fill(arrayClosedPoints, null);
		openPointsLength = 0; 
		closedPointsLength = 0; 
		incrementing = 0; 
		PathEntity pathTo = generateChildNodes(targeterXCoord, targeterZCoord, targetXCoord, targetZCoord, world); 
		return pathTo; 
	}
	
	/**Searches the 8 adjacent quadrants to the given quadrant, and adds them to the open list. If they are the target, returns a full path.
	 * @throws IOException 
	 * @throws ParseException **/ 
	public static PathEntity generateChildNodes(int targeterXCoord, int targeterZCoord, int targetXCoord, int targetZCoord, World world) throws IOException, ParseException{
		while((openPointsLength > 0 &&  incrementing <= 90)|| incrementing == 0){
			incrementing++; 
			QuadrantPathPoint prevNode = new QuadrantPathPoint(targeterXCoord, targeterZCoord);
			QuadrantPathPoint target = new QuadrantPathPoint(targetXCoord, targetZCoord); 
			
			QuadrantPathPoint x = new QuadrantPathPoint(targeterXCoord - 1, targeterZCoord + 1); 
			if(x.equals(target)){
				PathEntity returnValue = finish(x); 
				return returnValue;  
			}
			addPointToList(x.xCoord, x.yCoord, x.zCoord, prevNode, world, target); 
			
			x = new QuadrantPathPoint(targeterXCoord, targeterZCoord + 1);
			if(x.equals(target)){
				PathEntity returnValue = finish(x); 
				return returnValue;  
			}
			addPointToList(x.xCoord, x.yCoord, x.zCoord, prevNode, world, target); 
			
			x = new QuadrantPathPoint(targeterXCoord + 1, targeterZCoord + 1); 
			if(x.equals(target)){
				PathEntity returnValue = finish(x); 
				return returnValue; 
			}
			addPointToList(x.xCoord, x.yCoord, x.zCoord, prevNode, world, target); 
			
			x = new QuadrantPathPoint(targeterXCoord - 1, targeterZCoord);
			if(x.equals(target)){
				PathEntity returnValue = finish(x); 
				return returnValue; 
			}
			addPointToList(x.xCoord, x.yCoord, x.zCoord, prevNode, world, target); 
			
			x = new QuadrantPathPoint(targeterXCoord + 1, targeterZCoord);
			if(x.equals(target)){
				PathEntity returnValue = finish(x); 
				return returnValue; 
			}
			addPointToList(x.xCoord, x.yCoord, x.zCoord, prevNode, world, target); 
			
			x = new QuadrantPathPoint(targeterXCoord - 1, targeterZCoord - 1);
			if(x.equals(target)){
				PathEntity returnValue = finish(x); 
				return returnValue; 
			}
			addPointToList(x.xCoord, x.yCoord, x.zCoord, prevNode, world, target); 
			
			x = new QuadrantPathPoint(targeterXCoord, targeterZCoord - 1);
			if(x.equals(target)){
				PathEntity returnValue = finish(x); 
				return returnValue; 
			}
			addPointToList(x.xCoord, x.yCoord, x.zCoord, prevNode, world, target); 
			
			x = new QuadrantPathPoint(targeterXCoord + 1, targeterZCoord - 1);
			if(x.equals(target)){
				PathEntity returnValue = finish(x); 
				return returnValue; 
			}
			addPointToList(x.xCoord, x.yCoord, x.zCoord, prevNode, world, target); 
			
			QuadrantPathPoint greatestF = pickQuadrantWithLeastCost(); 
			if(greatestF != null){
				return generateChildNodes(greatestF.xCoord, greatestF.zCoord, targetXCoord, targetZCoord, world);

			}
		}
		return null; 
	}
	
	/**If the given point is not the target, adds the given point to the list of open points. If the given point is the target, executes the finish function.
	 * @throws IOException 
	 * @throws ParseException **/ 
	public static void addPointToList(int X, int Y, int Z, QuadrantPathPoint prevNode, World world, QuadrantPathPoint target) throws IOException, ParseException{
		QuadrantPathPoint newPathPoint = new QuadrantPathPoint(X, Z); 
		
		/**Adds the previous point in the path as a field in the node.**/ 
		newPathPoint.previous = prevNode; 
		
		//Read quadrants file to determine classification of quadrant + adjust cost accordingly
		JSONObject quadrantsFileRead = new JSONObject(); 
		JSONParser parser = new JSONParser(); 
		
		GridSearchFramework.testFileExists("quadrants.json"); 
		BufferedReader biomesbr = new BufferedReader(new FileReader("quadrants.json"));     
		if (!(biomesbr.readLine() == null)) {
			Object fileRead = parser.parse(new FileReader ("quadrants.json"));
			quadrantsFileRead = (JSONObject) fileRead; 
		}
		biomesbr.close(); 
		
		String worldName = world.getSaveHandler().getWorldDirectoryName();
		JSONObject readQuadrantData = (JSONObject) quadrantsFileRead.get((Double.toString(X) + "--" + Double.toString(Z) + worldName));
		Long biomeClassification = null; 
		if(readQuadrantData != null){
			biomeClassification = (Long) readQuadrantData.get("Classification");
		}
		if(biomeClassification != null){
			//classification: 0 = safe, 1 = true neutral, 2 = neutral + safe, 3 = neutral + danger, 4 = danger
			if(biomeClassification == 0){
				newPathPoint.CurrentCost = -2; 
			}
			if(biomeClassification == 2){
				newPathPoint.CurrentCost = -1; 
			}
			if(biomeClassification == 3){
				newPathPoint.CurrentCost = 1; 
			}
			if(biomeClassification == 4){
				//won't add to list if danger is too great (if is target, will have already exited)
				return; 
			}
		}
		newPathPoint.CurrentCost = newPathPoint.CurrentCost + newPathPoint.distanceTo(target);
		
		/**Tests whether this node has been checked previously with a lower f, and if so, exits the method.**/ 
		for(int a = 0; a < closedPointsLength; a++ ){
			if (arrayClosedPoints[a] != null){
				if(newPathPoint.equals(arrayClosedPoints[a])){
					if(newPathPoint.CurrentCost > arrayClosedPoints[a].CurrentCost){ 
						return; 
					}
				}
			}
		}
		for(int a = 0; a < openPointsLength; a++ ){
			if(arrayOpenPoints[a] != null){
				if(newPathPoint.equals(arrayOpenPoints[a])){
					if(newPathPoint.CurrentCost > arrayOpenPoints[a].CurrentCost){
						return; 
					}
				}
			}
			else{
				System.out.println("the array value" + a + "was equal to null + openPointsLength = " + openPointsLength); 
			}
		}
			
		
		arrayOpenPoints[openPointsLength] = newPathPoint; 
		openPointsLength++;
	}
	
	/**Returns a PathEntity from a given ending point.**/ 
	public static PathEntity finish(QuadrantPathPoint last) {
        int i = 1;
        QuadrantPathPoint pathpoint2 = last;
        
        /**Returns the length of the path**/ 
        for (pathpoint2 = last; pathpoint2.previous != null; pathpoint2 = pathpoint2.previous)
        {
            ++i;
        }
       
        QuadrantPathPoint[] apathpoint = new QuadrantPathPoint[i];
        pathpoint2 = last;
        --i;

        for (apathpoint[i] = last; pathpoint2.previous != null; apathpoint[i] = pathpoint2)
        {
            pathpoint2 = pathpoint2.previous;
            --i;
        }
        
        return new PathEntity(apathpoint);
		
	}


	/**Pulls block with the least f off the open list, then adds it to the closed list.**/ 
	public static QuadrantPathPoint pickQuadrantWithLeastCost() {
		float CurrentLowestCost = 10000000; 
		int a = 1; 
		int ArrayID = 0; 
		for(int i = 0; i < openPointsLength; i++){
			if(arrayOpenPoints[i] != null){
				if(arrayOpenPoints[i].CurrentCost < CurrentLowestCost){
					ArrayID = i; 
					CurrentLowestCost = arrayOpenPoints[i].CurrentCost; 
				}
			}
			else{
				System.out.println("the array value" + a + "was equal to null"); 
			}
		}
		QuadrantPathPoint greatestF = arrayOpenPoints[ArrayID]; 
		arrayClosedPoints[closedPointsLength] = greatestF;  
		closedPointsLength++;
		ArrayUtils.removeElement(arrayOpenPoints, ArrayID); 
		openPointsLength--; 
		return greatestF;  

	}
	

}

/** 
 * HI! Looks like you found my super-secret notes on how this pathfinder works.
 * The whole thing is pretty messy, and you probably shouldn't be using my code for reference at all.
 * If you want to know more about A* from people who can explain it better than I can, check out this article: http://web.mit.edu/eranki/www/tutorials/search/
 * 
 * f= heuristic distance + actual distance

-The entity searches the quadrants surrounding it + adds all those quadrants to the open list. *
-The entity examines the f of each quadrant. *
-The entity picks the quadrant with the least f and pulls it off. *
-Generate the successors of that quadrant and determine whether any of them are the target *
-Have we checked any of the successors before? Take those off. *
-Test whether there is an instance of the same successor with lower f in the open or closed list.*
-If all these conditions check out, throw the successor on the open list. *
-Continue generating and checking quadrants on the open list. *
-Once the target is found, string its parent nodes together into an EntityPath.*
-Pass the EntityPath to the entity.*
-Have the entity read the EntityPath.*
-periodically (every 4 blocks?) determine whether the target has moved, and if so, recalculate everything.*
**/ 
