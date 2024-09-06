package forestry.apiculture;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import forestry.api.ForestryTags;
import forestry.api.apiculture.IFlowerType;

public class FlowerType implements IFlowerType {
	private final TagKey<Block> acceptableFlowers;
	private final boolean dominant;

	public FlowerType(TagKey<Block> acceptableFlowers, boolean dominant) {
		this.acceptableFlowers = acceptableFlowers;
		this.dominant = dominant;
	}

	@Override
	public boolean isAcceptableFlower(Level level, BlockPos pos) {
		// for debugging purposes
		//level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(Blocks.REDSTONE_BLOCK.defaultBlockState()));
		return level.getBlockState(pos).is(this.acceptableFlowers);
	}

	@Override
	public boolean plantRandomFlower(Level level, BlockPos pos, List<BlockState> nearbyFlowers) {
		if (level.hasChunkAt(pos) && level.isEmptyBlock(pos)) {
			for (BlockState state : nearbyFlowers) {
				if (state.is(ForestryTags.Blocks.PLANTABLE_FLOWERS)) {
					if (state.canSurvive(level, pos)) {
						if (state.hasProperty(DoublePlantBlock.HALF)) {
							BlockPos topPos = pos.above();

							if (level.isEmptyBlock(topPos)) {
								return level.setBlockAndUpdate(pos, state.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER))
										&& level.setBlockAndUpdate(topPos, state.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER));
							}
						} else {
							return level.setBlockAndUpdate(pos, state);
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean isDominant() {
		return this.dominant;
	}
}
