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

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmType;
import forestry.api.farming.IFarmable;
import forestry.core.utils.BlockUtil;

public class FarmLogicCrops extends FarmLogicWatered {

	public FarmLogicCrops(IFarmType properties, boolean isManual) {
		super(properties, isManual);
	}

	@Override
	public List<ItemStack> collect(Level level, IFarmHousing farmHousing) {
		return collectEntityItems(level, farmHousing, false);
	}

	@Override
	protected boolean maintainCrops(Level world, IFarmHousing farmHousing, BlockPos pos, Direction direction, int extent) {
		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos, direction, i);
			if (!world.hasChunkAt(position)) {
				break;
			}

			BlockState state = world.getBlockState(position);
			if (!world.isEmptyBlock(position) && !BlockUtil.isReplaceableBlock(state, world, position)) {
				continue;
			}

			BlockState groundState = world.getBlockState(position.below());
			if (isAcceptedSoil(groundState)) {
				return trySetCrop(world, farmHousing, position, direction);
			}
		}

		return false;
	}

	private boolean trySetCrop(Level world, IFarmHousing farmHousing, BlockPos position, Direction direction) {
		for (IFarmable candidate : getFarmables()) {
			if (farmHousing.plantGermling(candidate, world, position, direction)) {
				return true;
			}
		}

		return false;
	}
}
