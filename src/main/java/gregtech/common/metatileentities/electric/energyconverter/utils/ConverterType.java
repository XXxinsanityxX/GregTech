package gregtech.common.metatileentities.electric.energyconverter.utils;

public enum ConverterType {
    GTEU_TO_FORGE(Energy.FE);

    private final Energy energyOutput;

    ConverterType(final Energy energyOutput) {
        if (energyOutput == Energy.GTEU) {
            throw new IllegalArgumentException();
        }
        this.energyOutput = energyOutput;
    }

    public Energy getEnergyOutput() {
        return this.energyOutput;
    }

    public Energy getInput(final boolean isGTEU) {
        return isGTEU ? Energy.GTEU : this.energyOutput;
    }

    public Energy getOutput(final boolean isGTEU) {
        return isGTEU ? this.energyOutput : Energy.GTEU;
    }


}
