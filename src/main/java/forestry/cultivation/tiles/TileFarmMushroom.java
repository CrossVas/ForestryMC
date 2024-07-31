package forestry.cultivation.tiles;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import forestry.cultivation.features.CultivationTiles;
import forestry.farming.logic.ForestryFarmIdentifier;

public class TileFarmMushroom extends TilePlanter {
	public TileFarmMushroom(BlockPos pos, BlockState state) {
		super(CultivationTiles.MUSHROOM.tileType(), pos, state, ForestryFarmIdentifier.SHROOM);
	}

	@Override
	public List<ItemStack> createGermlingStacks() {
		return List.of(
				new ItemStack(Blocks.RED_MUSHROOM),
				new ItemStack(Blocks.BROWN_MUSHROOM),
				new ItemStack(Blocks.BROWN_MUSHROOM),
				new ItemStack(Blocks.RED_MUSHROOM)
		);
	}

	@Override
	public List<ItemStack> createResourceStacks() {
		return List.of(
				new ItemStack(Blocks.MYCELIUM),
				new ItemStack(Blocks.PODZOL),
				new ItemStack(Blocks.PODZOL),
				new ItemStack(Blocks.MYCELIUM)
		);
	}

	@Override
	public List<ItemStack> createProductionStacks() {
		return List.of(
				new ItemStack(Blocks.RED_MUSHROOM),
				new ItemStack(Blocks.BROWN_MUSHROOM),
				new ItemStack(Blocks.BROWN_MUSHROOM),
				new ItemStack(Blocks.RED_MUSHROOM)
		);
	}
}
