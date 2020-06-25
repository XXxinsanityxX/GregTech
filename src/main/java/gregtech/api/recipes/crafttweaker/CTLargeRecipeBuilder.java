package gregtech.api.recipes.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import gregtech.api.recipes.LargeRecipeBuilder;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.gregtech.recipe.LargeRecipeBuilder")
@ZenRegister
public class CTLargeRecipeBuilder extends CTRecipeBuilder {

    private final LargeRecipeBuilder largeRecipeBuilder;


    public CTLargeRecipeBuilder(LargeRecipeBuilder backingBuilder) {
        super(backingBuilder);
        this.largeRecipeBuilder = backingBuilder;
    }

    @ZenMethod
    public CTLargeRecipeBuilder dupeForSmall() {
        this.largeRecipeBuilder.dupeForSmall();
        return this;
    }


}