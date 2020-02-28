/*
 * Copyright (c) 2020, John Campbell and other contributors.  All rights reserved.
 *
 * This file is part of Tectonicus. It is subject to the license terms in the LICENSE file found in
 * the top-level directory of this distribution.  The full list of project contributors is contained
 * in the AUTHORS file found in the same location.
 *
 */

package tectonicus.rasteriser.processing;

import com.jogamp.opengl.GL2;
import org.joml.Vector3f;
import org.joml.Vector4f;
import processing.core.PGraphics;
import processing.core.PGraphics3D;
import tectonicus.rasteriser.Mesh;
import tectonicus.rasteriser.Texture;
import tectonicus.util.Colour4f;

import java.util.Arrays;

public class ProcessingMesh implements Mesh
{
	private final PGraphics3D graphics;
	private final ProcessingTexture texture;
	
	private float[] xPositions, yPositions, zPositions;
	private float[] reds, greens, blues, alphas;
	private float[] uCoords, vCoords;
	
	private int numVerts;
	
	public ProcessingMesh(ProcessingTexture texture, PGraphics3D graphics)
	{
		this.graphics = graphics;
		this.texture = texture;
		
		final int initialCapacity = 64;
		
		xPositions = new float[initialCapacity];
		yPositions = new float[initialCapacity];
		zPositions = new float[initialCapacity];
		
		reds = new float[initialCapacity];
		greens = new float[initialCapacity];
		blues = new float[initialCapacity];
		alphas = new float[initialCapacity];
		
		uCoords = new float[initialCapacity];
		vCoords = new float[initialCapacity];
	}
	
	public void destroy() {}
	
	private void ensureCapacity(final int numVerts)
	{
		xPositions = Arrays.copyOf(xPositions, numVerts);
		yPositions = Arrays.copyOf(yPositions, numVerts);
		zPositions = Arrays.copyOf(zPositions, numVerts);
		
		reds = Arrays.copyOf(reds, numVerts);
		greens = Arrays.copyOf(greens, numVerts);
		blues = Arrays.copyOf(blues, numVerts);
		alphas = Arrays.copyOf(alphas, numVerts);
		
		uCoords = Arrays.copyOf(uCoords, numVerts);
		vCoords = Arrays.copyOf(vCoords, numVerts);
	}
	
	@Override
	public void finalise()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Texture getTexture()
	{
		return null;
	}

	@Override
	public void bind()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bind(GL2 gl2)
	{
		// TODO Auto-generated method stub

	}
	
	@Override
	public void draw(final float xOffset, final float yOffset, final float zOffset)
	{
		assert (texture.getPImage() != null);
		
		// TODO: Apply offset to transform matrix rather than doing addition manually
		
		graphics.beginShape(PGraphics.QUADS);
		
		graphics.texture(texture.getPImage());
		graphics.noSmooth();
		graphics.noStroke();
		graphics.noLights();
		
		for (int i=0; i<numVerts; i++)
		{
			graphics.tint(reds[i], greens[i], blues[i], alphas[i]);
		//	graphics.fill(reds[i], greens[i], blues[i], alphas[i]);			
		//	graphics.fill(reds[i]*255.0f, greens[i]*255.0f, blues[i]*255.0f, alphas[i]*255.0f);
			
		//	graphics.vertex(xPositions[i], yPositions[i], zPositions[i], uCoords[i], vCoords[i]);
			graphics.vertex(xPositions[i] + xOffset, yPositions[i] + yOffset, zPositions[i] + zOffset, uCoords[i], vCoords[i]);
		}
		
		graphics.endShape();
	}

	@Override
	public void draw(final float xOffset, final float yOffset, final float zOffset, GL2 gl2) {

	}

	@Override
	public int getMemorySize()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTotalVertices()
	{
		return numVerts;
	}

	@Override
	public void addVertex(Vector3f position, Vector4f colour, float u, float v)
	{
		if (numVerts+1 == xPositions.length)
			ensureCapacity(numVerts + 1024);
		
		xPositions[numVerts] = position.x;
		yPositions[numVerts] = position.y;
		zPositions[numVerts] = position.z;
		
		reds[numVerts] = colour.x;
		greens[numVerts] = colour.y;
		blues[numVerts] = colour.z;
		alphas[numVerts] = colour.w;
		
		uCoords[numVerts] = u;
		vCoords[numVerts] = v;
		
		numVerts++;
	}

	@Override
	public void addVertex(Vector3f position, final float u, final float v)
	{
		addVertex(position, new Vector4f(1, 1, 1, 1), u, v);
	}
	
	@Override
	public void addVertex(Vector3f position, Colour4f color, final float u, final float v)
	{
		
	}
	
}
