package gregtech.common.metatileentities.electric.energyconverter.utils;

import gregtech.common.ConfigHolder;

public enum EnergyConverterType{
	CONVERT_GTEU(ConverterType.GTEU_TO_FORGE, true) {},
	CONVERT_FORGE(ConverterType.GTEU_TO_FORGE, false) {};

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

    @Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
