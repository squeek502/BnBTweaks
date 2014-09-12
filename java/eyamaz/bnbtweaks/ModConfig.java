package eyamaz.bnbtweaks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ModConfig
{
	public static boolean patchLycanitesMobsEntityCreatureBase = false;
	public static boolean patchExtraTicRecipeHandler = false;
	public static boolean patchHostileWorldsMapGenSchematics = false;
	public static boolean patchMinecraftMobSpawnerLogic = false;
	public static boolean patchMinecraftMaterialPortal = false;
	public static boolean patchMinecraftEntityMob = false;
	public static boolean patchMinecraftChunkProviderServer = false;

	public static Map<String, String> config = new HashMap<String, String>();
	private static Properties properties = new Properties();
	private static File configFile;

	public static void init(File mcLocation)
	{
		configFile = new File(mcLocation + "/config/" + ModInfo.MODID + ".cfg");

		if (configFile != null)
		{
			loadMap(configFile, config);

			if (config != null)
			{
				readConfig();
			}
			saveMap(configFile, config);
		}

	}

	public static void readConfig()
	{
		String patchExtraTicRecipeHandlerKey = "patchExtraTicRecipeHandler";
		String patchHostileWorldsMapGenSchematicsKey = "patchHostileWorldsMapGenSchematics";
		String patchLycanitesMobsEntityCreatureBaseKey = "patchLycanitesMobsEntityCreatureBase";
		String patchMinecraftMobSpawnerLogicKey = "patchMinecraftMobSpawnerLogic";
		String patchMinecraftMaterialPortalKey = "patchMinecraftMaterialPortal";
		String patchMinecraftEntityMobKey = "patchMinecraftEntityMob";
		String patchMinecraftChunkProviderServerKey = "patchMinecraftChunkProviderServer";

		if (config.containsKey(patchExtraTicRecipeHandlerKey))
		{
			String stringPatchExtraTicRecipeHandler = config.get(patchExtraTicRecipeHandlerKey);
			patchExtraTicRecipeHandler = (stringPatchExtraTicRecipeHandler.equalsIgnoreCase("true"));
		} else
		{
			config.put(patchExtraTicRecipeHandlerKey, "true");
			patchExtraTicRecipeHandler = true;
		}

		if (config.containsKey(patchHostileWorldsMapGenSchematicsKey))
		{
			String stringPatchHostileWorldsMapGenSchematics = config.get(patchHostileWorldsMapGenSchematicsKey);
			patchHostileWorldsMapGenSchematics = (stringPatchHostileWorldsMapGenSchematics.equalsIgnoreCase("true"));
		} else
		{
			config.put(patchHostileWorldsMapGenSchematicsKey, "true");
			patchHostileWorldsMapGenSchematics = true;
		}

		if (config.containsKey(patchLycanitesMobsEntityCreatureBaseKey))
		{
			String stringpatchLycanitesMobsEntityCreatureBase = config.get(patchLycanitesMobsEntityCreatureBaseKey);
			patchLycanitesMobsEntityCreatureBase = (stringpatchLycanitesMobsEntityCreatureBase.equalsIgnoreCase("true"));
		} else
		{
			config.put(patchLycanitesMobsEntityCreatureBaseKey, "true");
			patchLycanitesMobsEntityCreatureBase = true;
		}

		if (config.containsKey(patchMinecraftMobSpawnerLogicKey))
		{
			String stringpatchMinecraftMobSpawnerLogic = config.get(patchMinecraftMobSpawnerLogicKey);
			patchMinecraftMobSpawnerLogic = (stringpatchMinecraftMobSpawnerLogic.equalsIgnoreCase("true"));
		} else
		{
			config.put(patchMinecraftMobSpawnerLogicKey, "true");
			patchMinecraftMobSpawnerLogic = true;
		}

		if (config.containsKey(patchMinecraftMaterialPortalKey))
		{
			String stringpatchMinecraftMaterialPortal = config.get(patchMinecraftMaterialPortalKey);
			patchMinecraftMaterialPortal = (stringpatchMinecraftMaterialPortal.equalsIgnoreCase("true"));
		} else
		{
			config.put(patchMinecraftMaterialPortalKey, "true");
			patchMinecraftMaterialPortal = true;
		}

		if (config.containsKey(patchMinecraftEntityMobKey))
		{
			String stringpatchMinecraftEntityMob = config.get(patchMinecraftEntityMobKey);
			patchMinecraftEntityMob = (stringpatchMinecraftEntityMob.equalsIgnoreCase("true"));
		} else
		{
			config.put(patchMinecraftEntityMobKey, "true");
			patchMinecraftEntityMob = true;
		}

		if (config.containsKey(patchMinecraftChunkProviderServerKey))
		{
			String stringpatchMinecraftChunkProviderServer = config.get(patchMinecraftChunkProviderServerKey);
			patchMinecraftChunkProviderServer = (stringpatchMinecraftChunkProviderServer.equalsIgnoreCase("true"));
		} else
		{
			config.put(patchMinecraftChunkProviderServerKey, "true");
			patchMinecraftChunkProviderServer = true;
		}
	}

	public static void saveMap(File file, Map<String, String> map)
	{
		properties.putAll(config);
		try
		{
			properties.store(new FileOutputStream(file), null);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void loadMap(File file, Map<String, String> map)
	{
		try
		{
			properties.load(new FileInputStream(file));
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		for (String key : properties.stringPropertyNames())
		{
			config.put(key, properties.get(key).toString());
		}
	}
}
