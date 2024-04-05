package forestry.modules;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public interface IClientModuleHandler extends ISidedModuleHandler {

	default void registerSprites(TextureStitchEvent.Pre event) {
	}

	default void handleSprites(TextureStitchEvent.Post event) {
	}

	default void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
	}

	default void bakeModels(ModelEvent.BakingCompleted event) {
	}

	default void registerReloadListeners(RegisterClientReloadListenersEvent event) {
	}

	default void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
	}

	default void setupClient(FMLClientSetupEvent event) {
	}

	default void setupLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
	}

	default void setupRenderers(EntityRenderersEvent.RegisterRenderers event) {
	}

	default void registerBlockColors(RegisterColorHandlersEvent.Block event) {
	}

	default void registerItemColors(RegisterColorHandlersEvent.Item event) {
	}
}
