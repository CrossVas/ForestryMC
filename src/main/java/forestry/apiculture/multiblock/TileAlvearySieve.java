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
package forestry.apiculture.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import genetics.api.GeneticsAPI;
import genetics.api.individual.IIndividual;

import forestry.api.apiculture.DefaultBeeListener;
import forestry.api.apiculture.IBeeListener;
import forestry.api.arboriculture.genetics.TreeLifeStage;
import forestry.api.genetics.IForestrySpeciesType;
import forestry.api.multiblock.IAlvearyComponent;
import forestry.apiculture.blocks.BlockAlvearyType;
import forestry.apiculture.gui.ContainerAlvearySieve;
import forestry.apiculture.inventory.InventoryAlvearySieve;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.watchers.ISlotPickupWatcher;

public class TileAlvearySieve extends TileAlveary implements IAlvearyComponent.BeeListener {

	private final IBeeListener beeListener;
	private final InventoryAlvearySieve inventory;

	public TileAlvearySieve(BlockPos pos, BlockState state) {
		super(BlockAlvearyType.SIEVE, pos, state);
		this.inventory = new InventoryAlvearySieve(this);
		this.beeListener = new AlvearySieveBeeListener(inventory);
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		return inventory;
	}

	public ISlotPickupWatcher getCrafter() {
		return inventory;
	}

	@Override
	public IBeeListener getBeeListener() {
		return beeListener;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ContainerAlvearySieve(windowId, inv, this);
	}

	static class AlvearySieveBeeListener extends DefaultBeeListener {
		private final InventoryAlvearySieve inventory;

		public AlvearySieveBeeListener(InventoryAlvearySieve inventory) {
			this.inventory = inventory;
		}

		@Override
		public boolean onPollenRetrieved(IIndividual pollen) {
			if (!inventory.canStorePollen()) {
				return false;
			}

			IForestrySpeciesType<IIndividual> root = GeneticsAPI.apiInstance.getRootHelper().getSpeciesRoot(pollen);

			ItemStack pollenStack = root.getTypes().createStack(pollen, TreeLifeStage.POLLEN);
			if (!pollenStack.isEmpty()) {
				inventory.storePollenStack(pollenStack);
				return true;
			}
			return false;
		}
	}
}
