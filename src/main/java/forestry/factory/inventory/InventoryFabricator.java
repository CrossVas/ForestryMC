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
package forestry.factory.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.core.Direction;

import forestry.core.recipes.RecipeManagers;
import forestry.core.inventory.InventoryAdapterTile;
import forestry.core.utils.RecipeUtils;
import forestry.core.utils.SlotUtil;
import forestry.factory.tiles.TileFabricator;

public class InventoryFabricator extends InventoryAdapterTile<TileFabricator> {
	public static final short SLOT_METAL = 0;
	public static final short SLOT_PLAN = 1;
	public static final short SLOT_RESULT = 2;
	public static final short SLOT_INVENTORY_1 = 3;
	public static final short SLOT_INVENTORY_COUNT = 18;
	public static final short SLOT_COUNT = 21;

	public InventoryFabricator(TileFabricator fabricator) {
		super(fabricator, SLOT_COUNT, "Items");
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack stack) {
		RecipeManager recipeManager = tile.getLevel().getRecipeManager();

		if (slotIndex == SLOT_METAL) {
			return RecipeUtils.getFabricatorMeltingRecipe(recipeManager, stack) != null;
		} else if (slotIndex == SLOT_PLAN) {
			return RecipeUtils.isFabricatorPlan(recipeManager, stack);
		} else if (SlotUtil.isSlotInRange(slotIndex, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT)) {
			if (RecipeUtils.isFabricatorPlan(recipeManager, stack)) {
				return false;
			} else if (RecipeUtils.getFabricatorMeltingRecipe(recipeManager, stack) != null) {
				return false;
			}
		}
		return SlotUtil.isSlotInRange(slotIndex, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT);
	}

	@Override
	public boolean canTakeItemThroughFace(int slotIndex, ItemStack stack, Direction side) {
		return slotIndex == SLOT_RESULT;
	}
}
