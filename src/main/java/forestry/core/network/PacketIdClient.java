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
package forestry.core.network;

import net.minecraft.resources.ResourceLocation;

import forestry.api.ForestryConstants;

/**
 * Packets sent to the client from the server
 */
public class PacketIdClient {
	// Core
	public static final ResourceLocation RECIPE_CACHE = ForestryConstants.forestry("recipe_cache");
	// Core Gui
	public static final ResourceLocation ERROR_UPDATE = ForestryConstants.forestry("error_update");
	public static final ResourceLocation GUI_UPDATE = ForestryConstants.forestry("gui_update");
	public static final ResourceLocation GUI_LAYOUT_SELECT = ForestryConstants.forestry("gui_layout_select");
	public static final ResourceLocation GUI_ENERGY = ForestryConstants.forestry("gui_energy");
	public static final ResourceLocation SOCKET_UPDATE = ForestryConstants.forestry("socket_update");
	// Core Tile Entities
	public static final ResourceLocation TILE_FORESTRY_UPDATE = ForestryConstants.forestry("tile_forestry_update");
	public static final ResourceLocation ITEMSTACK_DISPLAY = ForestryConstants.forestry("itemstack_display");
	public static final ResourceLocation FX_SIGNAL = ForestryConstants.forestry("fx_signal");
	public static final ResourceLocation TANK_LEVEL_UPDATE = ForestryConstants.forestry("tank_level_update");
	// Core Genome
	public static final ResourceLocation GENOME_TRACKER_UPDATE = ForestryConstants.forestry("genome_tracker_update");
	// Factory
	public static final ResourceLocation WORKTABLE_MEMORY_UPDATE = ForestryConstants.forestry("worktable_memory_update");
	public static final ResourceLocation WORKTABLE_CRAFTING_UPDATE = ForestryConstants.forestry("worktable_crafting_update");
	// Apiculture
	public static final ResourceLocation TILE_FORESTRY_ACTIVE = ForestryConstants.forestry("tile_forestry_active");
	public static final ResourceLocation BEE_LOGIC_ACTIVE = ForestryConstants.forestry("bee_logic_active");
	public static final ResourceLocation HABITAT_BIOME_POINTER = ForestryConstants.forestry("habitat_biome_pointer");
	public static final ResourceLocation ALVERAY_CONTROLLER_CHANGE = ForestryConstants.forestry("alveray_controller_change");
	// Arboriculture
	public static final ResourceLocation RIPENING_UPDATE = ForestryConstants.forestry("ripening_update");
	// Mail
	public static final ResourceLocation TRADING_ADDRESS_RESPONSE = ForestryConstants.forestry("trading_address_response");
	public static final ResourceLocation LETTER_INFO_RESPONSE_PLAYER = ForestryConstants.forestry("letter_info_response_player");
	public static final ResourceLocation LETTER_INFO_RESPONSE_TRADER = ForestryConstants.forestry("letter_info_response_trader");
	public static final ResourceLocation POBOX_INFO_RESPONSE = ForestryConstants.forestry("pobox_info_response");
	// Climate
	public static final ResourceLocation UPDATE_CLIMATE = ForestryConstants.forestry("update_climate");
	public static final ResourceLocation CLIMATE_LISTENER_UPDATE = ForestryConstants.forestry("climate_listener_update");
	public static final ResourceLocation CLIMATE_PLAYER = ForestryConstants.forestry("climate_player");
	// Sorting
	public static final ResourceLocation GUI_UPDATE_FILTER = ForestryConstants.forestry("gui_update_filter");
	// JEI
	public static final ResourceLocation RECIPE_TRANSFER_UPDATE = ForestryConstants.forestry("recipe_transfer_update");
}
