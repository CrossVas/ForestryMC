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
package forestry.farming.logic;

import java.util.Collection;
import java.util.Stack;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmType;
import forestry.api.farming.IFarmable;
import forestry.core.utils.BlockUtil;

public class FarmLogicInfernal extends FarmLogicHomogeneous {

	public FarmLogicInfernal(IFarmType properties, boolean isManual) {
		super(properties, isManual);
	}

	@Override
	public Collection<ICrop> harvest(Level level, IFarmHousing housing, Direction direction, int extent, BlockPos pos) {
		Stack<ICrop> crops = new Stack<>();
		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos.above(), direction, i);
			if (!level.hasChunkAt(position)) {
				break;
			}
			if (level.isEmptyBlock(pos)) {
				continue;
			}
			BlockState blockState = level.getBlockState(position);
			for (IFarmable farmable : getFarmables()) {
				ICrop crop = farmable.getCropAt(level, position, blockState);
				if (crop != null) {
					crops.push(crop);
					break;
				}
			}

		}
		return crops;

	}

	@Override
	protected boolean maintainSeedlings(Level world, IFarmHousing farmHousing, BlockPos pos, Direction direction, int extent) {
		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos, direction, i);
			if (!world.hasChunkAt(position)) {
				break;
			}

			BlockState blockState = world.getBlockState(position);
			if (!world.isEmptyBlock(position) && !BlockUtil.isReplaceableBlock(blockState, world, position)) {
				continue;
			}

			BlockPos soilPosition = position.below();
			BlockState soilState = world.getBlockState(soilPosition);
			if (isAcceptedSoil(soilState)) {
				return trySetCrop(world, farmHousing, position, direction);
			}
		}

		return false;
	}

}
