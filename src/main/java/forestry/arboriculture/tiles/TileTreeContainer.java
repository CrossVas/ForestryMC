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
package forestry.arboriculture.tiles;

import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.arboriculture.genetics.ITree;
import forestry.arboriculture.genetics.Tree;
import forestry.core.network.IStreamable;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.owner.OwnerHandler;
import forestry.core.utils.NBTUtilForestry;
import forestry.core.utils.RenderUtil;

import forestry.core.utils.SpeciesUtil;

/**
 * This is the base TE class for any block that needs to contain tree genome information.
 *
 * @author SirSengir
 */
public abstract class TileTreeContainer extends BlockEntity implements IStreamable, IOwnedTile {
	@Nullable
	private ITree containedTree;
	private final OwnerHandler ownerHandler = new OwnerHandler();

	public TileTreeContainer(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	/* SAVING & LOADING */
	@Override
	public void load(CompoundTag compoundNBT) {
		super.load(compoundNBT);

		if (compoundNBT.contains("ContainedTree")) {
			containedTree = new Tree(compoundNBT.getCompound("ContainedTree"));
		}
		ownerHandler.read(compoundNBT);
	}

	@Override
	public void saveAdditional(CompoundTag compoundNBT) {
		super.saveAdditional(compoundNBT);

		if (containedTree != null) {
			CompoundTag subcompound = new CompoundTag();
			containedTree.write(subcompound);
			compoundNBT.put("ContainedTree", subcompound);
		}
		ownerHandler.write(compoundNBT);
	}

	@Override
	public void writeData(FriendlyByteBuf data) {
		ITree tree = getTree();
		if (tree != null) {
			data.writeBoolean(true);
			ResourceLocation speciesId = tree.getSpecies().id();
			data.writeResourceLocation(speciesId);
		} else {
			data.writeBoolean(false);
		}
	}

	@Override
	public void readData(FriendlyByteBuf data) {
		if (data.readBoolean()) {
			ResourceLocation speciesId = data.readResourceLocation();
			ITree tree = getTree(speciesId);
			setTree(tree);
		}
	}

	private static ITree getTree(ResourceLocation speciesId) {
		return SpeciesUtil.TREE_TYPE.get().getSpecies(speciesId).createIndividual();
	}

	/* CLIENT INFORMATION */

	/* CONTAINED TREE */
	public void setTree(ITree tree) {
		this.containedTree = tree;
		if (level != null && level.isClientSide) {
			RenderUtil.markForUpdate(getBlockPos());
		}
	}

	@Nullable
	public ITree getTree() {
		return this.containedTree;
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return ownerHandler;
	}

	/* UPDATING */

	/**
	 * Leaves and saplings will implement their logic here.
	 */
	public abstract void onBlockTick(Level worldIn, BlockPos pos, BlockState state, RandomSource rand);

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		CompoundTag nbt = pkt.getTag();
		handleUpdateTag(nbt);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		return NBTUtilForestry.writeStreamableToNbt(this, tag);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleUpdateTag(CompoundTag tag) {
		super.handleUpdateTag(tag);
		NBTUtilForestry.readStreamableFromNbt(this, tag);
	}

}
