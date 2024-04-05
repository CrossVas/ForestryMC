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
package forestry.core;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import forestry.api.circuits.ChipsetManager;
import forestry.api.genetics.alleles.AlleleManager;
import forestry.api.modules.ForestryModule;
import forestry.api.multiblock.MultiblockManager;
import forestry.api.recipes.RecipeManagers;
import forestry.core.circuits.CircuitRegistry;
import forestry.core.circuits.GuiSolderingIron;
import forestry.core.circuits.SolderManager;
import forestry.core.commands.CommandModules;
import forestry.core.config.Constants;
import forestry.core.features.CoreMenuTypes;
import forestry.core.features.CoreFeatures;
import forestry.core.genetics.alleles.AlleleFactory;
import forestry.core.gui.GuiAlyzer;
import forestry.core.gui.GuiAnalyzer;
import forestry.core.gui.GuiEscritoire;
import forestry.core.gui.GuiNaturalistInventory;
import forestry.core.multiblock.MultiblockLogicFactory;
import forestry.core.network.IPacketRegistry;
import forestry.core.network.PacketRegistryCore;
import forestry.core.owner.GameProfileDataSerializer;
import forestry.core.proxy.Proxies;
import forestry.core.recipes.HygroregulatorManager;
import forestry.core.utils.ClimateUtil;
import forestry.core.utils.ForestryModEnvWarningCallable;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ISidedModuleHandler;

@ForestryModule(modId = Constants.MOD_ID, moduleID = ForestryModuleUids.CORE, name = "Core", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.core.description", coreModule = true)
public class ModuleCore extends BlankForestryModule {
	public static final LiteralArgumentBuilder<CommandSourceStack> rootCommand = LiteralArgumentBuilder.literal("forestry");

	public ModuleCore() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		CoreFeatures.CONFIGURED_FEATURES.register(bus);
		CoreFeatures.PLACED_FEATURES.register(bus);
	}

	@Override
	public Set<ResourceLocation> getDependencyUids() {
		return Collections.emptySet();
	}

	@Override
	public void setupAPI() {
		ChipsetManager.solderManager = new SolderManager();

		ChipsetManager.circuitRegistry = new CircuitRegistry();

		AlleleManager.climateHelper = new ClimateUtil();
		AlleleManager.alleleFactory = new AlleleFactory();

		MultiblockManager.logicFactory = new MultiblockLogicFactory();

		RecipeManagers.hygroregulatorManager = new HygroregulatorManager();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerGuiFactories() {
		MenuScreens.register(CoreMenuTypes.ALYZER.menuType(), GuiAlyzer::new);
		MenuScreens.register(CoreMenuTypes.ANALYZER.menuType(), GuiAnalyzer::new);
		MenuScreens.register(CoreMenuTypes.NATURALIST_INVENTORY.menuType(), GuiNaturalistInventory::new);
		MenuScreens.register(CoreMenuTypes.ESCRITOIRE.menuType(), GuiEscritoire::new);
		MenuScreens.register(CoreMenuTypes.SOLDERING_IRON.menuType(), GuiSolderingIron::new);
	}


	@Override
	public void preInit() {
		GameProfileDataSerializer.register();

		rootCommand.then(CommandModules.register());
	}

	@Nullable
	@Override
	public LiteralArgumentBuilder<CommandSourceStack> register() {
		return rootCommand;
	}

	@Override
	public void doInit() {
		ForestryModEnvWarningCallable.register();

		Proxies.render.initRendering();
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerCore();
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryCore();
	}

	@Override
	public boolean processIMCMessage(InterModComms.IMCMessage message) {
		return false;
	}

	@Override
	public IPickupHandler getPickupHandler() {
		return new PickupHandlerCore();
	}

	@Override
	public ISidedModuleHandler getModuleHandler() {
		return Proxies.render;
	}
}
