package eyamaz.bnbtweaks.asm;

import java.io.File;
import java.util.Map;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import eyamaz.bnbtweaks.ModConfig;

@IFMLLoadingPlugin.SortingIndex(100)
@TransformerExclusions("eyamaz.bnbtweaks")
public class ASMPlugin implements IFMLLoadingPlugin
{
	@Override
	public String[] getASMTransformerClass()
	{
		return new String[]{ClassTransformer.class.getName()};
	}

	@Override
	public String getModContainerClass()
	{
		return null;
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		ModConfig.init((File) data.get("mcLocation"));
	}
}
