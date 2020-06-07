package gregtech.common.metatileentities.electric.energyconverter.energy;

import gregtech.api.GTValues;
import gregtech.api.capability.IElectricItem;
import gregtech.common.metatileentities.electric.energyconverter.MetaTileEntityEnergyConverter;
import gregtech.common.metatileentities.electric.energyconverter.utils.Energy;
import gregtech.common.metatileentities.electric.energyconverter.utils.Ratio;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.function.BiConsumer;

public class ElectricItemFE implements IElectricItem {
    private final MetaTileEntityEnergyConverter energyConverter;
    private final IEnergyStorage storage;
    private final Ratio ratio;

    public ElectricItemFE(final MetaTileEntityEnergyConverter energyConverter, final IEnergyStorage storage) {
        this.energyConverter = energyConverter;
        this.storage = storage;
        this.ratio = ((energyConverter.getType().getInput() == Energy.FE) ? energyConverter.ratio() : energyConverter.ratio().reverse());
    }

    public boolean canProvideChargeExternally() {
        return true;
    }

    public void addChargeListener(final BiConsumer<ItemStack, Long> chargeListener) {
    }

    public long charge(final long amount, final int tier, final boolean ignoreTransferLimit, final boolean simulate) {
        return this.ratio.convertToLong(this.storage.receiveEnergy(this.ratio.reverse().convertToInt(amount), simulate));
    }

    public long discharge(final long amount, final int tier, final boolean ignoreTransferLimit, final boolean externally, final boolean simulate) {
        return this.ratio.convertToLong(this.storage.extractEnergy(this.ratio.reverse().convertToInt(amount), simulate));
    }

    public long getCharge() {
        return this.ratio.convertToLong(this.storage.getEnergyStored());
    }

    public long getMaxCharge() {
        return this.ratio.convertToLong(this.storage.getMaxEnergyStored());
    }

    public boolean canUse(final long amount) {
        return this.ratio.convertToLong(this.storage.getEnergyStored()) >= amount;
    }

    public int getTier() {
        return this.energyConverter.getTier();
    }

    public long getTransferLimit() {
        return GTValues.V[this.getTier()];
    }
}
