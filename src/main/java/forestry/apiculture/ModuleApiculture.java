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
package forestry.apiculture;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Consumer;

import deleteme.BiomeCategory;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;

import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.loading.FMLEnvironment;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IArmorApiarist;
import forestry.api.apiculture.hives.HiveManager;
import forestry.api.apiculture.hives.IHiveRegistry.HiveType;
import forestry.api.genetics.flowers.IFlowerAcceptableRule;
import forestry.api.modules.ForestryModule;
import forestry.apiculture.commands.CommandBee;
import forestry.apiculture.features.ApicultureMenuTypes;
import forestry.apiculture.features.ApicultureItems;
import forestry.apiculture.flowers.FlowerRegistry;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.apiculture.genetics.BeeFactory;
import forestry.apiculture.genetics.BeeMutationFactory;
import forestry.apiculture.genetics.HiveDrop;
import forestry.apiculture.genetics.JubilanceFactory;
import forestry.apiculture.gui.ContainerBeeHousing;
import forestry.apiculture.gui.ContainerMinecartBeehouse;
import forestry.apiculture.gui.GuiAlveary;
import forestry.apiculture.gui.GuiAlvearyHygroregulator;
import forestry.apiculture.gui.GuiAlvearySieve;
import forestry.apiculture.gui.GuiAlvearySwarmer;
import forestry.apiculture.gui.GuiBeeHousing;
import forestry.apiculture.gui.GuiHabitatLocator;
import forestry.apiculture.gui.GuiImprinter;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.apiculture.items.EnumPollenCluster;
import forestry.apiculture.network.PacketRegistryApiculture;
import forestry.apiculture.proxy.ProxyApiculture;
import forestry.apiculture.worldgen.HiveDescription;
import forestry.apiculture.worldgen.HiveGenHelper;
import forestry.apiculture.worldgen.HiveRegistry;
import forestry.core.ClientsideCode;
import forestry.core.ISaveEventHandler;
import forestry.core.ModuleCore;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.core.utils.IMCUtil;
import forestry.core.utils.Log;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ISidedModuleHandler;

import genetics.api.GeneticsAPI;

@ForestryModule(modId = Constants.MOD_ID, moduleID = ForestryModuleUids.APICULTURE, name = "Apiculture", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.apiculture.description", lootTable = "apiculture")
public class ModuleApiculture extends BlankForestryModule {

	@Nullable
	private static HiveRegistry hiveRegistry;

	public static String beekeepingMode = "NORMAL";

	public static int ticksPerBeeWorkCycle = 550;

	public static boolean hivesDamageOnPeaceful = false;

	public static boolean hivesDamageUnderwater = true;

	public static boolean hivesDamageOnlyPlayers = false;

	public static boolean hiveDamageOnAttack = true;

	public static boolean doSelfPollination = true;

	public static int maxFlowersSpawnedPerHive = 20;

	public static final ProxyApiculture PROXY = FMLEnvironment.dist == Dist.CLIENT ? ClientsideCode.newProxyApiculture() : new ProxyApiculture();

	public static HiveRegistry getHiveRegistry() {
		Preconditions.checkNotNull(hiveRegistry);
		return hiveRegistry;
	}

	@Override
	public void setupAPI() {
		HiveManager.hiveRegistry = hiveRegistry = new HiveRegistry();
		HiveManager.genHelper = new HiveGenHelper();

		FlowerManager.flowerRegistry = new FlowerRegistry();

		BeeManager.commonVillageBees = new ArrayList<>();
		BeeManager.uncommonVillageBees = new ArrayList<>();

		BeeManager.beeFactory = new BeeFactory();
		BeeManager.beeMutationFactory = new BeeMutationFactory();
		BeeManager.jubilanceFactory = new JubilanceFactory();
		BeeManager.armorApiaristHelper = new ArmorApiaristHelper();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerGuiFactories() {
		MenuScreens.register(ApicultureMenuTypes.ALVEARY.menuType(), GuiAlveary::new);
		MenuScreens.register(ApicultureMenuTypes.ALVEARY_HYGROREGULATOR.menuType(), GuiAlvearyHygroregulator::new);
		MenuScreens.register(ApicultureMenuTypes.ALVEARY_SIEVE.menuType(), GuiAlvearySieve::new);
		MenuScreens.register(ApicultureMenuTypes.ALVEARY_SWARMER.menuType(), GuiAlvearySwarmer::new);
		MenuScreens.register(ApicultureMenuTypes.BEE_HOUSING.menuType(), GuiBeeHousing<ContainerBeeHousing>::new);
		MenuScreens.register(ApicultureMenuTypes.HABITAT_LOCATOR.menuType(), GuiHabitatLocator::new);
		MenuScreens.register(ApicultureMenuTypes.IMPRINTER.menuType(), GuiImprinter::new);
		MenuScreens.register(ApicultureMenuTypes.BEEHOUSE_MINECART.menuType(), GuiBeeHousing<ContainerMinecartBeehouse>::new);
	}

	@Override
	public void preInit() {
		// Commands
		ModuleCore.rootCommand.then(CommandBee.register());

		ApicultureFilterRuleType.init();
		ApicultureFilterRule.init();
	}

	@Override
	public void registerCapabilities(Consumer<Class<?>> consumer) {
		consumer.accept(IArmorApiarist.class);
	}

	@Override
	public void doInit() {
		initFlowerRegistry();

		// Genetics
		BeeDefinition.initBees();

		// Hives
		createHives();
		registerBeehiveDrops();

		// Inducers for swarmer
		BeeManager.inducers.put(ApicultureItems.ROYAL_JELLY.stack(), 10);

		BeeManager.commonVillageBees.add(BeeDefinition.FOREST.getGenome());
		BeeManager.commonVillageBees.add(BeeDefinition.MEADOWS.getGenome());
		BeeManager.commonVillageBees.add(BeeDefinition.MODEST.getGenome());
		BeeManager.commonVillageBees.add(BeeDefinition.MARSHY.getGenome());
		BeeManager.commonVillageBees.add(BeeDefinition.WINTRY.getGenome());
		BeeManager.commonVillageBees.add(BeeDefinition.TROPICAL.getGenome());

		BeeManager.uncommonVillageBees.add(BeeDefinition.FOREST.getRainResist().getGenome());
		BeeManager.uncommonVillageBees.add(BeeDefinition.COMMON.getGenome());
		BeeManager.uncommonVillageBees.add(BeeDefinition.VALIANT.getGenome());
	}

	@Override
	public void postInit() {
		//TODO loottable
		//		registerDungeonLoot();
	}

	private void initFlowerRegistry() {
		FlowerRegistry flowerRegistry = (FlowerRegistry) FlowerManager.flowerRegistry;

		flowerRegistry.registerAcceptableFlowerRule(new EndFlowerAcceptableRule(), FlowerManager.FlowerTypeEnd);

		// Register acceptable plants
		flowerRegistry.registerAcceptableFlower(Blocks.DRAGON_EGG, FlowerManager.FlowerTypeEnd);
		flowerRegistry.registerAcceptableFlower(Blocks.CHORUS_PLANT, FlowerManager.FlowerTypeEnd);
		flowerRegistry.registerAcceptableFlower(Blocks.CHORUS_FLOWER, FlowerManager.FlowerTypeEnd);
		flowerRegistry.registerAcceptableFlower(Blocks.VINE, FlowerManager.FlowerTypeJungle);
		flowerRegistry.registerAcceptableFlower(Blocks.FERN, FlowerManager.FlowerTypeJungle);
		flowerRegistry.registerAcceptableFlower(Blocks.WHEAT, FlowerManager.FlowerTypeWheat);
		flowerRegistry.registerAcceptableFlower(Blocks.PUMPKIN_STEM, FlowerManager.FlowerTypeGourd);
		flowerRegistry.registerAcceptableFlower(Blocks.MELON_STEM, FlowerManager.FlowerTypeGourd);
		flowerRegistry.registerAcceptableFlower(Blocks.NETHER_WART, FlowerManager.FlowerTypeNether);
		flowerRegistry.registerAcceptableFlower(Blocks.CACTUS, FlowerManager.FlowerTypeCacti);

		Block[] standardFlowers = new Block[]{
				Blocks.DANDELION,
				Blocks.POPPY,
				Blocks.BLUE_ORCHID,
				Blocks.ALLIUM,
				Blocks.AZURE_BLUET,
				Blocks.RED_TULIP,
				Blocks.ORANGE_TULIP,
				Blocks.WHITE_TULIP,
				Blocks.PINK_TULIP,
				Blocks.OXEYE_DAISY,
				Blocks.CORNFLOWER,
				Blocks.WITHER_ROSE,
				Blocks.LILY_OF_THE_VALLEY,
		};
		Block[] pottedStandardFlowers = new Block[]{
				Blocks.POTTED_POPPY,
				Blocks.POTTED_BLUE_ORCHID,
				Blocks.POTTED_ALLIUM,
				Blocks.POTTED_AZURE_BLUET,
				Blocks.POTTED_RED_TULIP,
				Blocks.POTTED_ORANGE_TULIP,
				Blocks.POTTED_WHITE_TULIP,
				Blocks.POTTED_PINK_TULIP,
				Blocks.POTTED_OXEYE_DAISY,
				Blocks.POTTED_CORNFLOWER,
				Blocks.POTTED_LILY_OF_THE_VALLEY,
				Blocks.POTTED_WITHER_ROSE,
		};

		// Register plantable plants
		String[] standardTypes = new String[]{FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow};
		for (Block standardFlower : standardFlowers) {
			flowerRegistry.registerPlantableFlower(standardFlower.defaultBlockState(), 1.0, standardTypes);
		}
		flowerRegistry.registerPlantableFlower(Blocks.BROWN_MUSHROOM.defaultBlockState(), 1.0, FlowerManager.FlowerTypeMushrooms);
		flowerRegistry.registerPlantableFlower(Blocks.RED_MUSHROOM.defaultBlockState(), 1.0, FlowerManager.FlowerTypeMushrooms);
		flowerRegistry.registerPlantableFlower(Blocks.CACTUS.defaultBlockState(), 1.0, FlowerManager.FlowerTypeCacti);

		//Flower Pots
		for (Block standardFlower : pottedStandardFlowers) {
			flowerRegistry.registerAcceptableFlower(standardFlower, standardTypes);
		}

		flowerRegistry.registerAcceptableFlower(Blocks.POTTED_RED_MUSHROOM, FlowerManager.FlowerTypeMushrooms);
		flowerRegistry.registerAcceptableFlower(Blocks.POTTED_BROWN_MUSHROOM, FlowerManager.FlowerTypeMushrooms);

		flowerRegistry.registerAcceptableFlower(Blocks.POTTED_CACTUS, FlowerManager.FlowerTypeCacti);
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryApiculture();
	}

	@Override
	public void registerRecipes() {
		// BREWING RECIPES
		BrewingRecipeRegistry.addRecipe(
				Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
				Ingredient.of(ApicultureItems.POLLEN_CLUSTER.stack(EnumPollenCluster.NORMAL, 1)),
				PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.HEALING));
		BrewingRecipeRegistry.addRecipe(
				Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
				Ingredient.of(ApicultureItems.POLLEN_CLUSTER.stack(EnumPollenCluster.CRYSTALLINE, 1)),
				PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.REGENERATION));
	}

	private static void registerBeehiveDrops() {
		ItemStack honeyComb = ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.HONEY, 1);
		HiveRegistry hiveRegistry = getHiveRegistry();

		hiveRegistry.addDrops(HiveType.FOREST.getHiveUid(),
				new HiveDrop(0.80, BeeDefinition.FOREST, honeyComb).setIgnobleShare(0.7),
				new HiveDrop(0.08, BeeDefinition.FOREST.getRainResist(), honeyComb),
				new HiveDrop(0.03, BeeDefinition.VALIANT, honeyComb)
		);

		hiveRegistry.addDrops(HiveType.MEADOWS.getHiveUid(),
				new HiveDrop(0.80, BeeDefinition.MEADOWS, honeyComb).setIgnobleShare(0.7),
				new HiveDrop(0.03, BeeDefinition.VALIANT, honeyComb)
		);

		ItemStack parchedComb = ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.PARCHED, 1);
		hiveRegistry.addDrops(HiveType.DESERT.getHiveUid(),
				new HiveDrop(0.80, BeeDefinition.MODEST, parchedComb).setIgnobleShare(0.7),
				new HiveDrop(0.03, BeeDefinition.VALIANT, parchedComb)
		);

		ItemStack silkyComb = ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.SILKY, 1);
		hiveRegistry.addDrops(HiveType.JUNGLE.getHiveUid(),
				new HiveDrop(0.80, BeeDefinition.TROPICAL, silkyComb).setIgnobleShare(0.7),
				new HiveDrop(0.03, BeeDefinition.VALIANT, silkyComb)
		);

		ItemStack mysteriousComb = ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.MYSTERIOUS, 1);
		hiveRegistry.addDrops(HiveType.END.getHiveUid(),
				new HiveDrop(0.90, BeeDefinition.ENDED, mysteriousComb)
		);

		ItemStack frozenComb = ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.FROZEN, 1);
		hiveRegistry.addDrops(HiveType.SNOW.getHiveUid(),
				new HiveDrop(0.80, BeeDefinition.WINTRY, frozenComb).setIgnobleShare(0.5),
				new HiveDrop(0.03, BeeDefinition.VALIANT, frozenComb)
		);

		ItemStack mossyComb = ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.MOSSY, 1);
		hiveRegistry.addDrops(HiveType.SWAMP.getHiveUid(),
				new HiveDrop(0.80, BeeDefinition.MARSHY, mossyComb).setIgnobleShare(0.4),
				new HiveDrop(0.03, BeeDefinition.VALIANT, mossyComb)
		);
	}

	private static void createHives() {
		HiveRegistry hiveRegistry = getHiveRegistry();
		hiveRegistry.registerHive(HiveType.FOREST.getHiveUid(), HiveDescription.FOREST);
		hiveRegistry.registerHive(HiveType.MEADOWS.getHiveUid(), HiveDescription.MEADOWS);
		hiveRegistry.registerHive(HiveType.DESERT.getHiveUid(), HiveDescription.DESERT);
		hiveRegistry.registerHive(HiveType.JUNGLE.getHiveUid(), HiveDescription.JUNGLE);
		hiveRegistry.registerHive(HiveType.END.getHiveUid(), HiveDescription.END);
		hiveRegistry.registerHive(HiveType.SNOW.getHiveUid(), HiveDescription.SNOW);
		hiveRegistry.registerHive(HiveType.SWAMP.getHiveUid(), HiveDescription.SWAMP);
	}

	public static double getSecondPrincessChance() {
		float secondPrincessChance = 0;
		return secondPrincessChance;
	}

	private static void parseBeeBlacklist(String[] items) {
		for (String item : items) {
			if (item.isEmpty()) {
				continue;
			}

			Log.debug("Blacklisting bee species identified by " + item);
			GeneticsAPI.apiInstance.getAlleleRegistry().blacklistAllele(new ResourceLocation(item));
		}
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerApiculture();
	}

	@Override
	public boolean processIMCMessage(InterModComms.IMCMessage message) {
		//		if (message.getMethod().equals("add-candle-lighting-id")) {
		//			ItemStack value = message.getItemStackValue();
		//			if (value != null) {
		//				BlockCandle.addItemToLightingList(value.getItem());
		//			} else {
		//				IMCUtil.logInvalidIMCMessage(message);
		//			}
		//			return true;
		//		} else if (message.getMethod().equals("add-alveary-slab") && message.isStringMessage()) {
		//			String messageString = String.format("Received a '%s' request from mod '%s'. This IMC message has been replaced with the oreDictionary for 'slabWood'. Please contact the author and report this issue.", message.key, message.getSender());
		//			Log.warning(messageString);
		//			return true;
		//		} else if (message.getMethod().equals("blacklist-hives-dimension")) {
		//			int[] dims = message.getNBTValue().getIntArray("dimensions");
		//			for (int dim : dims) {
		//				HiveConfig.addBlacklistedDim(dim);
		//			}
		//			return true;
		//		} else if (message.getMethod().equals("add-plantable-flower")) {
		//			return addPlantableFlower(message);
		//		} else if (message.getMethod().equals("add-acceptable-flower")) {
		//			return addAcceptableFlower(message);
		//		}
		//TODO new imc
		return false;
	}

	private boolean addPlantableFlower(InterModComms.IMCMessage message) {
		try {
			//TODO new imc
			//			CompoundNBT tagCompound = message.getNBTValue();
			//			BlockState flowerState = NBTUtil.readBlockState(tagCompound);
			//			double weight = tagCompound.getDouble("weight");
			//			List<String> flowerTypes = new ArrayList<>();
			//			for (String key : tagCompound.getKeySet()) {
			//				if (key.contains("flowertype")) {
			//					flowerTypes.add(tagCompound.getString("flowertype"));
			//				}
			//			}
			//			FlowerManager.flowerRegistry.registerPlantableFlower(flowerState, weight, flowerTypes.toArray(new String[0]));
			return true;
		} catch (Exception e) {
			IMCUtil.logInvalidIMCMessage(message);
			return false;
		}
	}

	private boolean addAcceptableFlower(InterModComms.IMCMessage message) {
		try {
			//TODO new imc
			//			CompoundNBT tagCompound = message.getNBTValue();
			//			BlockState flowerState = NBTUtil.readBlockState(tagCompound);
			//			List<String> flowerTypes = new ArrayList<>();
			//			for (String key : tagCompound.getKeySet()) {
			//				if (key.contains("flowertype")) {
			//					flowerTypes.add(tagCompound.getString("flowertype"));
			//				}
			//			}
			//			FlowerManager.flowerRegistry.registerAcceptableFlower(flowerState, flowerTypes.toArray(new String[0]));
			return true;
		} catch (Exception e) {
			IMCUtil.logInvalidIMCMessage(message);
			return false;
		}
	}

	@Override
	public ISidedModuleHandler getModuleHandler() {
		return PROXY;
	}

	private static class EndFlowerAcceptableRule implements IFlowerAcceptableRule {
		@Override
		public boolean isAcceptableFlower(BlockState blockState, Level world, BlockPos pos, String flowerType) {
			Biome biomeGenForCoords = world.getBiome(pos).value();
			return BiomeCategory.THEEND.is(biomeGenForCoords);
		}
	}
}
