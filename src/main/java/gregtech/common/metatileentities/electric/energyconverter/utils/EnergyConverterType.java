package gregtech.common.metatileentities.electric.energyconverter.utils;

import gregtech.api.items.OreDictNames;
import gregtech.common.ConfigHolder;
import gregtech.loaders.recipe.CraftingComponent;

public enum EnergyConverterType implements EnergyConverterCraftingHelper.RecipeFunction {
	CONVERT_GTEU(ConverterType.GTEU_TO_FORGE, true) {
		@Override
		public Object[] createRecipe(final int tier, final int slots) {
			return new Object[]{"WTW", "RMR", "WSW", 'M', CraftingComponent.HULL.getIngredient(tier), 'W', EnergyConverterCraftingHelper.HELPER.cable(tier, slots), 'T', OreDictNames.chestWood, 'R', EnergyConverterCraftingHelper.HELPER.redCable(slots), 'S', CraftingComponent.CIRCUIT.getIngredient(tier)};
		}
	},
	CONVERT_FORGE(ConverterType.GTEU_TO_FORGE, false) {
		@Override
		public Object[] createRecipe(final int tier, final int slots) {
			return new Object[]{"WSW", "RMR", "WTW", 'M', CraftingComponent.HULL.getIngredient(tier), 'W', EnergyConverterCraftingHelper.HELPER.cable(tier, slots), 'T', OreDictNames.chestWood, 'R', EnergyConverterCraftingHelper.HELPER.redCable(slots), 'S', CraftingComponent.CIRCUIT.getIngredient(tier)};
		}
	};

	private final ConverterType type;
	private final boolean isGTEU;

	EnergyConverterType(final ConverterType type, final boolean isGTEU) {
		this.type = type;
		this.isGTEU = isGTEU;
	}

	public ConverterType getConverterType() {
		return this.type;
	}

	public boolean isGTEU() {
		return this.isGTEU;
	}

	public Energy getInput() {
		return this.type.getInput(this.isGTEU);
	}

	public Energy getOutput() {
		return this.type.getOutput(this.isGTEU);
	}

	public Ratio ratio() {
		return type.getOutput(isGTEU) == Energy.FE ?
				Ratio.ratioOf(1, ConfigHolder.energyConverter.RatioEUtoRF) : //EU to RF
				Ratio.ratioOf(ConfigHolder.energyConverter.RatioRFtoEU, 1); //RF to RF
	}

	public boolean isDisabled() {
		return ConfigHolder.energyConverter.Disable;
	}

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
