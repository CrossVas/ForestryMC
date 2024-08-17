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
package forestry.core.gui;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.IFluidTank;

import forestry.core.tiles.ILiquidTankTile;

public abstract class ContainerLiquidTanks<T extends BlockEntity & ILiquidTankTile> extends ContainerTile<T> implements IContainerLiquidTanks {
	private final ContainerLiquidTanksHelper<T> helper;

	protected ContainerLiquidTanks(int windowId, MenuType<?> type, Inventory playerInventory, T tile, int xInv, int yInv) {
		super(windowId, type, playerInventory, tile, xInv, yInv);

		this.helper = new ContainerLiquidTanksHelper<>(tile);

		if (this.player != null) {
			this.tile.getTankManager().sendAllTanks(this, this.player);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handlePipetteClickClient(int slot, Player player) {
		helper.handlePipetteClickClient(slot, player);
	}

	@Override
	public void handlePipetteClick(int slot, ServerPlayer player) {
		helper.handlePipetteClick(slot, player);
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();

		if (this.player != null) {
			tile.getTankManager().broadcastChanges(this, this.player);
		}
	}

	@Override
	public void removed(Player PlayerEntity) {
		super.removed(PlayerEntity);
		tile.getTankManager().onClosed(this);
	}

	@Nullable
	@Override
	public IFluidTank getTank(int slot) {
		return tile.getTankManager().getTank(slot);
	}
}
