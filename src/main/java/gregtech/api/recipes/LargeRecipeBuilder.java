package gregtech.api.recipes;

import com.google.common.collect.ImmutableMap;
import gregtech.api.util.ValidationResult;
import net.minecraftforge.fluids.FluidStack;

public class LargeRecipeBuilder extends RecipeBuilder<LargeRecipeBuilder> {

    private final RecipeMap<?> simple;
    private boolean dupeForSimple = false;

    public LargeRecipeBuilder(RecipeMap<?> simple) {
        this.simple = simple;
    }

    public LargeRecipeBuilder(LargeRecipeBuilder recipeBuilder) {
        super(recipeBuilder);
        this.simple = recipeBuilder.simple;
    }

    @Override
    public LargeRecipeBuilder copy() {
        return new LargeRecipeBuilder(this);
    }

    public LargeRecipeBuilder dupeForSmall() {
        dupeForSimple = true;
        return this;
    }


    @Override
    public void buildAndRegister() {
        if (dupeForSimple) {
            RecipeBuilder<?> builder = simple.recipeBuilder()
                .inputsIngredients(inputs)
                .fluidInputs(fluidInputs.toArray(new FluidStack[0]))
                .fluidOutputs(fluidOutputs.toArray(new FluidStack[0]))
                .outputs(outputs)
                .duration(duration)
                .EUt(EUt);

            this.chancedOutputs.forEach(chanceEntry -> builder.chancedOutput(chanceEntry.getItemStack(), chanceEntry.getChance(), chanceEntry.getBoostPerTier()));
            builder.buildAndRegister();
        }
        super.buildAndRegister();
    }

    public ValidationResult<Recipe> build() {
        return ValidationResult.newResult(finalizeAndValidate(),
            new Recipe(inputs, outputs, chancedOutputs, fluidInputs, fluidOutputs,
                ImmutableMap.of(), duration, EUt, hidden));
    }
}