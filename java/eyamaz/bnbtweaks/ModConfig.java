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
	public static final String fixExtraTiCIngotsPerOreKey = "fixExtraTiCIngotsPerOre";
	public static boolean fixExtraTiCIngotsPerOre = false;

	public static final String fixHostileWorldsBossSuffocatingKey = "fixHostileWorldsBossSuffocating";
	public static boolean fixHostileWorldsBossSuffocating = false;

	public static final String makeMobSpawnersIgnoreLightLevelsKey = "makeMobSpawnersIgnoreLightLevels";
	public static boolean makeMobSpawnersIgnoreLightLevels = false;

	public static final String makePortalsSolidToFluidsKey = "makePortalsSolidToFluids";
	public static boolean makePortalsSolidToFluids = false;

	public static final String fixNetherCrashKey = "fixNetherCrash";
	public static boolean fixNetherCrash = false;

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

		if (config.containsKey(fixExtraTiCIngotsPerOreKey))
		{
			String stringPatchExtraTicRecipeHandler = config.get(fixExtraTiCIngotsPerOreKey);
			fixExtraTiCIngotsPerOre = (stringPatchExtraTicRecipeHandler.equalsIgnoreCase("true"));
		}
		else
		{
			config.put(fixExtraTiCIngotsPerOreKey, "true");
			fixExtraTiCIngotsPerOre = true;
		}

		if (config.containsKey(fixHostileWorldsBossSuffocatingKey))
		{
			String stringPatchHostileWorldsMapGenSchematics = config.get(fixHostileWorldsBossSuffocatingKey);
			fixHostileWorldsBossSuffocating = (stringPatchHostileWorldsMapGenSchematics.equalsIgnoreCase("true"));
		}
		else
		{
			config.put(fixHostileWorldsBossSuffocatingKey, "true");
			fixHostileWorldsBossSuffocating = true;
		}

		if (config.containsKey(makeMobSpawnersIgnoreLightLevelsKey))
		{
			String stringpatchMinecraftMobSpawnerLogic = config.get(makeMobSpawnersIgnoreLightLevelsKey);
			makeMobSpawnersIgnoreLightLevels = (stringpatchMinecraftMobSpawnerLogic.equalsIgnoreCase("true"));
		}
		else
		{
			config.put(makeMobSpawnersIgnoreLightLevelsKey, "true");
			makeMobSpawnersIgnoreLightLevels = true;
		}

		if (config.containsKey(makePortalsSolidToFluidsKey))
		{
			String stringpatchMinecraftMaterialPortal = config.get(makePortalsSolidToFluidsKey);
			makePortalsSolidToFluids = (stringpatchMinecraftMaterialPortal.equalsIgnoreCase("true"));
		}
		else
		{
			config.put(makePortalsSolidToFluidsKey, "true");
			makePortalsSolidToFluids = true;
		}

		if (config.containsKey(fixNetherCrashKey))
		{
			String stringpatchMinecraftChunkProviderServer = config.get(fixNetherCrashKey);
			fixNetherCrash = (stringpatchMinecraftChunkProviderServer.equalsIgnoreCase("true"));
		}
		else
		{
			config.put(fixNetherCrashKey, "true");
			fixNetherCrash = true;
		}
	}

	public static void saveMap(File file, Map<String, String> map)
	{
		properties.putAll(config);
		try
		{
			properties.store(new FileOutputStream(file), null);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void loadMap(File file, Map<String, String> map)
	{
		try
		{
			properties.load(new FileInputStream(file));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		for (String key : properties.stringPropertyNames())
		{
			config.put(key, properties.get(key).toString());
		}
	}
}
