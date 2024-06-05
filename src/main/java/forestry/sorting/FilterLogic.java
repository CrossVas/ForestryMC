package forestry.sorting;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;

import forestry.api.core.ILocatable;
import forestry.api.genetics.alleles.AlleleManager;
import forestry.api.genetics.filter.IFilterData;
import forestry.api.genetics.filter.IFilterLogic;
import forestry.api.genetics.filter.IFilterRuleType;
import forestry.core.utils.NetworkUtil;
import forestry.sorting.network.packets.PacketFilterChangeGenome;
import forestry.sorting.network.packets.PacketFilterChangeRule;
import forestry.sorting.network.packets.PacketGuiFilterUpdate;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;
import genetics.utils.AlleleUtils;
import genetics.utils.RootUtils;

public class FilterLogic implements IFilterLogic {
	private final ILocatable locatable;
	private final INetworkHandler networkHandler;
	private IFilterRuleType[] filterRules = new IFilterRuleType[6];
	private AlleleFilter[][] genomeFilter = new AlleleFilter[6][3];

	public FilterLogic(ILocatable locatable, INetworkHandler networkHandler) {
		this.locatable = locatable;
		this.networkHandler = networkHandler;
		for (int i = 0; i < filterRules.length; i++) {
			filterRules[i] = AlleleManager.filterRegistry.getDefaultRule();
		}
	}

	@Override
	public INetworkHandler getNetworkHandler() {
		return networkHandler;
	}

	@Override
	public CompoundTag write(CompoundTag data) {
		for (int i = 0; i < filterRules.length; i++) {
			data.putString("TypeFilter" + i, filterRules[i].getUID());
		}

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				AlleleFilter filter = genomeFilter[i][j];
				if (filter == null) {
					continue;
				}
				if (filter.activeAllele != null) {
					data.putString("GenomeFilterS" + i + "-" + j + "-" + 0, filter.activeAllele.getRegistryName().toString());
				}
				if (filter.inactiveAllele != null) {
					data.putString("GenomeFilterS" + i + "-" + j + "-" + 1, filter.inactiveAllele.getRegistryName().toString());
				}
			}
		}
		return data;
	}

	@Override
	public void read(CompoundTag data) {
		for (int i = 0; i < filterRules.length; i++) {
			filterRules[i] = AlleleManager.filterRegistry.getRuleOrDefault(data.getString("TypeFilter" + i));
		}

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				AlleleFilter filter = new AlleleFilter();
				if (data.contains("GenomeFilterS" + i + "-" + j + "-" + 0)) {
					filter.activeAllele = AlleleUtils.getAlleleOrNull(data.getString("GenomeFilterS" + i + "-" + j + "-" + 0));
				}
				if (data.contains("GenomeFilterS" + i + "-" + j + "-" + 1)) {
					filter.inactiveAllele = AlleleUtils.getAlleleOrNull(data.getString("GenomeFilterS" + i + "-" + j + "-" + 1));
				}
				genomeFilter[i][j] = filter;
			}
		}
	}

	@Override
	public void writeGuiData(FriendlyByteBuf buffer) {
		writeFilterRules(buffer, filterRules);
		writeGenomeFilters(buffer, genomeFilter);
	}

	@Override
	public void readGuiData(FriendlyByteBuf buffer) {
		filterRules = readFilterRules(buffer);
		genomeFilter = readGenomeFilters(buffer);
	}

	public static void writeFilterRules(FriendlyByteBuf buffer, IFilterRuleType[] filterRules) {
		for (IFilterRuleType filterRule : filterRules) {
			buffer.writeShort(AlleleManager.filterRegistry.getId(filterRule));
		}
	}

	public static void writeGenomeFilters(FriendlyByteBuf buffer, AlleleFilter[][] genomeFilter) {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				AlleleFilter filter = genomeFilter[i][j];
				if (filter == null) {
					buffer.writeBoolean(false);
					buffer.writeBoolean(false);
					continue;
				}
				if (filter.activeAllele != null) {
					buffer.writeBoolean(true);
					buffer.writeUtf(filter.activeAllele.getRegistryName().toString());
				} else {
					buffer.writeBoolean(false);
				}
				if (filter.inactiveAllele != null) {
					buffer.writeBoolean(true);
					buffer.writeUtf(filter.inactiveAllele.getRegistryName().toString());
				} else {
					buffer.writeBoolean(false);
				}
			}
		}
	}

	public static IFilterRuleType[] readFilterRules(FriendlyByteBuf buffer) {
		IFilterRuleType[] filterRules = new IFilterRuleType[6];
		for (int i = 0; i < 6; i++) {
			filterRules[i] = AlleleManager.filterRegistry.getRule(buffer.readShort());
		}

		return filterRules;
	}

	public static AlleleFilter[][] readGenomeFilters(FriendlyByteBuf buffer) {
		AlleleFilter[][] genomeFilters = new AlleleFilter[6][32023];

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				AlleleFilter filter = new AlleleFilter();
				if (buffer.readBoolean()) {
					filter.activeAllele = AlleleUtils.getAlleleOrNull(buffer.readUtf(1024));
				}
				if (buffer.readBoolean()) {
					filter.inactiveAllele = AlleleUtils.getAlleleOrNull(buffer.readUtf(1024));
				}
				genomeFilters[i][j] = filter;
			}
		}

		return genomeFilters;
	}

	public Collection<Direction> getValidDirections(ItemStack itemStack, Direction from) {
		IRootDefinition<IIndividualRoot<IIndividual>> definition = RootUtils.getRoot(itemStack);
		IIndividual individual = null;
		IOrganismType type = null;
		if (definition.isPresent()) {
			IIndividualRoot<IIndividual> root = definition.get();
			individual = root.create(itemStack).orElse(null);
			type = root.getTypes().getType(itemStack).orElse(null);
		}
		IFilterData filterData = new FilterData(definition, individual, type);
		List<Direction> validFacings = new LinkedList<>();
		for (Direction facing : Direction.VALUES) {
			if (facing == from) {
				continue;
			}
			if (isValid(facing, itemStack, filterData)) {
				validFacings.add(facing);
			}
		}
		return validFacings;
	}

	@Override
	public boolean isValid(ItemStack itemStack, Direction facing) {
		IRootDefinition<IIndividualRoot<IIndividual>> definition = RootUtils.getRoot(itemStack);
		IIndividual individual = null;
		IOrganismType type = null;
		if (definition.isPresent()) {
			IIndividualRoot<IIndividual> root = definition.get();
			individual = root.create(itemStack).orElse(null);
			type = root.getTypes().getType(itemStack).orElse(null);
		}
		return isValid(facing, itemStack, new FilterData(definition, individual, type));
	}

	public boolean isValid(Direction facing, ItemStack itemStack, IFilterData filterData) {
		IFilterRuleType rule = getRule(facing);
		if (rule == DefaultFilterRuleType.CLOSED) {
			return false;
		}
		if (rule == DefaultFilterRuleType.ITEM && !filterData.isPresent()) {
			return true;
		}
		String requiredRoot = rule.getRootUID();
		if (requiredRoot != null && (!filterData.isPresent() || !filterData.getRoot().getUID().equals(requiredRoot))) {
			return false;
		}
		if (rule == DefaultFilterRuleType.ANYTHING || rule.isValid(itemStack, filterData)) {
			if (filterData.isPresent()) {
				IIndividual ind = filterData.getIndividual();
				IGenome genome = ind.getGenome();
				IAllele active = genome.getPrimary();
				IAllele inactive = genome.getSecondary();
				return isValidAllelePair(facing, active.getRegistryName().toString(), inactive.getRegistryName().toString());
			}
			return true;
		}
		return false;
	}

	public boolean isValidAllelePair(Direction orientation, String activeUID, String inactiveUID) {
		AlleleFilter[] directionFilters = genomeFilter[orientation.ordinal()];

		if (directionFilters == null) {
			return true;
		}

		boolean foundFilter = false;
		for (int i = 0; i < 3; i++) {
			AlleleFilter filter = directionFilters[i];
			if (filter != null && !filter.isEmpty()) {
				foundFilter = true;
				if (!filter.isEmpty() && filter.isValid(activeUID, inactiveUID)) {
					return true;
				}
			}
		}
		return !foundFilter;
	}

	public IFilterRuleType getRule(Direction facing) {
		return filterRules[facing.ordinal()];
	}

	public boolean setRule(Direction facing, IFilterRuleType rule) {
		if (filterRules[facing.ordinal()] != rule) {
			filterRules[facing.ordinal()] = rule;
			return true;
		}
		return false;
	}

	@Nullable
	public AlleleFilter getGenomeFilter(Direction facing, int index) {
		return genomeFilter[facing.ordinal()][index];
	}

	@Nullable
	public IAllele getGenomeFilter(Direction facing, int index, boolean active) {
		AlleleFilter filter = getGenomeFilter(facing, index);
		if (filter == null) {
			return null;
		}
		return active ? filter.activeAllele : filter.inactiveAllele;
	}

	public boolean setGenomeFilter(Direction facing, int index, boolean active, @Nullable IAllele allele) {
		AlleleFilter filter = genomeFilter[facing.ordinal()][index];
		if (filter == null) {
			filter = genomeFilter[facing.ordinal()][index] = new AlleleFilter();
		}
		boolean set;
		if (active) {
			set = filter.activeAllele != allele;
			filter.activeAllele = allele;
		} else {
			set = filter.inactiveAllele != allele;
			filter.inactiveAllele = allele;
		}
		return set;
	}

	@Override
	public void sendToServer(Direction facing, int index, boolean active, @Nullable IAllele allele) {
		NetworkUtil.sendToServer(new PacketFilterChangeGenome(locatable.getCoordinates(), facing, (short) index, active, allele));
	}

	@Override
	public void sendToServer(Direction facing, IFilterRuleType rule) {
		NetworkUtil.sendToServer(new PacketFilterChangeRule(locatable.getCoordinates(), facing, rule));
	}

	public PacketGuiFilterUpdate createGuiUpdatePacket(BlockPos pos) {
		return new PacketGuiFilterUpdate(pos, this.filterRules, this.genomeFilter);
	}

	public void readGuiUpdatePacket(PacketGuiFilterUpdate msg) {
		this.filterRules = msg.filterRules();
		this.genomeFilter = msg.genomeFilter();
	}
}
