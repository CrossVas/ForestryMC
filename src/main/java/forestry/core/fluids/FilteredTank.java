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
package forestry.core.fluids;

import com.google.common.base.Suppliers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.Rarity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import forestry.api.core.tooltips.ToolTip;
import forestry.core.utils.ModUtil;

public class FilteredTank extends StandardTank {
	private Supplier<Set<ResourceLocation>> filters = Suppliers.ofInstance(new HashSet<>());

	public FilteredTank(int capacity) {
		super(capacity);
		setValidator(this::fluidMatchesFilter);
	}

	public FilteredTank(int capacity, boolean canFill, boolean canDrain) {
		super(capacity, canFill, canDrain);
	}

	public FilteredTank setFilter(Supplier<Set<ResourceLocation>> filters) {
		this.filters = filters;
		return this;
	}

	public FilteredTank setFilters(Collection<Fluid> filters) {
		Set<ResourceLocation> set = new HashSet<>();
		this.filters = () -> set;
		for (Fluid fluid : filters) {
			set.add(ModUtil.getRegistryName(fluid));
		}
		return this;
	}

	private boolean fluidMatchesFilter(FluidStack resource) {
		return resource.getFluid() != Fluids.EMPTY && filters.get().contains(ModUtil.getRegistryName(resource.getFluid()));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void refreshTooltip() {
		if (hasFluid()) {
			super.refreshTooltip();
			return;
		}

		ToolTip toolTip = getToolTip();
		toolTip.clear();
		Set<ResourceLocation> filters = this.filters.get();

		if (Screen.hasShiftDown() || filters.size() < 5) {
			for (ResourceLocation filterName : filters) {
				Fluid fluidFilter = ForgeRegistries.FLUIDS.getValue(filterName);
				FluidType attributes = fluidFilter.getFluidType();
				Rarity rarity = attributes.getRarity();
				if (rarity == null) {
					rarity = Rarity.COMMON;
				}
				FluidStack filterFluidStack = new FluidStack(fluidFilter, 1);
				toolTip.add(filterFluidStack.getDisplayName(), rarity.color);
			}
		} else {
			Component tmiComponent = Component.literal("<")
					.append(Component.translatable("for.gui.tooltip.tmi"))
					.append(Component.literal(">"));
			toolTip.add(tmiComponent, ChatFormatting.ITALIC);
		}
		toolTip.add(Component.translatable("for.gui.tooltip.liquid.amount", getFluidAmount(), getCapacity()));
	}
}
