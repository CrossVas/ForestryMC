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
package forestry.core.errors;

import com.google.common.collect.ImmutableSet;

import net.minecraft.network.FriendlyByteBuf;

import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorState;

public enum FakeErrorLogic implements IErrorLogic {
	INSTANCE;

	@Override
	public boolean setCondition(boolean condition, IErrorState errorState) {
		return false;
	}

	@Override
	public boolean contains(IErrorState state) {
		return false;
	}

	@Override
	public boolean hasErrors() {
		return true;
	}

	@Override
	public void clearErrors() {
	}

	@Override
	public void writeData(FriendlyByteBuf data) {
	}

	@Override
	public void readData(FriendlyByteBuf data) {
	}

	@Override
	public void copy(IErrorLogic errorLogic) {
	}

	@Override
	public ImmutableSet<IErrorState> getErrorStates() {
		return ImmutableSet.of();
	}
}
