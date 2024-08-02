package forestry.api.plugin;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmType;

/**
 * Register farm related data here.
 * Obtain an instance from {@link IForestryPlugin#registerFarming}.
 */
public interface IFarmingRegistration {
	/**
	 * Creates a new farm type.
	 * Calling {@link IFarmTypeBuilder#setFertilizerConsumption} and {@link IFarmTypeBuilder#setWaterConsumption} is required.
	 *
	 * @param id           The unique ID of the farm type. See {@link forestry.api.farming.ForestryFarmTypes} for defaults.
	 * @param logicFactory The factory for creating IFarmLogic instances. The boolean is whether the logic should be manual.
	 * @param icon         The item
	 * @return A builder to customize the properties of this farm type.
	 */
	IFarmTypeBuilder createFarmType(ResourceLocation id, BiFunction<IFarmType, Boolean, IFarmLogic> logicFactory, ItemStack icon);

	/**
	 * Modifies an existing farm type.
	 *
	 * @param id     The ID of the farm type to modify. See {@link forestry.api.farming.ForestryFarmTypes} for defaults.
	 * @param action The modifications to this farm type.
	 */
	void modifyFarmType(ResourceLocation id, Consumer<IFarmTypeBuilder> action);

	/**
	 * Registers a new fertilizer item for use in the Forestry farms.
	 * By default, the only fertilizer is the Forestry fertilizer, which is worth {@code 500}.
	 *
	 * @param fertilizer The ingredient to match items against.
	 * @param amount     The amount of fertilizer a single item is worth. Forestry's fertilizer is worth {@code 500}.
	 */
	void registerFertilizer(Ingredient fertilizer, int amount);
}
