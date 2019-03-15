/*
 * Copyright (c) 2012-2019, John Campbell and other contributors.  All rights reserved.
 *
 * This file is part of Tectonicus. It is subject to the license terms in the LICENSE file found in
 * the top-level directory of this distribution.  The full list of project contributors is contained
 * in the AUTHORS file found in the same location.
 *
 */

package tectonicus.configuration;

import java.io.File;
import java.nio.file.Path;

import static tectonicus.configuration.ParseUtil.*;

import lombok.experimental.UtilityClass;
import tectonicus.Minecraft;
import tectonicus.configuration.Configuration.Dimension;
import tectonicus.configuration.Configuration.Mode;
import tectonicus.configuration.Configuration.RasteriserType;
import tectonicus.configuration.Configuration.RenderStyle;

@UtilityClass
public class CommandLineParser
{
	public static MutableConfiguration parseCommandLine(String[] args) throws Exception
	{
		ArgParser parser = new ArgParser(args);
		
		if (parser.isEmpty())
			return null;
		
		MutableConfiguration config = new MutableConfiguration();
		MutableMap map = new MutableMap("Map0");
		MutableLayer layer = new MutableLayer("LayerA", map.getId());
		map.addLayer(layer);
		config.addMap(map);
		
		System.out.println("Parsing old style command line...");
		
		Mode mode = parseMode( parser.getString("mode", "") );
		config.setMode(mode);
		
		RenderStyle renderStyle = parseRenderStyle( parser.getString("renderStyle", "") );
		layer.setRenderStyle(renderStyle);
		
		// If rendering caves then default the light style to cave style
		if (renderStyle == RenderStyle.Cave)
			layer.setLightStyle(LightStyle.Cave);
		
		// Find the world dir
		File worldDir;
		if (parser.hasValue("worldDir"))
		{
			String path = parser.get("worldDir");
			if (path.equals("1") || path.equals("2") || path.equals("3") || path.equals("4") || path.equals("5"))
			{
				worldDir = new File(Minecraft.findMinecraftDir(), "saves/World"+path);
			}
			else
			{
				worldDir = new File(path);
			}
		}
		else
		{
			worldDir = new File(".");
		}
		map.setWorldDir(worldDir);
		
		final int cameraAngle = parseCameraAngle( parser.getString("cameraAngle", "") );
		map.setCameraAngleDeg(cameraAngle);
		
		final int cameraElevation = parseElevationAngle( parser.getString("cameraElevation", "") );
		map.setCameraElevationDeg(cameraElevation);
		
		// Find the dimension to render
		Dimension dimension = parseDimension( parser.getString("dimension", "") );
		map.setDimension(dimension);
		
		RasteriserType rasteriser = parseRasteriserType( parser.getString("rasteriser", "") );
		config.setRasteriserType(rasteriser);
		
		config.setIsVerbose( parseIsVerbose( parser.getString("verbose", "") ) );
		
		File minecraftJar;
		if (parser.hasValue("minecraftJar"))
		{
			minecraftJar = parser.getFile("minecraftJar", new File("."));
		}
		else
		{
			// Not specified, try and guess where it is
			
			minecraftJar = Minecraft.findMinecraftJar();
		}
		config.setMinecraftJar(minecraftJar);
		
		File texturePack = parser.getFile("texturePack", null);
		config.setTexturePack(texturePack);
		
		final int tileSize = parseTileSize( parser.getString("tileSize", "") );
		config.setTileSize( tileSize );
			
		ImageFormat imageFormat = parseImageFormat( parser.getString("imageFormat", "") );
		layer.setImageFormat(imageFormat);
		
		final float imageCompression = parseImageCompression( parser.getString("imageCompressionLevel", "") );
		layer.setImageCompressionLevel(imageCompression);
		
		SignFilter signFilter = parseSignFilter( parser.getString("signs", "") );
		map.setSignFilter(signFilter);
		
		config.setShowPlayerSpawn( parseShowSpawn( parser.getString("showSpawn", "") ) );
		
		PlayerFilterType playerFilterType = parsePlayerFilterType( parser.getString("players", "") );
		Path playerFilterFile = parsePlayerFilterFile( parser.getString("playerFilterFile", "") );
		PlayerFilter playerFilter = new PlayerFilter(playerFilterType, playerFilterFile, map.getWorldDir().toPath());
		map.setPlayerFilter(playerFilter);
		
		PortalFilterType portalFilterType = parsePortalFilter( parser.getString("portals", "") );
		map.setPortalFilter( new PortalFilter(portalFilterType) );
		
		config.setExtractLwjglNatives( parseExtractLwjglNatives( parser.getString("extractLwjglNatives", "") ) );
		
		config.setEraseOutputDir( parseEraseOutputDir( parser.getString("eraseOutputDir", "") ) );
		
		File outputDir = parseOutputDir( parser.get("outputDir") );
		config.setOutputDir(outputDir);
		
		File logFile = parseLogFile( parser.getString("logFile", "") );
		config.setLogFile(logFile);
		
		config.setUseCache( parseUseCache(parser.getString("useCache", "") ) );
		
		File cacheDir = parseCacheDir(parser.getString("cacheDir", ""), config.outputDir());
		config.setCacheDir(cacheDir);
		
		
		String htmlName = parseOutputHtmlName( parser.getString("outputHtmlName", "") );
		config.setOutputHtmlName(htmlName);
		
		LightStyle lightStyle = parseLightStyle( parser.getString("lighting", "") );
		if (lightStyle != null)
			layer.setLightStyle(lightStyle);
		
		config.setColourDepth( parseColourDepth( parser.getString("colourDepth", "") ) );
		config.setAlphaBits( parseAlphaBits( parser.getString("alphaBits", "") ) );
		config.setNumSamples( parseNumSamples( parser.getString("numSamples", "") ) );
		
		config.setNumZoomLevels( parseNumZoomLevels( parser.getString("numZoomLevels", "") ) );
		
		map.setClosestZoomSize( parseClosestZoomSize( parser.getString("closestZoomSize", "") ) );
		
		map.setNorthDirection( parseNorthDirection( parser.getString("north", "")));
		map.setCustomCompassRose( parser.getString("compassRose", null));
		
		config.setMaxTiles( parseMaxTiles( parser.getString("maxTiles", "") ) );
		
		config.setForceLoadAwt( parseForceLoadAwt( parser.getString("forceLoadAwt", "") ) );
		config.setForce32BitNatives( parseForce32BitNatives( parser.getString("force32BitNatives", "") ) );
		config.setForce64BitNatives( parseForce64BitNatives( parser.getString("force64BitNatives", "") ) );
		
		map.setUseBiomeColours( parseUseBiomeColours( parser.getString("useBiomeColours", "") ) );
		
		config.setSignsInitiallyVisible( parseInitiallyVisible( parser.getString("signsInitiallyVisible", "") ) );
		config.setPlayersInitiallyVisible( parseInitiallyVisible( parser.getString("playersInitiallyVisible", "") ) );
		config.setPortalsInitiallyVisible( parseInitiallyVisible( parser.getString("portalsInitiallyVisible", "") ) );
		config.setBedsInitiallyVisible( parseInitiallyVisible( parser.getString("bedsInitiallyVisible", "") ) );
		config.setSpawnInitiallyVisible( parseInitiallyVisible( parser.getString("spawnInitiallyVisible", "") ) );
		
		final int numDownsampleThreads = parseNumDownsampleThreads( parser.getString("numDownsampleThreads", "") );
		config.setNumDownsampleThreads(numDownsampleThreads);
		
		return config;
	}
}
