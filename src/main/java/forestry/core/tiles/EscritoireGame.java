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

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.api.genetics.capability.IIndividualHandlerItem;
import forestry.api.genetics.ISpecies;
import forestry.api.genetics.ISpeciesType;
import forestry.core.network.IStreamable;
import forestry.core.utils.NetworkUtil;

public class EscritoireGame implements INbtWritable, INbtReadable, IStreamable {
	private static final RandomSource rand = RandomSource.create();

	public static final int BOUNTY_MAX = 16;

	public enum Status {
		EMPTY, PLAYING, FAILURE, SUCCESS;
		public static final Status[] VALUES = values();
	}

	private EscritoireGameBoard gameBoard;
	private long lastUpdate;
	private int bountyLevel;
	private Status status = Status.EMPTY;

	public EscritoireGame() {
		this.gameBoard = new EscritoireGameBoard();
	}

	@Nullable
	public EscritoireGameToken getToken(int index) {
		return this.gameBoard.getToken(index);
	}

	public Status getStatus() {
		return this.status;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public CompoundTag write(CompoundTag compoundNBT) {
		compoundNBT.putInt("bountyLevel", bountyLevel);
		compoundNBT.putLong("lastUpdate", lastUpdate);
		gameBoard.write(compoundNBT);

		compoundNBT.putInt("Status", status.ordinal());
		return compoundNBT;
	}

	@Override
	public void read(CompoundTag nbt) {
		bountyLevel = nbt.getInt("bountyLevel");
		lastUpdate = nbt.getLong("lastUpdate");
		gameBoard = new EscritoireGameBoard(nbt);

		if (nbt.contains("Status")) {
			int statusOrdinal = nbt.getInt("Status");
			status = Status.values()[statusOrdinal];
		}

		lastUpdate = System.currentTimeMillis();
	}

	/* NETWORK */
	@Override
	public void writeData(FriendlyByteBuf data) {
		data.writeInt(bountyLevel);
		gameBoard.writeData(data);
		NetworkUtil.writeEnum(data, status);
	}

	@Override
	public void readData(FriendlyByteBuf data) {
		bountyLevel = data.readInt();
		gameBoard.readData(data);
		status = NetworkUtil.readEnum(data, Status.VALUES);
	}

	/* INTERACTION */
	public void initialize(ItemStack specimen) {
		reset();
		if (gameBoard.initialize(specimen)) {
			status = Status.PLAYING;
			bountyLevel = BOUNTY_MAX;
			lastUpdate = System.currentTimeMillis();
		}
	}

	public void probe(ItemStack specimen, Container inventory, int startSlot, int slotCount) {
		if (status != Status.PLAYING) {
			return;
		}

		IIndividualHandlerItem.ifPresent(specimen, individual -> {
			if (this.bountyLevel > 1) {
				this.bountyLevel--;
			}

			ISpecies<?> species = individual.getSpecies();
			@SuppressWarnings("unchecked")
			ISpeciesType<ISpecies<?>, ?> type = (ISpeciesType<ISpecies<?>, ?>) species.getType();
			this.gameBoard.hideProbedTokens();

			int revealCount = getSampleSize(slotCount);

			for (int i = 0; i < revealCount; i++) {
				ItemStack sample = inventory.removeItem(startSlot + i, 1);
				if (!sample.isEmpty()) {
					if (rand.nextFloat() < type.getResearchSuitability(species, sample)) {
						this.gameBoard.probe();
					}
				}
			}

			this.lastUpdate = System.currentTimeMillis();
		});
	}

	public void reset() {
		this.bountyLevel = BOUNTY_MAX;
		this.gameBoard.reset();
		this.status = Status.EMPTY;

		this.lastUpdate = System.currentTimeMillis();
	}

	public void choose(int tokenIndex) {
		if (this.status != Status.PLAYING) {
			return;
		}

		EscritoireGameToken token = gameBoard.getToken(tokenIndex);
		if (token != null) {
			this.status = gameBoard.choose(token);
			this.lastUpdate = System.currentTimeMillis();
		}
	}

	public int getBountyLevel() {
		return bountyLevel;
	}

	/* RETRIEVAL */
	public int getSampleSize(int slotCount) {
		if (this.status == Status.EMPTY) {
			return 0;
		}

		int samples = this.gameBoard.getTokenCount() / 4;
		samples = Math.max(samples, 2);
		return Math.min(samples, slotCount);
	}
}
