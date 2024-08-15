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
package forestry.farming.logic.crops;

import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import forestry.api.genetics.IFruitBearer;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.BlockUtil;

public class CropFruit extends Crop {
	public CropFruit(Level world, BlockPos position) {
		super(world, position);
	}

	@Override
	protected boolean isCrop(Level world, BlockPos pos) {
		IFruitBearer bearer = TileUtil.getTile(world, pos, IFruitBearer.class);
		return bearer != null && bearer.hasFruit() && bearer.getRipeness() >= 0.9f;
	}

	@Override
	protected List<ItemStack> harvestBlock(Level level, BlockPos pos) {
		IFruitBearer tile = TileUtil.getTile(level, pos, IFruitBearer.class);
		if (tile == null) {
			return NonNullList.create();
		}

		BlockUtil.sendDestroyEffects(level, pos, level.getBlockState(pos));
		return tile.pickFruit(ItemStack.EMPTY);
	}
}
