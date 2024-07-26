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
package forestry.apiculture.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import forestry.apiculture.features.ApicultureTiles;
import forestry.core.tiles.TileNaturalistChest;
import forestry.core.utils.SpeciesUtil;

public class TileApiaristChest extends TileNaturalistChest {
	public TileApiaristChest(BlockPos pos, BlockState state) {
		super(ApicultureTiles.APIARIST_CHEST.tileType(), pos, state, SpeciesUtil.BEE_TYPE.get());
	}
}
