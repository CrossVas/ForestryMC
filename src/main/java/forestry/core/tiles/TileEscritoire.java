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
package forestry.core.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.genetics.ForestryComponentKeys;
import forestry.api.genetics.IResearchHandler;
import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import forestry.core.features.CoreTiles;
import forestry.core.gui.ContainerEscritoire;
import forestry.core.inventory.InventoryAnalyzer;
import forestry.core.inventory.InventoryEscritoire;
import forestry.core.inventory.watchers.ISlotPickupWatcher;
import forestry.core.network.IStreamableGui;
import forestry.core.network.packets.PacketItemStackDisplay;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.NetworkUtil;

import genetics.api.individual.IIndividual;
import forestry.api.genetics.ISpeciesType;
import genetics.utils.RootUtils;

public class TileEscritoire extends TileBase implements WorldlyContainer, ISlotPickupWatcher, IStreamableGui, IItemStackDisplay {

	private final EscritoireGame game = new EscritoireGame();
	private ItemStack individualOnDisplayClient = ItemStack.EMPTY;

	public TileEscritoire(BlockPos pos, BlockState state) {
		super(CoreTiles.ESCRITOIRE.tileType(), pos, state);
		setInternalInventory(new InventoryEscritoire(this));
	}

	/* SAVING & LOADING */
	@Override
	public void load(CompoundTag compoundNBT) {
		super.load(compoundNBT);
		game.read(compoundNBT);
	}


	@Override
	public void saveAdditional(CompoundTag compoundNBT) {
		super.saveAdditional(compoundNBT);
		game.write(compoundNBT);
	}

	/* GAME */
	public EscritoireGame getGame() {
		return game;
	}

	public void choose(GameProfile gameProfile, int index) {
		game.choose(index);
		processTurnResult(gameProfile);
	}

	private void processTurnResult(GameProfile gameProfile) {
		if (getGame().getStatus() != EscritoireGame.Status.SUCCESS) {
			return;
		}

		IIndividual individual = RootUtils.getIndividual(getItem(InventoryEscritoire.SLOT_ANALYZE));
		if (individual == null) {
			return;
		}

		IAlleleForestrySpecies species = individual.getGenome().getPrimarySpecies(IAlleleForestrySpecies.class);
		ISpeciesType<IIndividual> root = (ISpeciesType<IIndividual>) species.getSpecies();
		IResearchHandler<IIndividual> handler = root.getComponent(ForestryComponentKeys.RESEARCH);
		for (ItemStack itemstack : handler.getResearchBounty(species, level, gameProfile, individual, game.getBountyLevel())) {
			InventoryUtil.addStack(getInternalInventory(), itemstack, InventoryEscritoire.SLOT_RESULTS_1, InventoryEscritoire.SLOTS_RESULTS_COUNT, true);
		}
	}

	private boolean areProbeSlotsFilled() {
		int filledSlots = 0;
		int required = game.getSampleSize(InventoryEscritoire.SLOTS_INPUT_COUNT);
		for (int i = InventoryEscritoire.SLOT_INPUT_1; i < InventoryEscritoire.SLOT_INPUT_1 + required; i++) {
			if (!getItem(i).isEmpty()) {
				filledSlots++;
			}
		}

		return filledSlots >= required;
	}

	public void probe() {
		if (level.isClientSide) {
			return;
		}

		ItemStack analyze = getItem(InventoryEscritoire.SLOT_ANALYZE);

		if (!analyze.isEmpty() && areProbeSlotsFilled()) {
			game.probe(analyze, this, InventoryEscritoire.SLOT_INPUT_1, InventoryEscritoire.SLOTS_INPUT_COUNT);
		}
	}

	/* NETWORK */
	@Override
	public void writeGuiData(FriendlyByteBuf data) {
		game.writeData(data);
	}

	@Override
	public void readGuiData(FriendlyByteBuf data) {
		game.readData(data);
	}

	@Override
	public void writeData(FriendlyByteBuf data) {
		super.writeData(data);
		ItemStack displayStack = getIndividualOnDisplay();
		data.writeItem(displayStack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void readData(FriendlyByteBuf data) {
		super.readData(data);
		individualOnDisplayClient = data.readItem();
	}

	/* ISlotPickupWatcher */
	@Override
	public void onTake(int slotIndex, Player player) {
		if (slotIndex == InventoryEscritoire.SLOT_ANALYZE) {
			game.reset();
			PacketItemStackDisplay packet = new PacketItemStackDisplay(this, getIndividualOnDisplay());
			NetworkUtil.sendNetworkPacket(packet, worldPosition, level);
		}
	}

	@Override
	public void setItem(int slotIndex, ItemStack itemstack) {
		super.setItem(slotIndex, itemstack);
		if (slotIndex == InventoryEscritoire.SLOT_ANALYZE) {
			if (level != null && !level.isClientSide) {
				PacketItemStackDisplay packet = new PacketItemStackDisplay(this, getIndividualOnDisplay());
				NetworkUtil.sendNetworkPacket(packet, worldPosition, level);
			}
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ContainerEscritoire(windowId, player.getInventory(), this);
	}

	@Override
	public void handleItemStackForDisplay(ItemStack itemStack) {
		if (!ItemStack.matches(itemStack, individualOnDisplayClient)) {
			individualOnDisplayClient = itemStack;
		}
	}

	public ItemStack getIndividualOnDisplay() {
		if (level.isClientSide) {
			return individualOnDisplayClient;
		}
		return getItem(InventoryAnalyzer.SLOT_ANALYZE);
	}
}
