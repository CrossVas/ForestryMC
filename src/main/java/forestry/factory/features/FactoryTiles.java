package forestry.factory.features;

import forestry.api.modules.ForestryModuleIds;
import forestry.factory.blocks.BlockTypeFactoryPlain;
import forestry.factory.blocks.BlockTypeFactoryTesr;
import forestry.factory.tiles.TileBottler;
import forestry.factory.tiles.TileCarpenter;
import forestry.factory.tiles.TileCentrifuge;
import forestry.factory.tiles.TileFabricator;
import forestry.factory.tiles.TileFermenter;
import forestry.factory.tiles.TileMillRainmaker;
import forestry.factory.tiles.TileMoistener;
import forestry.factory.tiles.TileRaintank;
import forestry.factory.tiles.TileSqueezer;
import forestry.factory.tiles.TileStill;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.FeatureTileType;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class FactoryTiles {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ForestryModuleIds.FACTORY);

	public static final FeatureTileType<TileBottler> BOTTLER = REGISTRY.tile(TileBottler::new, "bottler", () -> FactoryBlocks.TESR.get(BlockTypeFactoryTesr.BOTTLER).collect());
	public static final FeatureTileType<TileCarpenter> CARPENTER = REGISTRY.tile(TileCarpenter::new, "carpenter", () -> FactoryBlocks.TESR.get(BlockTypeFactoryTesr.CARPENTER).collect());
	public static final FeatureTileType<TileCentrifuge> CENTRIFUGE = REGISTRY.tile(TileCentrifuge::new, "centrifuge", () -> FactoryBlocks.TESR.get(BlockTypeFactoryTesr.CENTRIFUGE).collect());
	public static final FeatureTileType<TileFabricator> FABRICATOR = REGISTRY.tile(TileFabricator::new, "fabricator", () -> FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.FABRICATOR).collect());
	public static final FeatureTileType<TileFermenter> FERMENTER = REGISTRY.tile(TileFermenter::new, "fermenter", () -> FactoryBlocks.TESR.get(BlockTypeFactoryTesr.FERMENTER).collect());
	public static final FeatureTileType<TileMillRainmaker> RAINMAKER = REGISTRY.tile(TileMillRainmaker::new, "rainmaker", () -> FactoryBlocks.TESR.get(BlockTypeFactoryTesr.RAINMAKER).collect());
	public static final FeatureTileType<TileMoistener> MOISTENER = REGISTRY.tile(TileMoistener::new, "moistener", () -> FactoryBlocks.TESR.get(BlockTypeFactoryTesr.MOISTENER).collect());
	public static final FeatureTileType<TileRaintank> RAIN_TANK = REGISTRY.tile(TileRaintank::new, "rain_tank", () -> FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.RAINTANK).collect());
	public static final FeatureTileType<TileSqueezer> SQUEEZER = REGISTRY.tile(TileSqueezer::new, "squeezer", () -> FactoryBlocks.TESR.get(BlockTypeFactoryTesr.SQUEEZER).collect());
	public static final FeatureTileType<TileStill> STILL = REGISTRY.tile(TileStill::new, "still", () -> FactoryBlocks.TESR.get(BlockTypeFactoryTesr.STILL).collect());
}
