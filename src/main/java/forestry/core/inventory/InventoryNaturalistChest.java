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
package forestry.core.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;

import forestry.api.genetics.ISpeciesType;
import forestry.core.tiles.TileNaturalistChest;

public class InventoryNaturalistChest extends InventoryAdapterTile<TileNaturalistChest> {
	private final ISpeciesType speciesRoot;

	public InventoryNaturalistChest(TileNaturalistChest tile, ISpeciesType speciesRoot) {
		super(tile, 125, "Items");
		this.speciesRoot = speciesRoot;
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemstack) {
		return speciesRoot.isMember(itemstack);
	}

	@Override
	public boolean canTakeItemThroughFace(int slotIndex, ItemStack stack, Direction side) {
		return true;
	}
}
