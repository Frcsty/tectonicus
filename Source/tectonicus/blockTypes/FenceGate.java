/*
 * Copyright (c) 2012-2020, John Campbell and other contributors.  All rights reserved.
 *
 * This file is part of Tectonicus. It is subject to the license terms in the LICENSE file found in
 * the top-level directory of this distribution.  The full list of project contributors is contained
 * in the AUTHORS file found in the same location.
 *
 */

package tectonicus.blockTypes;

import org.joml.Vector4f;

import tectonicus.BlockContext;
import tectonicus.BlockType;
import tectonicus.BlockTypeRegistry;
import tectonicus.configuration.LightFace;
import tectonicus.rasteriser.Mesh;
import tectonicus.raw.RawChunk;
import tectonicus.renderer.Geometry;
import tectonicus.texture.SubTexture;

public class FenceGate implements BlockType
{
	private final String name;
	private final SubTexture texture;
	
	public FenceGate(String name, SubTexture texture)
	{
		this.name = name;
		this.texture = texture;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public boolean isSolid()
	{
		return false;
	}
	
	@Override
	public boolean isWater()
	{
		return false;
	}
	
	@Override
	public void addInteriorGeometry(int x, int y, int z, BlockContext world, BlockTypeRegistry registry, RawChunk rawChunk, Geometry geometry)
	{
		addEdgeGeometry(x, y, z, world, registry, rawChunk, geometry);
	}
	
	@Override
	public void addEdgeGeometry(final int x, final int y, final int z, BlockContext world, BlockTypeRegistry registry, RawChunk rawChunk, Geometry geometry)
	{
		final int data = rawChunk.getBlockData(x, y, z);
		
		Mesh mesh = geometry.getMesh(texture.texture, Geometry.MeshType.Solid);
		
		Vector4f colour = new Vector4f(1, 1, 1, 1);
		
		final float topLight = world.getLight(rawChunk.getChunkCoord(), x, y, z, LightFace.Top);
		final float northSouthLight = world.getLight(rawChunk.getChunkCoord(), x, y, z, LightFace.NorthSouth);
		final float eastWestLight = world.getLight(rawChunk.getChunkCoord(), x, y, z, LightFace.EastWest);
		
		final int northId = world.getBlockId(rawChunk.getChunkCoord(), x, y, z+1);
		final int southId = world.getBlockId(rawChunk.getChunkCoord(), x, y, z-1);
		final int eastId = world.getBlockId(rawChunk.getChunkCoord(), x+1, y, z);
		final int westId = world.getBlockId(rawChunk.getChunkCoord(), x-1, y, z);
		final BlockType northType = registry.find(northId, 0);
		final BlockType southType = registry.find(southId, 0);
		final BlockType eastType = registry.find(eastId, 0);
		final BlockType westType = registry.find(westId, 0);
		
		
		final boolean open = (data & 0x04) == 0x04;
		final int direction = (data & 0x03);
		
		if ((direction == 1 || direction == 3) && (!(northType instanceof Wall) || !(southType instanceof Wall)) ) // south/north
		{
			// outside posts
			BlockUtil.addBlock(mesh, x, y, z, 7, 5,  0, 2, 11, 2, colour, texture, topLight, northSouthLight, eastWestLight);
			BlockUtil.addBlock(mesh, x, y, z, 7, 5, 14, 2, 11, 2, colour, texture, topLight, northSouthLight, eastWestLight);
			
			// gates
			if (!open)
			{
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	 7, 12,  2,
													 2,  3, 12, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	 7,  6,  2,
													 2,  3, 12, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	 7,  9,  6,
													 2,  3,  4, colour, texture, topLight, northSouthLight, eastWestLight);
			}
			else if (direction == 1) // open north
			{
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	 1, 12,  0,
													 6,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	 1,  6,  0,
													 6,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	 1,  9,  0,
													 2,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	 1, 12, 14,
													 6,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	 1,  6, 14,
													 6,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	 1,  9, 14,
													 2,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
			}
			else // open south
			{
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	 9, 12,  0,
													 6,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	 9,  6,  0,
													 6,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	13,  9,  0,
													 2,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	 9, 12, 14,
													 6,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	 9,  6, 14,
													 6,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	13,  9, 14,
													 2,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
			}
		}
		else if ((direction == 0 || direction == 2) && (!(eastType instanceof Wall) || !(westType instanceof Wall)) )// east/west
		{
			// outside posts
			BlockUtil.addBlock(mesh, x, y, z,  0, 5, 7, 2, 11, 2, colour, texture, topLight, northSouthLight, eastWestLight);
			BlockUtil.addBlock(mesh, x, y, z, 14, 5, 7, 2, 11, 2, colour, texture, topLight, northSouthLight, eastWestLight);
			
			// gates
			if (!open)
			{
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	 2, 12,  7,
													12,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	 2,  6,  7,
													12,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	 6,  9,  7,
													 4,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
			}
			else if (direction == 0) // open west
			{
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	 0, 12,  9,
													 2,  3,  6, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	 0,  6,  9,
													 2,  3,  6, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	 0,  9, 13,
													 2,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	14, 12,  9,
													 2,  3,  6, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	14,  6,  9,
													 2,  3,  6, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	14,  9, 13,
													 2,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
			}
			else // open east
			{
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	 0, 12,  1,
													 2,  3,  6, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	 0,  6,  1,
													 2,  3,  6, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	 0,  9,  1,
													 2,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	14, 12,  1,
													 2,  3,  6, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	14,  6,  1,
													 2,  3,  6, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	14,  9,  1,
													 2,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
			}
		}
		else if ((direction == 1 || direction == 3) && (northType instanceof Wall || southType instanceof Wall) )  //south/north //If fence gate is connected to walls it needs to be lower
		{
			// outside posts
			BlockUtil.addBlock(mesh, x, y, z, 7, 2,  0, 2, 11, 2, colour, texture, topLight, northSouthLight, eastWestLight);
			BlockUtil.addBlock(mesh, x, y, z, 7, 2, 14, 2, 11, 2, colour, texture, topLight, northSouthLight, eastWestLight);
			
			// gates
			if (!open)
			{
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	 7, 9,  2,
													 2,  3, 12, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	 7,  3,  2,
													 2,  3, 12, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	 7,  6,  6,
													 2,  3,  4, colour, texture, topLight, northSouthLight, eastWestLight);
			}
			else if (direction == 1) // open north
			{
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	 1, 9,  0,
													 6,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	 1,  3,  0,
													 6,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	 1,  6,  0,
													 2,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	 1, 9, 14,
													 6,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	 1,  3, 14,
													 6,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	 1,  6, 14,
													 2,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
			}
			else // open south
			{
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	 9, 9,  0,
													 6,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	 9,  3,  0,
													 6,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	13,  6,  0,
													 2,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	 9, 9, 14,
													 6,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	 9,  3, 14,
													 6,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	13,  6, 14,
													 2,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
			}
		}
		else // east/west fence gate connected to walls
		{
			// outside posts
			BlockUtil.addBlock(mesh, x, y, z,  0, 2, 7, 2, 11, 2, colour, texture, topLight, northSouthLight, eastWestLight);
			BlockUtil.addBlock(mesh, x, y, z, 14, 2, 7, 2, 11, 2, colour, texture, topLight, northSouthLight, eastWestLight);
			
			// gates
			if (!open)
			{
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	 2, 9,  7,
													12,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	 2,  3,  7,
													12,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	 6,  6,  7,
													 4,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
			}
			else if (direction == 0) // open west
			{
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	 0, 9,  9,
													 2,  3,  6, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	 0,  3,  9,
													 2,  3,  6, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	 0,  6, 13,
													 2,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	14, 9,  9,
													 2,  3,  6, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	14,  3,  9,
													 2,  3,  6, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	14,  6, 13,
													 2,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
			}
			else // open east
			{
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	 0, 9,  1,
													 2,  3,  6, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	 0,  3,  1,
													 2,  3,  6, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	 0,  6,  1,
													 2,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
				// Top bar
				BlockUtil.addBlock(mesh, x, y, z,	14, 9,  1,
													 2,  3,  6, colour, texture, topLight, northSouthLight, eastWestLight);
				
				// Bottom bar
				BlockUtil.addBlock(mesh, x, y, z,	14,  3,  1,
													 2,  3,  6, colour, texture, topLight, northSouthLight, eastWestLight);

				// Middle
				BlockUtil.addBlock(mesh, x, y, z,	14,  6,  1,
													 2,  3,  2, colour, texture, topLight, northSouthLight, eastWestLight);
			}
		}

		/*
		// Auto-connect to adjacent fences
		
		// Bars are two wide and three high
		
		// North
		final int northId = world.getBlockId(chunk.getChunkCoord(), x-1, y, z);
		if (northId == blockId)
		{
			// Top bar
			BlockUtil.addBlock(mesh, x, y, z,	0, 10, 7,
												8, 3, 2, colour, texture, topLight, northSouthLight, eastWestLight);
			
			// Bottom bar
			BlockUtil.addBlock(mesh, x, y, z,	0, 4, 7,
												8, 3, 2, colour, texture, topLight, northSouthLight, eastWestLight);
		}
		
		// South
		final int southId = world.getBlockId(chunk.getChunkCoord(), x+1, y, z);
		if (southId == blockId)
		{
			// Top bar
			BlockUtil.addBlock(mesh, x, y, z,	8, 10, 7,
												8, 3, 2, colour, texture, topLight, northSouthLight, eastWestLight);
			
			// Bottom bar
			BlockUtil.addBlock(mesh, x, y, z,	8, 4, 7,
												8, 3, 2, colour, texture, topLight, northSouthLight, eastWestLight);
		}
		
		// East
		final int eastId = world.getBlockId(chunk.getChunkCoord(), x, y, z-1);
		if (eastId == blockId)
		{
			// Top bar
			BlockUtil.addBlock(mesh, x, y, z,	7, 10, 0,
												2, 3, 8, colour, texture, topLight, northSouthLight, eastWestLight);
			
			// Bottom bar
			BlockUtil.addBlock(mesh, x, y, z,	7, 4, 0,
												2, 3, 8, colour, texture, topLight, northSouthLight, eastWestLight);
		}
		
		// West
		final int westId = world.getBlockId(chunk.getChunkCoord(), x, y, z+1);
		if (westId == blockId)
		{
			// Top bar
			BlockUtil.addBlock(mesh, x, y, z,	7, 10, 8,
												2, 3, 8, colour, texture, topLight, northSouthLight, eastWestLight);
			
			// Bottom bar
			BlockUtil.addBlock(mesh, x, y, z,	7, 4, 8,
												2, 3, 8, colour, texture, topLight, northSouthLight, eastWestLight);
		}
		*/
	}
}
