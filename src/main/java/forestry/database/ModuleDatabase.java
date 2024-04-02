package forestry.database;

import net.minecraft.client.gui.screens.MenuScreens;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.database.features.DatabaseContainers;
import forestry.database.gui.GuiDatabase;
import forestry.database.network.PacketRegistryDatabase;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

@ForestryModule(modId = Constants.MOD_ID, moduleID = ForestryModuleUids.DATABASE, name = "Database", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.database.description")
public class ModuleDatabase extends BlankForestryModule {
	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerGuiFactories() {
		MenuScreens.register(DatabaseContainers.DATABASE.menuType(), GuiDatabase::new);
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryDatabase();
	}
}
