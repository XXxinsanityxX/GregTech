package gregtech.common.metatileentities.multi.electric;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.gui.Widget.ClickData;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.multiblock.BlockPattern;
import gregtech.api.multiblock.BlockWorldState;
import gregtech.api.multiblock.FactoryBlockPattern;
import gregtech.api.multiblock.PatternMatchContext;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.render.ICubeRenderer;
import gregtech.api.render.Textures;
import gregtech.common.blocks.BlockMetalCasing.MetalCasingType;
import gregtech.common.blocks.BlockWireCoil;
import gregtech.common.blocks.BlockWireCoil.CoilType;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;
import java.util.function.Predicate;

public class MetaTileEntityElectricBlastFurnace extends RecipeMapMultiblockController {

    private static final MultiblockAbility<?>[] ALLOWED_ABILITIES = {
        MultiblockAbility.IMPORT_ITEMS, MultiblockAbility.IMPORT_FLUIDS,
        MultiblockAbility.EXPORT_ITEMS, MultiblockAbility.EXPORT_FLUIDS,
        MultiblockAbility.INPUT_ENERGY
    };

    private int blastFurnaceTemperature;
    private boolean overclock = false;

    public MetaTileEntityElectricBlastFurnace(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.BLAST_RECIPES);
        this.recipeMapWorkable = new BlastFurnanceWorkable(this);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
        return new MetaTileEntityElectricBlastFurnace(metaTileEntityId);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        if (isStructureFormed()) {
            textList.add(new TextComponentTranslation("gregtech.multiblock.blast_furnace.max_temperature", blastFurnaceTemperature));
            
            ITextComponent overclockText = new TextComponentTranslation("gregtech.multiblock.blast_furnace.coil_overclock");
            ITextComponent button = new TextComponentString(this.overclock ? "[ON]" : "[OFF]");
            overclockText.appendText(": ");
            overclockText.appendSibling(AdvancedTextWidget.withButton(button, "switch_overclock"));
            overclockText = AdvancedTextWidget.withHoverTextTranslate(overclockText, "gregtech.multiblock.blast_furnace.coil_overclock.desc");
            
            textList.add(overclockText);
        }
        super.addDisplayText(textList);
    }
    
	@Override
    protected void handleDisplayClick(String componentData, ClickData clickData) {
    	super.handleDisplayClick(componentData, clickData);
    	if (componentData.equals("switch_overclock")) {
    		this.overclock = !this.overclock;
    		writeCustomData(111, buf -> buf.writeBoolean(this.overclock));
    	}
    }
    
    
    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.blastFurnaceTemperature = context.getOrDefault("CoilType", CoilType.CUPRONICKEL).getCoilTemperature();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.blastFurnaceTemperature = 0;
    }

    @Override
    public boolean checkRecipe(Recipe recipe, boolean consumeIfSuccess) {
        int recipeRequiredTemp = recipe.getIntegerProperty("blast_furnace_temperature");
        return this.blastFurnaceTemperature >= recipeRequiredTemp;
    }

    public static Predicate<BlockWorldState> heatingCoilPredicate() {
        return blockWorldState -> {
            IBlockState blockState = blockWorldState.getBlockState();
            if (!(blockState.getBlock() instanceof BlockWireCoil))
                return false;
            BlockWireCoil blockWireCoil = (BlockWireCoil) blockState.getBlock();
            CoilType coilType = blockWireCoil.getState(blockState);
            CoilType currentCoilType = blockWorldState.getMatchContext().getOrPut("CoilType", coilType);
            return currentCoilType.getName().equals(coilType.getName());
        };
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
            .aisle("XXX", "CCC", "CCC", "XXX")
            .aisle("XXX", "C#C", "C#C", "XXX")
            .aisle("XSX", "CCC", "CCC", "XXX")
            .setAmountAtLeast('L', 10)
            .where('S', selfPredicate())
            .where('L', statePredicate(getCasingState()))
            .where('X', statePredicate(getCasingState()).or(abilityPartPredicate(ALLOWED_ABILITIES)))
            .where('C', heatingCoilPredicate())
            .where('#', isAirPredicate())
            .build();
    }

    protected IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(MetalCasingType.INVAR_HEATPROOF);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.HEAT_PROOF_CASING;
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
    	data.setBoolean("overclock", this.overclock);
    	return super.writeToNBT(data);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound data) {
    	this.overclock = data.getBoolean("overclock");
    	super.readFromNBT(data);
    }
    
    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
    	super.writeInitialSyncData(buf);
    	buf.writeBoolean(this.overclock);
    }
    
    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
    	super.receiveInitialSyncData(buf);
    	this.overclock = buf.readBoolean();
    }
    
    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
    	super.receiveCustomData(dataId, buf);
    	if (dataId == 111) {
    		this.overclock = buf.readBoolean();
    	}
    }
    
    protected class BlastFurnanceWorkable extends MultiblockRecipeLogic {

		public BlastFurnanceWorkable(RecipeMapMultiblockController tileEntity) {
			super(tileEntity);
		}
    	
		@Override
		protected void setupRecipe(Recipe recipe) {
			int excessTemp = blastFurnaceTemperature - recipe.getIntegerProperty("blast_furnace_temperature");
			int recipeDuration = recipe.getDuration();
			int initialEUt = recipe.getEUt();
			
			while (excessTemp >= 900 && overclock && (initialEUt * 1.15F) <= getMaxVoltage()) {
				if (overclock) {
					recipeDuration = Math.max(1, (int)(recipeDuration * 0.75));
					initialEUt = Math.min(Integer.MAX_VALUE, (int)(initialEUt * 1.15F));
				} else {
					initialEUt = Math.max(1, (int)(initialEUt * 0.9F));
				}
				excessTemp -= 900;
			}
			
			Recipe modified = recipeMap.recipeBuilder()
					.inputsIngredients(recipe.getInputs())
					.outputs(recipe.getOutputs())
                    .fluidInputs(recipe.getFluidInputs())
                    .fluidOutputs(recipe.getFluidOutputs())
					.EUt(initialEUt)
					.duration(recipeDuration)
					.build().getResult();
			super.setupRecipe(modified);
		}
    }
    
}
