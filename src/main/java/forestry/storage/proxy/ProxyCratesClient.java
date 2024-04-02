package forestry.storage.proxy;

import net.minecraft.client.resources.model.ModelResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import forestry.api.storage.EnumBackpackType;
import forestry.core.config.Constants;
import forestry.core.utils.ForgeUtils;
import forestry.modules.IClientModuleHandler;
import forestry.storage.BackpackMode;
import forestry.storage.models.BackpackItemModel;
import forestry.storage.models.CrateModel;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ProxyCratesClient extends ProxyCrates implements IClientModuleHandler {
	@Override
	public void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
		for (EnumBackpackType backpackType : EnumBackpackType.values()) {
			for (BackpackMode mode : BackpackMode.values()) {
				event.register(backpackType.getLocation(mode));
			}
		}

		event.register(new ModelResourceLocation(Constants.MOD_ID, "crate-filled", "inventory"));
	}

	@Override
	public void registerReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(CrateModel.Loader.INSTANCE);
		event.registerReloadListener(BackpackItemModel.Loader.INSTANCE);
	}

	@Override
	public void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
		event.register(CrateModel.Loader.LOCATION.getPath(), CrateModel.Loader.INSTANCE);
		event.register(BackpackItemModel.Loader.LOCATION.getPath(), BackpackItemModel.Loader.INSTANCE);
	}
}
