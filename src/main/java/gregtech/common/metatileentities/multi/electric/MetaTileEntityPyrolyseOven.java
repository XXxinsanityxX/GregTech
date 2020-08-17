package gregtech.common.metatileentities.multi.electric;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
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
import gregtech.common.blocks.BlockMachineCasing.MachineCasingType;
import gregtech.common.blocks.BlockWireCoil;
import gregtech.common.blocks.BlockWireCoil.CoilType;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;
import java.util.function.Predicate;

public class MetaTileEntityPyrolyseOven extends RecipeMapMultiblockController {

    private static final MultiblockAbility<?>[] ALLOWED_ABILITIES = {
        MultiblockAbility.IMPORT_ITEMS, MultiblockAbility.IMPORT_FLUIDS,
        MultiblockAbility.EXPORT_ITEMS, MultiblockAbility.EXPORT_FLUIDS,
        MultiblockAbility.INPUT_ENERGY
    };

    private int coilTierDiscount;

    public MetaTileEntityPyrolyseOven(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.PYROLYSE_RECIPES);
        this.recipeMapWorkable = new PyrolyseOvenWorkable(this);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
        return new MetaTileEntityPyrolyseOven(metaTileEntityId);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        if (isStructureFormed()) {
            textList.add(new TextComponentTranslation("gregtech.multiblock.pyrolyze_oven.coil_tier", coilTierDiscount));
        }
        super.addDisplayText(textList);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        CoilType coilType = context.getOrDefault("CoilType", CoilType.CUPRONICKEL);
        this.coilTierDiscount = coilType.getTierOverclock();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.coilTierDiscount = 0;
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
            .aisle("XXX", "XXX", "XXX")
            .aisle("CCC", "C#C", "CCC")
            .aisle("CCC", "C#C", "CCC")
            .aisle("XXX", "XSX", "XXX")
            .where('S', selfPredicate())
            .where('X', statePredicate(getCasingState()).or(abilityPartPredicate(ALLOWED_ABILITIES)))
            .where('C', heatingCoilPredicate())
            .where('#', isAirPredicate())
            .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.STEAM_CASING_STEEL;
    }

    protected IBlockState getCasingState() {
        return MetaBlocks.MACHINE_CASING.getState(MachineCasingType.STEEL_HULL);
    }

    protected class PyrolyseOvenWorkable extends MultiblockRecipeLogic {
        public PyrolyseOvenWorkable(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }
        @Override
        protected void setupRecipe(Recipe recipe) {
            int recipeDuration = recipe.getDuration();

            Recipe modified = recipeMap.recipeBuilder()
                .inputsIngredients(recipe.getInputs())
                .outputs(recipe.getOutputs())
                .fluidInputs(recipe.getFluidInputs())
                .fluidOutputs(recipe.getFluidOutputs())
                .EUt(recipe.getEUt())
                .duration((recipeDuration * 100 / coilTierDiscount))
                .build().getResult();
            super.setupRecipe(modified);
        }
    }
}
