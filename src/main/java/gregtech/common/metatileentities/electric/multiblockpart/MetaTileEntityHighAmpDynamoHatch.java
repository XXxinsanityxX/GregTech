package gregtech.common.metatileentities.electric.multiblockpart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.impl.EnergyContainerHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.render.SimpleOverlayRenderer;
import gregtech.api.render.Textures;
import gregtech.api.util.GTUtility;
import gregtech.api.util.PipelineUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;

public class MetaTileEntityHighAmpDynamoHatch extends MetaTileEntityEnergyHatch {

    private final int amps;

    public MetaTileEntityHighAmpDynamoHatch(ResourceLocation metaTileEntityId, int tier, int amps) {
        super(metaTileEntityId, tier, true);
        this.amps = amps;
        Field energyContainer = ObfuscationReflectionHelper.findField(MetaTileEntityEnergyHatch.class, "energyContainer");
        try {
            energyContainer.set(this, EnergyContainerHandler.emitterContainer(this, GTValues.V[tier]*8, GTValues.V[tier], amps));
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (shouldRenderOverlay()) {
            SimpleOverlayRenderer renderer = Textures.ENERGY_OUT_MULTI;
            renderer.renderSided(getFrontFacing(), renderState, translation, PipelineUtil.color(pipeline, GTValues.VC[getTier()]));
        }
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
        return new MetaTileEntityHighAmpDynamoHatch(metaTileEntityId, getTier(), amps);
    }
}