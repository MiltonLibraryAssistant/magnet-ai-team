package com.github.miltonlibraryassistant.mcp.entity.searching.pathing;

import net.minecraft.pathfinding.PathPoint;

public class QuadrantPathPoint extends PathPoint{

    /** The x coordinate of this point */
    public final int xCoord;
    /** The z coordinate of this point */
    public final int zCoord;
    /** A hash of the coordinates used to identify this point */
    private final int hash;
    /** The index of this point in its assigned path */
    int index = -1;
    /** The distance along the path to this point */
    float totalPathDistance;
    /** The linear distance to the next point */
    float distanceToNext;
    /** The distance to the target */
    float distanceToTarget;
    /** The point preceding this in its assigned path */
    public QuadrantPathPoint previous;

    float CurrentCost = 0; 
    QuadrantPathPoint next; 
    public boolean isFirst;
    
	public QuadrantPathPoint(int X, int Z) {
		//because these are nonvertical path points, y is not used
		super(X, 0, Z);
        this.xCoord = X;
        this.zCoord = Z;
        this.hash = makeHash(X, 0, Z);
	}
	
	@Override
    public boolean equals(Object p_equals_1_)
    {
		QuadrantPathPoint pathpoint = (QuadrantPathPoint)p_equals_1_;
         return this.xCoord == pathpoint.xCoord && this.zCoord == pathpoint.zCoord;
    }
}
