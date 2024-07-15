package forestry.apiculture;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.resources.ResourceLocation;

import forestry.api.genetics.alyzer.IAlleleDisplayHelper;
import forestry.api.genetics.alyzer.IAlyzerDisplayProvider;
import forestry.apiculture.genetics.IGeneticTooltipProvider;

import genetics.api.individual.IIndividual;
import forestry.api.genetics.ILifeStage;

public class DisplayHelper implements IAlleleDisplayHelper {
	private final Map<ResourceLocation, PriorityQueue<OrderedPair<IGeneticTooltipProvider<?>>>> tooltips = new HashMap<>();
	private final Map<ResourceLocation, PriorityQueue<OrderedPair<IAlyzerDisplayProvider>>> alyzers = new HashMap<>();

	@Nullable
	private static DisplayHelper instance;

	public static DisplayHelper getInstance() {
		if (instance == null) {
			instance = new DisplayHelper();
		}
		return instance;
	}

	@Override
	public void addTooltip(IGeneticTooltipProvider<?> provider, ResourceLocation id, int orderingInfo) {
		this.tooltips.computeIfAbsent(id, (root) -> new PriorityQueue<>()).add(new OrderedPair<>(provider, orderingInfo, null));
	}

	@Override
	public void addTooltip(IGeneticTooltipProvider<?> provider, ResourceLocation id, int orderingInfo, Predicate<ILifeStage> typeFilter) {
		this.tooltips.computeIfAbsent(id, (root) -> new PriorityQueue<>()).add(new OrderedPair<>(provider, orderingInfo, typeFilter));
	}

	@Override
	public void addAlyzer(IAlyzerDisplayProvider provider, ResourceLocation id, int orderingInfo) {
		this.alyzers.computeIfAbsent(id, (root) -> new PriorityQueue<>()).add(new OrderedPair<>(provider, orderingInfo, null));
	}

	public <I extends IIndividual> Collection<IGeneticTooltipProvider<I>> getTooltips(String rootUID, ILifeStage type) {
		if (!tooltips.containsKey(rootUID)) {
			return Collections.emptyList();
		}
		return tooltips.get(rootUID).stream()
				.filter((value) -> value.hasValue(type))
				.map((value) -> (IGeneticTooltipProvider<I>) value.value)
				.collect(Collectors.toList());
	}

	private static class OrderedPair<T> implements Comparable<OrderedPair<T>> {
		private final T value;
		private final int info;
		@Nullable
		private final Predicate<ILifeStage> filter;

		private OrderedPair(T value, int info, @Nullable Predicate<ILifeStage> filter) {
			this.value = value;
			this.info = info;
			this.filter = filter;
		}

		public boolean hasValue(ILifeStage type) {
			return filter == null || filter.test(type);
		}

		@Override
		public int compareTo(OrderedPair<T> o) {
			return info - o.info;
		}
	}
}
