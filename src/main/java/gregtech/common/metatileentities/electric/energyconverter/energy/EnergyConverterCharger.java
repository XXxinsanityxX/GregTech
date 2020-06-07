package gregtech.common.metatileentities.electric.energyconverter.energy;

import gregtech.common.metatileentities.electric.energyconverter.utils.Energy;

public interface EnergyConverterCharger {
	Number extractEnergy(final Energy p0, final Number p1, final boolean p2, final boolean p3);

	Number insertEnergy(final Energy p0, final Number p1, final boolean p2, final boolean p3);

	Number getStoredSum(final Energy p0, final boolean p1);

	Number getCapacitySum(final Energy p0, final boolean p1);
}
