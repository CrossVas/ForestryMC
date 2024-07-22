package forestry.apiculture.genetics;

import javax.annotation.Nullable;

import net.minecraft.world.item.Rarity;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.core.tooltips.ToolTip;
import forestry.api.core.ToleranceType;
import forestry.api.genetics.ClimateHelper;
import forestry.api.genetics.ForestrySpeciesTypes;
import forestry.api.genetics.alleles.BeeChromosomes;
import forestry.api.genetics.alleles.IAlleleFlowers;
import forestry.api.genetics.alyzer.IAlleleDisplayHandler;
import forestry.api.genetics.alyzer.IAlleleDisplayHelper;
import forestry.core.genetics.GenericRatings;
import forestry.core.utils.Translator;

import forestry.api.genetics.alleles.IAllele;
import forestry.api.genetics.alleles.IValueAllele;
import genetics.api.individual.IChromosomeAllele;
import forestry.api.genetics.alleles.IChromosome;
import genetics.api.individual.IChromosomeValue;
import forestry.api.genetics.IGenome;

public enum BeeDisplayHandler implements IAlleleDisplayHandler<IBee> {
	GENERATIONS(-1) {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
			int generation = individual.getGeneration();
			if (generation > 0) {
				Rarity rarity;
				if (generation >= 1000) {
					rarity = Rarity.EPIC;
				} else if (generation >= 100) {
					rarity = Rarity.RARE;
				} else if (generation >= 10) {
					rarity = Rarity.UNCOMMON;
				} else {
					rarity = Rarity.COMMON;
				}
				toolTip.translated("for.gui.beealyzer.generations", generation).style(rarity.color);
			}
		}
	}, SPECIES(BeeChromosomes.SPECIES, 0) {
	},
	SPEED(BeeChromosomes.SPEED, 2, 1) {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
			IValueAllele<Integer> speedAllele = getActive(genome);
			String customSpeedKey = "for.tooltip.worker." + speedAllele.getTranslationKey().replaceAll("(.*)\\.", "");
			if (Translator.canTranslateToLocal(customSpeedKey)) {
				toolTip.singleLine()
						.add(Component.translatable(customSpeedKey))
						.style(ChatFormatting.GRAY)
						.create();
			} else {
				toolTip.singleLine()
						.add(speedAllele.getDisplayName())
						.text(" ")
						.translated("for.gui.worker")
						.style(ChatFormatting.GRAY)
						.create();
			}
		}
	},
	LIFESPAN(BeeChromosomes.LIFESPAN, 1, 0) {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
			toolTip.singleLine()
					.add(genome.getActiveAllele(BeeChromosomes.LIFESPAN).getDisplayName())
					.text(" ")
					.translated("for.gui.life")
					.style(ChatFormatting.GRAY)
					.create();
		}
	},
	FERTILITY(BeeChromosomes.FERTILITY, 5, "fertility") {
	},
	TEMPERATURE_TOLERANCE(BeeChromosomes.TEMPERATURE_TOLERANCE, -1, 2) {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
			IAlleleBeeSpecies primary = genome.getActiveAllele(BeeChromosomes.SPECIES);
			IValueAllele<ToleranceType> tempToleranceAllele = getActive(genome);
			Component caption = ClimateHelper.toDisplay(primary.getTemperature());
			toolTip.singleLine()
					.text("T: ")
					.add(caption)
					.text(" / ")
					.add(tempToleranceAllele.getDisplayName())
					.style(ChatFormatting.GREEN)
					.create();
		}
	},
	HUMIDITY_TOLERANCE(BeeChromosomes.HUMIDITY_TOLERANCE, -1, 3) {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
			IAlleleBeeSpecies primary = genome.getActiveAllele(BeeChromosomes.SPECIES);
			IValueAllele<ToleranceType> humidToleranceAllele = getActive(genome);
			Component caption = ClimateHelper.toDisplay(primary.getHumidity());
			toolTip.singleLine()
					.text("H: ")
					.add(caption)
					.text(" / ")
					.add(humidToleranceAllele.getDisplayName())
					.style(ChatFormatting.GREEN)
					.create();
		}
	},
	FLOWER_PROVIDER(BeeChromosomes.FLOWER_TYPE, 4, 4, "flowers") {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
			IAlleleFlowers flowers = getActiveAllele(genome);
			toolTip.add(flowers.getType().getDescription(), ChatFormatting.GRAY);
		}
	},
	FLOWERING(BeeChromosomes.POLLINATION, 3, -1, "pollination"),
	NEVER_SLEEPS(BeeChromosomes.NEVER_SLEEPS, -1, 5) {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
			Boolean value = getActiveValue(genome);
			if (value) {
				toolTip.add(GenericRatings.rateActivityTime(true, false)).style(ChatFormatting.RED);
			}
		}
	},
	TOLERATES_RAIN(BeeChromosomes.TOLERATES_RAIN, -1, 6) {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
			Boolean value = getActiveValue(genome);
			if (value) {
				toolTip.translated("for.gui.flyer.tooltip").style(ChatFormatting.WHITE);
			}
		}
	},
	TERRITORY(BeeChromosomes.TERRITORY, 6, "area"),
	EFFECT(BeeChromosomes.EFFECT, 7, "effect");

	final IChromosome type;
	@Nullable
	final String alyzerCaption;
	final int alyzerIndex;
	final int tooltipIndex;

	BeeDisplayHandler(IChromosome type, int alyzerIndex) {
		this(type, alyzerIndex, -1, null);
	}

	BeeDisplayHandler(IChromosome type, int alyzerIndex, int tooltipIndex) {
		this(type, alyzerIndex, tooltipIndex, null);
	}

	BeeDisplayHandler(IChromosome type, int alyzerIndex, @Nullable String alyzerCaption) {
		this(type, alyzerIndex, -1, alyzerCaption);
	}

	BeeDisplayHandler(int tooltipIndex) {
		this.type = null;
		this.alyzerCaption = "";
		this.alyzerIndex = -1;
		this.tooltipIndex = tooltipIndex;
	}

	BeeDisplayHandler(IChromosome type, int alyzerIndex, int tooltipIndex, @Nullable String alyzerCaption) {
		this.type = type;
		this.alyzerCaption = alyzerCaption;
		this.alyzerIndex = alyzerIndex;
		this.tooltipIndex = tooltipIndex;
	}

	public static void init(IAlleleDisplayHelper helper) {
		for (BeeDisplayHandler handler : values()) {
			int tooltipIndex = handler.tooltipIndex;
			if (tooltipIndex >= 0) {
				helper.addTooltip(handler, ForestrySpeciesTypes.BEE, tooltipIndex * 10);
			}
			int alyzerIndex = handler.alyzerIndex;
			if (alyzerIndex >= 0) {
				helper.addAlyzer(handler, ForestrySpeciesTypes.BEE, alyzerIndex * 10);
			}
		}
	}

	@Override
	public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
		//Default Implementation
	}

	<V> IValueAllele<V> getActive(IGenome genome) {
		//noinspection unchecked
		return genome.getActiveAllele((IChromosomeValue<V>) type);
	}

	<V> IValueAllele<V> getInactive(IGenome genome) {
		//noinspection unchecked
		return genome.getInactiveAllele((IChromosomeValue<V>) type);
	}

	<A extends IAllele> A getActiveAllele(IGenome genome) {
		//noinspection unchecked
		return genome.getActiveAllele((IChromosomeAllele<A>) type);
	}

	<A extends IAllele> A getInactiveAllele(IGenome genome) {
		//noinspection unchecked
		return genome.getInactiveAllele((IChromosomeAllele<A>) type);
	}

	<V> V getActiveValue(IGenome genome) {
		//noinspection unchecked
		return genome.getActiveValue((IChromosomeValue<V>) type);
	}

	<V> V getInactiveValue(IGenome genome) {
		//noinspection unchecked
		return genome.getInactiveValue((IChromosomeValue<V>) type);
	}
}
