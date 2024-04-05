/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/

package forestry;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import genetics.Genetics;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

import forestry.api.climate.ClimateManager;
import forestry.api.core.ForestryAPI;
import forestry.api.core.ISetupListener;
import forestry.api.core.ISpriteRegistry;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.IHygroregulatorRecipe;
import forestry.api.recipes.IMoistenerRecipe;
import forestry.api.recipes.ISolderRecipe;
import forestry.api.recipes.ISqueezerContainerRecipe;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.IStillRecipe;
import forestry.arboriculture.loot.CountBlockFunction;
import forestry.arboriculture.loot.GrafterLootModifier;
import forestry.core.EventHandlerCore;
import forestry.core.circuits.CircuitRecipe;
import forestry.core.climate.ClimateFactory;
import forestry.core.climate.ClimateRoot;
import forestry.core.climate.ClimateStateHelper;
import forestry.core.config.Constants;
import forestry.core.data.ForestryAdvancementProvider;
import forestry.core.data.ForestryBackpackTagProvider;
import forestry.core.data.ForestryBlockModelProvider;
import forestry.core.data.ForestryBlockStateProvider;
import forestry.core.data.ForestryBlockTagsProvider;
import forestry.core.data.ForestryFluidTagsProvider;
import forestry.core.data.ForestryItemModelProvider;
import forestry.core.data.ForestryItemTagsProvider;
import forestry.core.data.ForestryLootModifierProvider;
import forestry.core.data.ForestryLootTableProvider;
import forestry.core.data.ForestryMachineRecipeProvider;
import forestry.core.data.ForestryRecipeProvider;
import forestry.core.errors.EnumErrorCode;
import forestry.core.errors.ErrorStateRegistry;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.loot.ConditionLootModifier;
import forestry.core.loot.OrganismFunction;
import forestry.core.models.ModelBlockCached;
import forestry.core.network.NetworkHandler;
import forestry.core.network.PacketHandlerServer;
import forestry.core.proxy.Proxies;
import forestry.core.proxy.ProxyClient;
import forestry.core.proxy.ProxyCommon;
import forestry.core.proxy.ProxyRender;
import forestry.core.proxy.ProxyRenderClient;
import forestry.core.recipes.HygroregulatorRecipe;
import forestry.core.render.ColourProperties;
import forestry.core.render.ForestrySpriteUploader;
import forestry.core.render.TextureManagerForestry;
import forestry.core.utils.ForgeUtils;
import forestry.core.worldgen.VillagerJigsaw;
import forestry.factory.recipes.CarpenterRecipe;
import forestry.factory.recipes.CentrifugeRecipe;
import forestry.factory.recipes.FabricatorRecipe;
import forestry.factory.recipes.FabricatorSmeltingRecipe;
import forestry.factory.recipes.FermenterRecipe;
import forestry.factory.recipes.MoistenerRecipe;
import forestry.factory.recipes.SqueezerContainerRecipe;
import forestry.factory.recipes.SqueezerRecipe;
import forestry.factory.recipes.StillRecipe;
import forestry.modules.ForestryModules;
import forestry.modules.ModuleManager;
import forestry.modules.features.ModFeatureRegistry;

import genetics.api.alleles.IAllele;
import genetics.utils.AlleleUtils;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

/**
 * Forestry Minecraft Mod
 *
 * @author SirSengir
 */
@Mod(Constants.MOD_ID)
public class Forestry {
	public Forestry() {
		ForestryAPI.errorStateRegistry = new ErrorStateRegistry();
		ClimateManager.climateRoot = ClimateRoot.getInstance();
		ClimateManager.climateFactory = ClimateFactory.INSTANCE;
		ClimateManager.stateHelper = ClimateStateHelper.INSTANCE;
		EnumErrorCode.init();

		ModuleManager moduleManager = ModuleManager.getInstance();
		ForestryAPI.moduleManager = moduleManager;
		moduleManager.registerContainers(new ForestryModules());
		ModuleManager.runSetup();
		NetworkHandler networkHandler = new NetworkHandler();
		IEventBus modEventBus = ForgeUtils.modBus();
		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::registerCapabilities);
		//		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		modEventBus.addListener(this::processIMCMessages);
		modEventBus.addListener(this::clientSetupRenderers);
		modEventBus.addListener(this::gatherData);
		MinecraftForge.EVENT_BUS.register(EventHandlerCore.class);
		MinecraftForge.EVENT_BUS.register(this);
		Proxies.render = DistExecutor.safeRunForDist(() -> ProxyRenderClient::new, () -> ProxyRender::new);
		Proxies.common = DistExecutor.safeRunForDist(() -> ProxyClient::new, () -> ProxyCommon::new);
        // Modules must be set up before Genetics API
        ModuleManager.getModuleHandler().runSetup();
        Genetics.initGenetics(modEventBus);

		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> new Client(modEventBus, networkHandler)::run);
		modEventBus.addListener(EventPriority.NORMAL, false, FMLCommonSetupEvent.class, evt -> networkHandler.serverPacketHandler());

		// Features must be created before registry events fire
		ModuleManager.getModuleHandler().createFeatures();
	}

	public void clientSetupRenderers(EntityRenderersEvent.RegisterRenderers event) {
		ModuleManager.getModuleHandler().registerGuiFactories();

		for (ModFeatureRegistry value : ModFeatureRegistry.getRegistries().values()) {
			value.clientSetupRenderers(event);
		}
	}

	@Nullable
	private static PacketHandlerServer packetHandler;

	public static PacketHandlerServer getPacketHandler() {
		Preconditions.checkNotNull(packetHandler);
		return packetHandler;
	}

	private void setup(FMLCommonSetupEvent event) {
		// Forestry's villager houses
		event.enqueueWork(VillagerJigsaw::init);

		packetHandler = new PacketHandlerServer();

		// Register event handler
		//TODO - DistExecutor
		callSetupListeners(true);
		ModuleManager.getModuleHandler().runPreInit();
		Proxies.render.registerItemAndBlockColors();
		//TODO put these here for now
		ModuleManager.getModuleHandler().runInit();
		callSetupListeners(false);
		ModuleManager.getModuleHandler().runPostInit();
	}

	private void registerCapabilities(RegisterCapabilitiesEvent event) {
		ModuleManager.getModuleHandler().registerCapabilities(event::register);
	}

	//TODO: Move to somewhere else
	private void callSetupListeners(boolean start) {
		for (IAllele allele : AlleleUtils.getAlleles()) {
			if (allele instanceof ISetupListener listener) {
				if (start) {
					listener.onStartSetup();
				} else {
					listener.onFinishSetup();
				}
			}
		}
	}

	private void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        ForestryBlockTagsProvider blockTagsProvider = new ForestryBlockTagsProvider(generator, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), new ForestryAdvancementProvider(generator));
        generator.addProvider(event.includeServer(), new ForestryItemTagsProvider(generator, blockTagsProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ForestryBackpackTagProvider(generator, blockTagsProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ForestryFluidTagsProvider(generator, existingFileHelper));
        generator.addProvider(event.includeServer(), new ForestryLootTableProvider(generator));
        generator.addProvider(event.includeServer(), new ForestryBlockStateProvider(generator, existingFileHelper));
        generator.addProvider(event.includeServer(), new ForestryBlockModelProvider(generator, existingFileHelper));
        generator.addProvider(event.includeServer(), new ForestryItemModelProvider(generator, existingFileHelper));
        generator.addProvider(event.includeServer(), new ForestryRecipeProvider(generator));
        generator.addProvider(event.includeServer(), new ForestryMachineRecipeProvider(generator));
        generator.addProvider(event.includeServer(), new ForestryLootModifierProvider(generator));
    }

	@OnlyIn(Dist.CLIENT)
	private record Client(IEventBus modEventBus, NetworkHandler networkHandler) implements Runnable {
		@Override
		public void run() {
			modEventBus.addListener((RegisterColorHandlersEvent.Block x) -> {
				Minecraft minecraft = Minecraft.getInstance();
				ForestrySpriteUploader spriteUploader = new ForestrySpriteUploader(minecraft.textureManager, TextureManagerForestry.LOCATION_FORESTRY_TEXTURE, "gui");
				TextureManagerForestry.getInstance().init(spriteUploader);
				ResourceManager resourceManager = minecraft.getResourceManager();
				if (resourceManager instanceof ReloadableResourceManager reloadableManager) {
					reloadableManager.registerReloadListener(ColourProperties.INSTANCE);
					reloadableManager.registerReloadListener(GuiElementFactory.INSTANCE);
					reloadableManager.registerReloadListener(spriteUploader);
				}
				//EntriesCategory.registerSearchTree();
				ModuleManager.getModuleHandler().runClientInit();

			});
			modEventBus.addListener(EventPriority.NORMAL, false, FMLLoadCompleteEvent.class, fmlLoadCompleteEvent -> networkHandler.clientPacketHandler());
		}
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Constants.MOD_ID)
	public static class RegistryEvents {

		private RegistryEvents() {
		}

		// should honestly go in Common
		@SubscribeEvent(priority = EventPriority.LOW)
		public static void createObjects(RegisterEvent event) {
			ModuleManager.getModuleHandler().registerObjects(event);
		}

		@SubscribeEvent
		public static void register(RegisterEvent event) {
			event.register(Registry.RECIPE_SERIALIZER_REGISTRY, helper -> {
				helper.register(new ResourceLocation(ICarpenterRecipe.TYPE.toString()), new CarpenterRecipe.Serializer());
				helper.register(new ResourceLocation(ICentrifugeRecipe.TYPE.toString()), new CentrifugeRecipe.Serializer());
				helper.register(new ResourceLocation(IFabricatorRecipe.TYPE.toString()), new FabricatorRecipe.Serializer());
				helper.register(new ResourceLocation(IFabricatorSmeltingRecipe.TYPE.toString()), new FabricatorSmeltingRecipe.Serializer());
				helper.register(new ResourceLocation(IFermenterRecipe.TYPE.toString()), new FermenterRecipe.Serializer());
				helper.register(new ResourceLocation(IHygroregulatorRecipe.TYPE.toString()), new HygroregulatorRecipe.Serializer());
				helper.register(new ResourceLocation(IMoistenerRecipe.TYPE.toString()), new MoistenerRecipe.Serializer());
				helper.register(new ResourceLocation(ISqueezerRecipe.TYPE.toString()), new SqueezerRecipe.Serializer());
				helper.register(new ResourceLocation(ISqueezerContainerRecipe.TYPE.toString()), new SqueezerContainerRecipe.Serializer());
				helper.register(new ResourceLocation(IStillRecipe.TYPE.toString()), new StillRecipe.Serializer());
				helper.register(new ResourceLocation(ISolderRecipe.TYPE.toString()), new CircuitRecipe.Serializer());
			});

			event.register(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, helper -> {
				helper.register(new ResourceLocation(Constants.MOD_ID, "condition_modifier"), ConditionLootModifier.CODEC);
				helper.register(new ResourceLocation(Constants.MOD_ID, "grafter_modifier"), GrafterLootModifier.CODEC);

				OrganismFunction.type = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(Constants.MOD_ID, "set_species_nbt"), new LootItemFunctionType(new OrganismFunction.Serializer()));
				CountBlockFunction.type = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(Constants.MOD_ID, "count_from_block"), new LootItemFunctionType(new CountBlockFunction.Serializer()));
			});
		}

		@SubscribeEvent
		@OnlyIn(Dist.CLIENT)
		public void handleTextureRemap(TextureStitchEvent.Pre event) {
			if (event.getAtlas().location() == InventoryMenu.BLOCK_ATLAS) {
				TextureManagerForestry.getInstance().registerSprites(ISpriteRegistry.fromEvent(event));
				ModelBlockCached.clear();
			}
		}
	}

	@SubscribeEvent
	public void registerCommands(RegisterCommandsEvent event) {
		ModuleManager.registerCommands(event.getDispatcher());
	}

	public void processIMCMessages(InterModProcessEvent event) {
		ModuleManager.getModuleHandler().processIMCMessages(event.getIMCStream());
	}
}
