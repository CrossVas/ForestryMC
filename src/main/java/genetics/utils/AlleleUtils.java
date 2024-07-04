package genetics.utils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.resources.ResourceLocation;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleValue;
import genetics.api.individual.IChromosomeAllele;
import genetics.api.individual.IChromosomeType;

public class AlleleUtils {

	private AlleleUtils() {
	}

	public static boolean isBlacklisted(IAllele allele) {
		return isBlacklisted(allele.getRegistryName());
	}

	public static boolean isBlacklisted(String registryName) {
		return isBlacklisted(new ResourceLocation(registryName));
	}

	public static boolean isBlacklisted(ResourceLocation registryName) {
		return GeneticsAPI.apiInstance.getAlleleRegistry().isBlacklisted(registryName);
	}

	public static <A extends IAllele> A getAlleleOr(String registryName, A fallback) {
		return getAlleleOr(new ResourceLocation(registryName), fallback);
	}

	public static <A extends IAllele> A getAlleleOr(ResourceLocation registryName, A fallback) {
		A allele = getAllele(registryName);
		return allele == null ? fallback : allele;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static <A extends IAllele> A getAllele(ResourceLocation registryName) {
		return (A) GeneticsAPI.apiInstance.getAlleleRegistry().getAllele(registryName);
	}

	@Nullable
	public static <A extends IAllele> A getAllele(String registryName) {
		return getAllele(new ResourceLocation(registryName));
	}

	public static void actOn(ResourceLocation location, Consumer<IAllele> alleleAction) {
		actOn(location, IAllele.class, alleleAction);
	}

	public static <R> R callOn(ResourceLocation location, Function<IAllele, R> alleleAction, R fallback) {
		return callOn(location, IAllele.class, alleleAction, fallback);
	}

	public static <A extends IAllele> void actOn(ResourceLocation location, Class<? extends A> alleleClass, Consumer<A> alleleAction) {
		IAllele allele = getAllele(location);
		if (!alleleClass.isInstance(allele)) {
			return;
		}
		A castedAllele = alleleClass.cast(allele);
		alleleAction.accept(castedAllele);
	}

	public static <A extends IAllele, R> R callOn(ResourceLocation location, Class<? extends A> alleleClass, Function<A, R> alleleAction, R fallback) {
		IAllele allele = getAllele(location);
		if (!alleleClass.isInstance(allele)) {
			return fallback;
		}
		A castedAllele = alleleClass.cast(allele);
		return alleleAction.apply(castedAllele);
	}

	public static Collection<ResourceLocation> getRegisteredNames() {
		return GeneticsAPI.apiInstance.getAlleleRegistry().getRegisteredNames();
	}

	public static Collection<IAllele> getAlleles() {
		return GeneticsAPI.apiInstance.getAlleleRegistry().getRegisteredAlleles();
	}

	public static Collection<IChromosomeType> getChromosomeTypes(IAllele allele) {
		return GeneticsAPI.apiInstance.getAlleleRegistry().getChromosomeTypes(allele);
	}

	public static Collection<IAllele> getAllelesByType(IChromosomeType type) {
		return GeneticsAPI.apiInstance.getAlleleRegistry().getRegisteredAlleles(type);
	}

	public static <A extends IAllele> Collection<A> filteredAlleles(IChromosomeAllele<A> type) {
		return filteredStream(type).collect(Collectors.toSet());
	}

	public static <A extends IAllele> void forEach(IChromosomeAllele<A> type, Consumer<A> action) {
		filteredStream(type).forEach(action);
	}

	public static <A extends IAllele> Stream<A> filteredStream(IChromosomeAllele<A> type) {
		return getAllelesByType(type).stream()
			.filter(allele -> type.getAlleleClass().isInstance(allele))
			.map(type::castAllele);
	}

	/**
	 * @return The value of the allele if the allele is an instance of {@link IAlleleValue} and not null.
	 * Otherwise it returns the given fallback object.
	 */
	public static <V> V getAlleleValue(IAllele allele, Class<? extends V> valueClass, V fallback) {
		if (!(allele instanceof IAlleleValue<?> alleleValue)) {
			return fallback;
		}
		Object value = alleleValue.getValue();
		if (valueClass.isInstance(value)) {
			return valueClass.cast(value);
		}
		return fallback;
	}

	/**
	 * @return The value of the allele if the allele is an instance of {@link IAlleleValue} and not null.
	 * Otherwise it returns null.
	 */
	@Nullable
	public static <V> V getAlleleValue(IAllele allele, Class<? extends V> valueClass) {
		if (!(allele instanceof IAlleleValue<?> alleleValue)) {
			return null;
		}
		Object value = alleleValue.getValue();
		if (valueClass.isInstance(value)) {
			return valueClass.cast(value);
		}
		return null;
	}
}
