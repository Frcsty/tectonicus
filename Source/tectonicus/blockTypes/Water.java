/*
 * Copyright (c) 2012-2020, John Campbell and other contributors.  All rights reserved.
 *
 * This file is part of Tectonicus. It is subject to the license terms in the LICENSE file found in
 * the top-level directory of this distribution.  The full list of project contributors is contained
 * in the AUTHORS file found in the same location.
 *
 */

package tectonicus.blockTypes;

import java.util.Random;

import org.joml.Vector3f;
import org.joml.Vector4f;

import tectonicus.BlockContext;
import tectonicus.BlockType;
import tectonicus.BlockTypeRegistry;
import tectonicus.configuration.LightFace;
import tectonicus.rasteriser.Mesh;
import tectonicus.rasteriser.MeshUtil;
import tectonicus.raw.RawChunk;
import tectonicus.renderer.Geometry;
import tectonicus.texture.SubTexture;

import static tectonicus.Version.VERSION_4;

public class Water implements BlockType
{
	private final String name;
	private SubTexture subTexture;	
	
	public Water(String name, SubTexture subTexture, int frame)
	{
		this.name = name;
		
		final int texHeight = subTexture.texture.getHeight();
		final int texWidth = subTexture.texture.getWidth();
		final int numTiles = texHeight/texWidth;

		if(numTiles > 1 && frame == 0)
		{
			Random rand = new Random();
			frame = rand.nextInt(numTiles)+1;
		}

		if (subTexture.texturePackVersion == VERSION_4)
			this.subTexture = subTexture;
		else
			this.subTexture = new SubTexture(subTexture.texture, subTexture.u0, subTexture.v0+(float)((frame-1)*texWidth)/texHeight, subTexture.u1, subTexture.v0+(float)(frame*texWidth)/texHeight);
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
		return true;
	}
	
	@Override
	public void addInteriorGeometry(int x, int y, int z, BlockContext world, BlockTypeRegistry registry, RawChunk rawChunk, Geometry geometry)
	{
		addEdgeGeometry(x, y, z, world, registry, rawChunk, geometry);
	}
	
	@Override
	public void addEdgeGeometry(final int x, final int y, final int z, BlockContext world, BlockTypeRegistry registry, RawChunk rawChunk, Geometry geometry)
	{
		Mesh mesh = geometry.getMesh(subTexture.texture, Geometry.MeshType.Transparent);
		
		final float alpha = 0.8f;
		final float internalAlpha = 0.3f;
		final float waterLevel = 14.0f/16.0f;
		
		final float topLight = world.getLight(rawChunk.getChunkCoord(), x, y+1, z, LightFace.Top);
		final float northLight = world.getLight(rawChunk.getChunkCoord(), x-1, y, z, LightFace.NorthSouth);
		final float southLight = world.getLight(rawChunk.getChunkCoord(), x+1, y, z, LightFace.NorthSouth);
		final float eastLight = world.getLight(rawChunk.getChunkCoord(), x, y, z-1, LightFace.EastWest);
		final float westLight = world.getLight(rawChunk.getChunkCoord(), x, y, z+1, LightFace.EastWest);
		
		BlockType above = world.getBlockType(rawChunk.getChunkCoord(), x, y+1, z);
		BlockType aboveNorth = world.getBlockType(rawChunk.getChunkCoord(), x, y+1, z+1);
		BlockType aboveSouth = world.getBlockType(rawChunk.getChunkCoord(), x, y+1, z-1);
		BlockType aboveEast = world.getBlockType(rawChunk.getChunkCoord(), x+1, y+1, z);
		BlockType aboveWest = world.getBlockType(rawChunk.getChunkCoord(), x-1, y+1, z);
		if(!above.getName().equals("Ice") && !above.isWater() && !aboveNorth.isWater() && !aboveSouth.isWater() && !aboveEast.isWater() && !aboveWest.isWater())  // Only water blocks that don't have another water block above them should be lower
		{
			BlockType north = world.getBlockType(rawChunk.getChunkCoord(), x-1, y, z);
			if (!north.isWater())
			{
				MeshUtil.addQuad(mesh,	new Vector3f(x,		y+waterLevel,	z),
										new Vector3f(x,		y+waterLevel,	z+1),
										new Vector3f(x,		y,		z+1),
										new Vector3f(x,		y,		z),
										new Vector4f(northLight, northLight, northLight, alpha),
										subTexture); 
			}

			BlockType south = world.getBlockType(rawChunk.getChunkCoord(), x+1, y, z);
			if (!south.isWater())
			{
				MeshUtil.addQuad(mesh,	new Vector3f(x+1,		y+waterLevel,		z+1),
										new Vector3f(x+1,		y+waterLevel,	z),
										new Vector3f(x+1,		y,	z),
										new Vector3f(x+1,		y,	z+1),
										new Vector4f(southLight, southLight, southLight, alpha),
										subTexture); 
			}

			BlockType east = world.getBlockType(rawChunk.getChunkCoord(), x, y, z-1);
			if (!east.isWater())
			{
				MeshUtil.addQuad(mesh,	new Vector3f(x+1,	y+waterLevel,	z),
										new Vector3f(x,		y+waterLevel,	z),
										new Vector3f(x,		y,		z),
										new Vector3f(x+1,	y,		z),
										new Vector4f(eastLight, eastLight, eastLight, alpha),
										subTexture); 
			}

			BlockType west = world.getBlockType(rawChunk.getChunkCoord(), x, y, z+1);
			if (!west.isWater())
			{
				MeshUtil.addQuad(mesh,	new Vector3f(x,		y+waterLevel,	z+1),
										new Vector3f(x+1,	y+waterLevel,	z+1),
										new Vector3f(x+1,	y,		z+1),
										new Vector3f(x,		y,		z+1),
										new Vector4f(westLight, westLight, westLight, alpha),
										subTexture); 
			}
			
		//	if (!above.isWater())
			
				final float aboveAlpha = above.isWater() ? internalAlpha : alpha;
				MeshUtil.addQuad(mesh,	new Vector3f(x,		y+waterLevel,	z),
										new Vector3f(x+1,	y+waterLevel,	z),
										new Vector3f(x+1,	y+waterLevel,	z+1),
										new Vector3f(x,		y+waterLevel,	z+1),
										new Vector4f(topLight, topLight, topLight, aboveAlpha),
										subTexture);
			
			
			BlockType below = world.getBlockType(rawChunk.getChunkCoord(), x, y+1, z);
			if (!below.isWater())
			{
				MeshUtil.addQuad(mesh,	new Vector3f(x,		y,	z+1),
										new Vector3f(x+1,	y,	z+1),
										new Vector3f(x+1,	y,	z),
										new Vector3f(x,		y,	z),
										new Vector4f(topLight, topLight, topLight, alpha),
										subTexture);
			}
		}
		else
		{
		//	final int northId = world.getBlockId(rawChunk.getChunkCoord(), x-1, y, z);
		//	BlockType north = registry.find(northId);
			BlockType north = world.getBlockType(rawChunk.getChunkCoord(), x-1, y, z);
			if (!north.isWater())
			{
				MeshUtil.addQuad(mesh,	new Vector3f(x,		y+1,	z),
										new Vector3f(x,		y+1,	z+1),
										new Vector3f(x,		y,		z+1),
										new Vector3f(x,		y,		z),
										new Vector4f(northLight, northLight, northLight, alpha),
										subTexture); 
			}
			
		//	final int southId = world.getBlockId(rawChunk.getChunkCoord(), x+1, y, z);
		//	BlockType south = registry.find(southId);
			BlockType south = world.getBlockType(rawChunk.getChunkCoord(), x+1, y, z);
			if (!south.isWater())
			{
				MeshUtil.addQuad(mesh,	new Vector3f(x+1,		y+1,		z+1),
										new Vector3f(x+1,		y+1,	z),
										new Vector3f(x+1,		y,	z),
										new Vector3f(x+1,		y,	z+1),
										new Vector4f(southLight, southLight, southLight, alpha),
										subTexture); 
			}
			
		//	final int eastId = world.getBlockId(rawChunk.getChunkCoord(), x, y, z-1);
		//	BlockType east = registry.find(eastId);
			BlockType east = world.getBlockType(rawChunk.getChunkCoord(), x, y, z-1);
			if (!east.isWater())
			{
				MeshUtil.addQuad(mesh,	new Vector3f(x+1,	y+1,	z),
										new Vector3f(x,		y+1,	z),
										new Vector3f(x,		y,		z),
										new Vector3f(x+1,	y,		z),
										new Vector4f(eastLight, eastLight, eastLight, alpha),
										subTexture); 
			}
			
		//	final int westId = world.getBlockId(rawChunk.getChunkCoord(), x, y, z+1);
		//	BlockType west = registry.find(westId);
			BlockType west = world.getBlockType(rawChunk.getChunkCoord(), x, y, z+1);
			if (!west.isWater())
			{
				MeshUtil.addQuad(mesh,	new Vector3f(x,		y+1,	z+1),
										new Vector3f(x+1,	y+1,	z+1),
										new Vector3f(x+1,	y,		z+1),
										new Vector3f(x,		y,		z+1),
										new Vector4f(westLight, westLight, westLight, alpha),
										subTexture); 
			}
			
			
		//	final int aboveId = world.getBlockId(rawChunk.getChunkCoord(), x, y+1, z);
		//	BlockType above = registry.find(aboveId);
			
		//	if (!above.isWater())
			
				final float aboveAlpha = above.isWater() ? internalAlpha : alpha;
				MeshUtil.addQuad(mesh,	new Vector3f(x,		y+1,	z),
										new Vector3f(x+1,	y+1,	z),
										new Vector3f(x+1,	y+1,	z+1),
										new Vector3f(x,		y+1,	z+1),
										new Vector4f(topLight, topLight, topLight, aboveAlpha),
										subTexture);
			
			
		//	final int belowId = world.getBlockId(rawChunk.getChunkCoord(), x, y+1, z);
		//	BlockType below = registry.find(belowId);
			BlockType below = world.getBlockType(rawChunk.getChunkCoord(), x, y+1, z);
			if (!below.isWater())
			{
				MeshUtil.addQuad(mesh,	new Vector3f(x,		y,	z+1),
										new Vector3f(x+1,	y,	z+1),
										new Vector3f(x+1,	y,	z),
										new Vector3f(x,		y,	z),
										new Vector4f(topLight, topLight, topLight, alpha),
										subTexture);
			}
		}
	}
	
}
