/*
 * Copyright (c) 2014, John Campbell and other contributors.  All rights reserved.
 *
 * This file is part of Tectonicus. It is subject to the license terms in the LICENSE file found in
 * the top-level directory of this distribution.  The full list of project contributors is contained
 * in the AUTHORS file found in the same location.
 *
 */

package tectonicus.renderer;

import java.util.HashMap;
import java.util.Map;

import tectonicus.rasteriser.Mesh;
import tectonicus.rasteriser.Rasteriser;
import tectonicus.rasteriser.Texture;

public class Geometry
{
	public enum MeshType
	{
		Solid,
		AlphaTest,
		Transparent
	};
	
	private final Rasteriser rasteriser;
	
	private Mesh baseMesh;
	private Mesh alphaTestMesh;
	private Mesh transparentMesh;
	
	private Map<MeshType, Map<Texture, Mesh>> meshes;
	
	public Geometry(Rasteriser rasteriser, Texture texture)
	{
		this.rasteriser = rasteriser;
		
		// Notes:
		//	base vertices generally around 20k-30k
		//	alpha generally around 2-3k, often lower, occasionally up to 5k
		//	transparent often low, but goes to around 10k when ocean visible
		
		// At the moment, with all three at 50k max, 100 loaded geometry chunks comes to 514Mb
		
		baseMesh = rasteriser.createMesh(texture);
		alphaTestMesh = rasteriser.createMesh(texture);
		transparentMesh = rasteriser.createMesh(texture);
		
		meshes = new HashMap<Geometry.MeshType, Map<Texture, Mesh>>();
		
		meshes.put(MeshType.Solid, new HashMap<Texture, Mesh>());
		meshes.put(MeshType.AlphaTest, new HashMap<Texture, Mesh>());
		meshes.put(MeshType.Transparent, new HashMap<Texture, Mesh>());
	}
	
	public void destroy()
	{
		baseMesh.destroy();
		alphaTestMesh.destroy();
		transparentMesh.destroy();
		
		for (Map<Texture, Mesh> meshMap : meshes.values())
			for (Mesh m : meshMap.values())
				m.destroy();
	}
	
	// TODO: Refactor to remove these
	public Mesh getBaseMesh() { return baseMesh; }
	public Mesh getAlphaTestMesh() { return alphaTestMesh; }
	public Mesh getTransparentMesh() { return transparentMesh; }
	
	public Mesh getMesh(Texture texture, MeshType type)
	{
		Mesh result = null;
		
		Map<Texture, Mesh> meshList = meshes.get(type);
		
		result = meshList.get(texture);
		if (result == null)
		{
			result = rasteriser.createMesh(texture);
			meshList.put(texture, result);
		}
		
		return result;
	}
	
	public void finalise()
	{
		baseMesh.finalise();
		alphaTestMesh.finalise();
		transparentMesh.finalise();
		
		for (Map<Texture, Mesh> meshMap : meshes.values())
			for (Mesh m : meshMap.values())
				m.finalise();
	}
	
	public void drawSolidSurfaces(final float xOffset, final float yOffset, final float zOffset)
	{
		baseMesh.bind();
		baseMesh.draw(xOffset, yOffset, zOffset);
		
		Map<Texture, Mesh> solidMeshes = meshes.get(MeshType.Solid);
		for (Mesh m : solidMeshes.values())
		{
			m.bind();
			m.draw(xOffset, yOffset, zOffset);
		}
	}
	
	public void drawAlphaTestedSurfaces(final float xOffset, final float yOffset, final float zOffset)
	{
		alphaTestMesh.bind();
		alphaTestMesh.draw(xOffset, yOffset, zOffset);
		
		Map<Texture, Mesh> alphaTestMeshes = meshes.get(MeshType.AlphaTest);
		for (Mesh m : alphaTestMeshes.values())
		{
			m.bind();
			m.draw(xOffset, yOffset, zOffset);
		}
	}
	
	public void drawTransparentSurfaces(final float xOffset, final float yOffset, final float zOffset)
	{
		transparentMesh.bind();
		transparentMesh.draw(xOffset, yOffset, zOffset);
		
		Map<Texture, Mesh> transparentMeshes = meshes.get(MeshType.Transparent);
		for (Mesh m : transparentMeshes.values())
		{
			m.bind();
			m.draw(xOffset, yOffset, zOffset);
		}
	}
	
	public long getMemorySize()
	{
		final int baseSize = baseMesh.getMemorySize() + alphaTestMesh.getMemorySize() + transparentMesh.getMemorySize();
		
		int size = 0;
		
		for (Map<Texture, Mesh> meshMap : meshes.values())
			for (Mesh m : meshMap.values())
				size += m.getMemorySize();
		
		return baseSize + size;
	}

	public void printGeometryStats()
	{
		final int baseVerts = countVertices(MeshType.Solid);
		final int alphaTestVerts = countVertices(MeshType.AlphaTest);
		final int transparentVerts = countVertices(MeshType.Transparent);
		
		System.out.println("Geometry:");
		System.out.println("\tbase vertices: "+ (baseMesh.getTotalVertices() + baseVerts));
		System.out.println("\talpha vertices: "+ (alphaTestMesh.getTotalVertices() + alphaTestVerts));
		System.out.println("\ttransparent vertices: "+ (transparentMesh.getTotalVertices() + transparentVerts));
	}
	
	private int countVertices(MeshType type)
	{
		int vertCount = 0;
		
		Map<Texture, Mesh> subMeshes = meshes.get(type);
		for (Mesh m : subMeshes.values())
		{
			vertCount += m.getTotalVertices();
		}
		
		return vertCount;
	}
}
