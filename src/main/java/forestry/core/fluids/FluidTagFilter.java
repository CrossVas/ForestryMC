package forestry.core.fluids;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import net.minecraftforge.server.ServerLifecycleHooks;

import forestry.api.ForestryTags;

public class FluidTagFilter extends ReloadableFluidFilter {
	public static final FluidTagFilter WATER = new FluidTagFilter(FluidTags.WATER);
	public static final FluidTagFilter LAVA = new FluidTagFilter(FluidTags.LAVA);
	public static final FluidTagFilter HONEY = new FluidTagFilter(ForestryTags.Fluids.HONEY);

	public FluidTagFilter(TagKey<Fluid> tag) {
		super(() -> {
			HolderSet.Named<Fluid> set = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registry.FLUID_REGISTRY).getTag(tag).orElse(null);
			if (set == null || set.size() == 0) {
				return Set.of();
			}
			Set<ResourceLocation> ids = new HashSet<>(set.size());

			for (Holder<Fluid> holder : set) {
				ids.add(holder.unwrapKey().get().location());
			}

			return ids;
		});
	}
}
