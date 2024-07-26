package forestry.farming;

import java.util.function.BiFunction;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.util.StringRepresentable;

import forestry.api.IForestryApi;
import forestry.api.arboriculture.ITreeSpecies;
import forestry.api.arboriculture.genetics.IFruit;
import forestry.api.arboriculture.genetics.TreeLifeStage;
import forestry.api.arboriculture.genetics.ITreeSpeciesType;
import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.farming.FarmPropertiesEvent;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmPropertiesBuilder;
import forestry.api.genetics.alleles.ForestryAlleles;
import forestry.api.genetics.alleles.IValueAllele;
import forestry.api.genetics.alleles.TreeChromosomes;
import forestry.core.circuits.Circuits;
import forestry.core.features.CoreBlocks;
import forestry.core.features.CoreItems;
import forestry.core.items.ItemFruit;
import forestry.core.items.definitions.EnumElectronTube;
import forestry.core.utils.ForgeUtils;
import forestry.core.utils.SpeciesUtil;
import forestry.farming.circuits.CircuitFarmLogic;
import forestry.farming.logic.FarmLogicArboreal;
import forestry.farming.logic.FarmLogicCocoa;
import forestry.farming.logic.FarmLogicCrops;
import forestry.farming.logic.FarmLogicEnder;
import forestry.farming.logic.FarmLogicGourd;
import forestry.farming.logic.FarmLogicInfernal;
import forestry.farming.logic.FarmLogicMushroom;
import forestry.farming.logic.FarmLogicOrchard;
import forestry.farming.logic.FarmLogicPeat;
import forestry.farming.logic.FarmLogicReeds;
import forestry.farming.logic.FarmLogicSucculent;
import forestry.api.modules.ForestryModuleIds;

public enum FarmDefinition implements StringRepresentable {
	CROPS("crops", EnumElectronTube.BRONZE, FarmLogicCrops::new) {
		@Override
		protected void initProperties(IFarmPropertiesBuilder properties) {
			wateredProperties(properties).addSoil(Blocks.DIRT)
				.addFarmables("Wheat")
				.setIcon(() -> new ItemStack(Items.WHEAT));
		}
	},
	GOURD("gourd", EnumElectronTube.LAPIS, FarmLogicGourd::new) {
		@Override
		protected void initProperties(IFarmPropertiesBuilder properties) {
			properties.setIcon(() -> new ItemStack(Items.MELON))
				.setFertilizer(10)
				.setWater(hydrationModifier -> (int) (40 * hydrationModifier));
		}
	},
	SHROOM("shroom", EnumElectronTube.APATITE, FarmLogicMushroom::new) {
		@Override
		protected void initProperties(IFarmPropertiesBuilder properties) {
			properties.addSoil(Blocks.MYCELIUM)
				.addSoil(Blocks.PODZOL)
				.setWater(hydrationModifier -> (int) (80 * hydrationModifier))
				.setFertilizer(20)
				.setIcon(() -> new ItemStack(Blocks.RED_MUSHROOM));
		}
	},
	INFERNAL("infernal", EnumElectronTube.BLAZE, FarmLogicInfernal::new) {
		@Override
		protected void initProperties(IFarmPropertiesBuilder properties) {
			properties.addSoil(Blocks.SOUL_SAND)
				.setWater(0)
				.setFertilizer(20)
				.setIcon(() -> new ItemStack(Items.NETHER_WART));
		}
	},
	POALES("poales", EnumElectronTube.TIN, FarmLogicReeds::new) {
		@Override
		protected void initProperties(IFarmPropertiesBuilder properties) {
			properties.addSoil(Blocks.SAND)
				.addSoil(Blocks.DIRT).setFertilizer(10)
				.setWater(hydrationModifier -> (int) (20 * hydrationModifier))
				.setIcon(() -> new ItemStack(Items.SUGAR_CANE));
		}
	},
	SUCCULENTES("succulentes", EnumElectronTube.COPPER, FarmLogicSucculent::new) {
		@Override
		protected void initProperties(IFarmPropertiesBuilder properties) {
			properties.addSoil(Blocks.SAND)
				.setFertilizer(10)
				.setWater(1)
				.setIcon(() -> new ItemStack(Items.GREEN_DYE));
		}
	},
	ENDER("ender", EnumElectronTube.ENDER, FarmLogicEnder::new) {
		@Override
		protected void initProperties(IFarmPropertiesBuilder properties) {
			properties.addSoil(Blocks.END_STONE)
				.setIcon(() -> new ItemStack(Items.ENDER_EYE))
				.setFertilizer(20)
				.setWater(0);
		}
	},
	ARBOREAL("arboreal", EnumElectronTube.GOLD, FarmLogicArboreal::new) {
		@Override
		protected void initProperties(IFarmPropertiesBuilder properties) {
			properties.addSoil(new ItemStack(Blocks.DIRT), CoreBlocks.HUMUS.defaultState())
				.addSoil(CoreBlocks.HUMUS.stack(), CoreBlocks.HUMUS.defaultState())
				.addProducts(new ItemStack(Blocks.SAND))
				.setFertilizer(10)
				.setWater(hydrationModifier -> (int) (10 * hydrationModifier))
				.setIcon(() -> new ItemStack(Blocks.OAK_SAPLING));
		}
	},
	PEAT("peat", EnumElectronTube.OBSIDIAN, FarmLogicPeat::new) {
		@Override
		protected void initProperties(IFarmPropertiesBuilder properties) {
			wateredProperties(properties).addSoil(CoreBlocks.BOG_EARTH.stack(), CoreBlocks.BOG_EARTH.defaultState())
				.addProducts(CoreItems.PEAT.stack(), new ItemStack(Blocks.DIRT))
				.setIcon(CoreItems.PEAT::stack)
				.setFertilizer(2);
		}
	},
	ORCHARD("orchard", EnumElectronTube.EMERALD, FarmLogicOrchard::new) {
		@Override
		protected void initProperties(IFarmPropertiesBuilder properties) {
			properties.setFertilizer(10)
				.setWater(hydrationModifier -> (int) (40 * hydrationModifier))
				.setIcon(() -> CoreItems.FRUITS.stack(ItemFruit.EnumFruit.CHERRY));
			ITreeSpeciesType treeRoot = SpeciesUtil.TREE_TYPE.get();
			if (treeRoot != null) {
				for (ITreeSpecies tree : treeRoot.getAllSpecies()) {
					IValueAllele<IFruit> fruitAllele = tree.getDefaultGenome().getActiveAllele(TreeChromosomes.FRUITS);

					if (fruitAllele != ForestryAlleles.FRUIT_NONE) {
						IFruit fruit = fruitAllele.value();
						properties.addSeedlings(tree.createStack(TreeLifeStage.SAPLING))
							.addProducts(fruit.getProducts())
							.addProducts(fruit.getSpecialty());
					}
				}
			}
		}
	},
	COCOA("cocoa", EnumElectronTube.DIAMOND, FarmLogicCocoa::new) {
		@Override
		protected void initProperties(IFarmPropertiesBuilder properties) {
			properties.addSoil(Blocks.JUNGLE_LOG)
				.addSeedlings(new ItemStack(Items.COCOA_BEANS))
				.addProducts(new ItemStack(Items.COCOA_BEANS))
				.setFertilizer(120)
				.setWater(hydrationModifier -> (int) (20 * hydrationModifier))
				.setIcon(() -> new ItemStack(Items.COCOA_BEANS));
		}
	};

	private final String name;
	private final EnumElectronTube tube;
	protected final IFarmProperties properties;
	private final ResourceLocation module;
	private final ICircuit managed;
	private final ICircuit manual;

	FarmDefinition(String identifier, EnumElectronTube tube, BiFunction<IFarmProperties, Boolean, IFarmLogic> factory) {
		this(identifier, tube, factory, ForestryModuleIds.FARMING);
	}

	FarmDefinition(String identifier, EnumElectronTube tube, BiFunction<IFarmProperties, Boolean, IFarmLogic> factory, ResourceLocation module) {
		String camelCase = WordUtils.capitalize(identifier);
		IFarmPropertiesBuilder builder = IForestryApi.INSTANCE.getFarmRegistry().getPropertiesBuilder(identifier)
			.setFactory(factory)
			.setTranslationKey("for.farm." + identifier)
			.addFarmables("farm" + camelCase);
		initProperties(builder);
		ForgeUtils.postEvent(new FarmPropertiesEvent(identifier, builder));
		this.properties = builder.create();
		this.managed = new CircuitFarmLogic("managed" + camelCase, properties, false);
		this.manual = new CircuitFarmLogic("manual" + camelCase, properties, true);
		this.name = identifier;
		this.tube = tube;
		this.module = module;
	}

	protected IFarmPropertiesBuilder wateredProperties(IFarmPropertiesBuilder builder) {
		return builder.setWater((hydrationModifier) -> (int) (20 * hydrationModifier))
			.setFertilizer(5);
	}

	protected void initProperties(IFarmPropertiesBuilder properties) {
		//Default Implementation
	}

	protected void registerCircuits(ICircuitLayout layoutManaged, ICircuitLayout layoutManual) {
		ChipsetManager.solderManager.addRecipe(layoutManaged, CoreItems.ELECTRON_TUBES.stack(tube, 1), managed);
		ChipsetManager.solderManager.addRecipe(layoutManual, CoreItems.ELECTRON_TUBES.stack(tube, 1), manual);
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public IFarmProperties getProperties() {
		return properties;
	}

	public static void registerCircuits() {
		ICircuitLayout layoutManaged = ChipsetManager.circuitRegistry.getLayout("forestry.farms.managed");
		ICircuitLayout layoutManual = ChipsetManager.circuitRegistry.getLayout("forestry.farms.manual");

		if (layoutManaged == null || layoutManual == null) {
			return;
		}

		for (FarmDefinition definition : values()) {
			definition.registerCircuits(layoutManaged, layoutManual);
		}
	}

	public static void init() {
		Circuits.farmArborealManaged = ARBOREAL.managed;
		Circuits.farmArborealManual = ARBOREAL.manual;

		Circuits.farmShroomManaged = SHROOM.managed;
		Circuits.farmShroomManual = SHROOM.manual;

		Circuits.farmPeatManaged = PEAT.managed;
		Circuits.farmPeatManual = PEAT.manual;

		Circuits.farmCropsManaged = CROPS.managed;
		Circuits.farmCropsManual = CROPS.manual;

		Circuits.farmInfernalManaged = INFERNAL.managed;
		Circuits.farmInfernalManual = INFERNAL.manual;

		Circuits.farmOrchardManaged = ORCHARD.managed;
		Circuits.farmOrchardManual = ORCHARD.manual;

		Circuits.farmSucculentManaged = SUCCULENTES.managed;
		Circuits.farmSucculentManual = SUCCULENTES.manual;

		Circuits.farmPoalesManaged = POALES.managed;
		Circuits.farmPoalesManual = POALES.manual;

		Circuits.farmGourdManaged = GOURD.managed;
		Circuits.farmGourdManual = GOURD.manual;

		Circuits.farmCocoaManaged = COCOA.managed;
		Circuits.farmCocoaManual = COCOA.manual;

		Circuits.farmEnderManaged = ENDER.managed;
		Circuits.farmEnderManual = ENDER.manual;
	}
}
