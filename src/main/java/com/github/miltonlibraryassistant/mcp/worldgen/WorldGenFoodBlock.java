package com.github.miltonlibraryassistant.mcp.worldgen;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenFoodBlock extends WorldGenerator
{

    //copied from the pumpkin world gen with a few things changed about 
    public boolean generate(World p_76484_1_, Random p_76484_2_, int p_76484_3_, int p_76484_4_, int p_76484_5_)
    {
        for (int l = 0; l < 64; ++l)
        {
            int i1 = p_76484_3_ + p_76484_2_.nextInt(8) - p_76484_2_.nextInt(8);
            int j1 = p_76484_4_ + p_76484_2_.nextInt(4) - p_76484_2_.nextInt(4);
            int k1 = p_76484_5_ + p_76484_2_.nextInt(8) - p_76484_2_.nextInt(8);

            if (p_76484_1_.isAirBlock(i1, j1, k1) && p_76484_1_.getBlock(i1, j1 - 1, k1) == Blocks.grass && com.github.miltonlibraryassistant.mcp.MainMCP.blockFoodBlock.canPlaceBlockAt(p_76484_1_, i1, j1, k1))
            {
                p_76484_1_.setBlock(i1, j1, k1, com.github.miltonlibraryassistant.mcp.MainMCP.blockFoodBlock, p_76484_2_.nextInt(4), 2);
            }
        }

        return true;
    }
}