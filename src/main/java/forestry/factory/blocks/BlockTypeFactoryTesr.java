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
package forestry.factory.blocks;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import forestry.core.blocks.BlockBase;
import forestry.core.blocks.IBlockType;
import forestry.core.blocks.IMachineProperties;
import forestry.core.blocks.MachineProperties;
import forestry.core.config.Constants;
import forestry.core.tiles.IForestryTicker;
import forestry.core.tiles.TileBase;
import forestry.core.tiles.TileMill;
import forestry.factory.features.FactoryTiles;
import forestry.factory.tiles.TileBottler;
import forestry.factory.tiles.TileCarpenter;
import forestry.factory.tiles.TileCentrifuge;
import forestry.factory.tiles.TileFermenter;
import forestry.factory.tiles.TileMoistener;
import forestry.factory.tiles.TileSqueezer;
import forestry.factory.tiles.TileStill;
import forestry.modules.features.FeatureTileType;

public enum BlockTypeFactoryTesr implements IBlockType {
	BOTTLER(FactoryTiles.BOTTLER, "bottler", TileBottler::serverTick),
	CARPENTER(FactoryTiles.CARPENTER, "carpenter", TileCarpenter::serverTick),
	CENTRIFUGE(FactoryTiles.CENTRIFUGE, "centrifuge", TileCentrifuge::serverTick),
	FERMENTER(FactoryTiles.FERMENTER, "fermenter", TileFermenter::serverTick),
	MOISTENER(FactoryTiles.MOISTENER, "moistener", TileMoistener::serverTick),
	SQUEEZER(FactoryTiles.SQUEEZER, "squeezer", TileSqueezer::serverTick),
	STILL(FactoryTiles.STILL, "still", TileStill::serverTick),
	RAINMAKER(FactoryTiles.RAINMAKER, "rainmaker", Constants.TEXTURE_PATH_BLOCK + "/rainmaker_");

	private final IMachineProperties<?> machineProperties;

	<T extends TileBase> BlockTypeFactoryTesr(FeatureTileType<T> teClass, String name, @Nullable IForestryTicker<T> serverTicker) {
		final VoxelShape nsBase = Block.box(2D, 2D, 4D, 14, 14, 12);
		final VoxelShape nsFront = Block.box(0D, 0D, 0D, 16, 16, 4);
		final VoxelShape nsBack = Block.box(0D, 0D, 12D, 16, 16, 16);
		final VoxelShape ns = Shapes.or(nsBase, nsFront, nsBack);
		final VoxelShape ewBase = Block.box(4D, 2D, 2D, 12, 14, 14);
		final VoxelShape ewFront = Block.box(0D, 0D, 0D, 4, 16, 16);
		final VoxelShape ewBack = Block.box(12D, 0D, 0D, 16, 16, 16);
		final VoxelShape ew = Shapes.or(ewBase, ewFront, ewBack);

		this.machineProperties = new MachineProperties.Builder<>(teClass, name)
				.setServerTicker(serverTicker)
				.setShape((state, reader, pos, context) -> {
					Direction direction = state.getValue(BlockBase.FACING);
					return (direction == Direction.NORTH || direction == Direction.SOUTH) ? ns : ew;
				})
				.create();
	}

	<T extends TileMill> BlockTypeFactoryTesr(FeatureTileType<T> teClass, String name, String renderMillTexture) {
		final VoxelShape pedestal = Block.box(0D, 0D, 0D, 16, 1, 16);
		final VoxelShape column = Block.box(5D, 1D, 4D, 11, 16, 12);
		final VoxelShape extension = Block.box(1D, 8D, 7D, 15, 10, 9);

		this.machineProperties = new MachineProperties.Builder<>(teClass, name)
				.setShape(() -> Shapes.or(pedestal, column, extension))
				.setClientTicker(TileMill::clientTick)
				.setServerTicker(TileMill::serverTick)
				.create();
	}

	@Override
	public IMachineProperties<?> getMachineProperties() {
		return machineProperties;
	}

	@Override
	public String getSerializedName() {
		return getMachineProperties().getSerializedName();
	}
}
