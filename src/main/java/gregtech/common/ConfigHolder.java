package gregtech.common;

import gregtech.api.GTValues;
import net.minecraftforge.common.config.Config;

@Config(modid = GTValues.MODID)
public class ConfigHolder {

    @Config.Comment("Whether to enable more verbose logging. Default: false")
    public static boolean debug = false;

    @Config.Comment("Whether to increase number of rolls for dungeon chests. Increases dungeon loot drastically.")
    public static boolean increaseDungeonLoot = true;

    @Config.Comment("Whether to hide facades of all blocks in JEI and creative search menu")
    public static boolean hideFacadesInJEI = true;

    @Config.Comment("Specifies min amount of veins in section")
    public static int minVeinsInSection = 5;

    @Config.Comment("Specifies additional random amount of veins in section")
    public static int additionalVeinsInSection = 0;

    @Config.Comment("Whether to disable vanilla ores generation in world. Default is false.")
    public static boolean disableVanillaOres = true;

    @Config.Comment("Whether to disable rubber tree world generation. Default is false.")
    @Config.RequiresMcRestart
    public static boolean disableRubberTreeGeneration = false;

    @Config.Comment("Whether machines should explode when overloaded with power. Default: true")
    public static boolean doExplosions = true;

    @Config.Comment("Energy use multiplier for electric items. Default: 100")
    public static int energyUsageMultiplier = 100;

    @Config.Comment("Chance of generating abandoned base in chunk = 1 / THIS_VALUE. 0 disables abandoned base generation")
    public static int abandonedBaseRarity = 0;

    @Config.RangeInt(min = 0, max = 100)
    @Config.Comment("Chance with which flint and steel will create fire. Default: 50")
    public static int flintChanceToCreateFire = 50;

    @Config.Comment("Recipes for machine hulls use more materials. Default: false")
    @Config.RequiresMcRestart
    public static boolean harderMachineHulls = true;

    @Config.Comment("If true, insufficient energy supply will reset recipe progress to zero. If false, progress will slowly go back (with 2x speed)")
    @Config.RequiresWorldRestart
    public static boolean insufficientEnergySupplyWipesRecipeProgress = false;

    @Config.Comment("Whether to use modPriorities setting in config for prioritizing ore dictionary item registrations. " +
        "By default, GTCE will sort ore dictionary registrations alphabetically comparing their owner ModIDs.")
    public static boolean useCustomModPriorities = false;

    @Config.Comment("Specifies priorities of mods in ore dictionary item registration. First ModID has highest priority, last - lowest. " +
        "Unspecified ModIDs follow standard sorting, but always have lower priority than last specified ModID.")
    @Config.RequiresMcRestart
    public static String[] modPriorities = new String[0];

    @Config.Comment("Setting this to true makes GTCE ignore error and invalid recipes that would otherwise cause crash. Default to true.")
    @Config.RequiresMcRestart
    public static boolean ignoreErrorOrInvalidRecipes = true;

    @Config.Comment("Setting this to false causes GTCE to not register additional methane recipes for foods in the centrifuge.")
    @Config.RequiresMcRestart
    public static boolean addFoodMethaneRecipes = true;

    @Config.Comment("Category that contains configs for changing vanilla recipes")
    @Config.RequiresMcRestart
    public static VanillaRecipes vanillaRecipes = new VanillaRecipes();

    @Config.Comment("Sets the bonus EU output of Steam Turbines.")
    @Config.RequiresMcRestart
    public static int steamTurbineBonusOutput = 6144;

    @Config.Comment("Sets the bonus EU output of Plasma Turbines.")
    @Config.RequiresMcRestart
    public static int plasmaTurbineBonusOutput = 6144;    

    @Config.Comment("Sets the bonus EU output of Gas Turbines.")
    @Config.RequiresMcRestart
    public static int gasTurbineBonusOutput = 6144;    
    
    public static class VanillaRecipes {

        @Config.Comment("Whether to nerf paper crafting recipe. Default is true.")
        public boolean nerfPaperCrafting = true;

        @Config.Comment("Whether to make flint and steel recipe require steel nugget instead of iron one. Default is true")
        public boolean flintAndSteelRequireSteel = true;

        @Config.Comment("Whether to nerf wood crafting to 2 planks from 1 log. Default is false.")
        public boolean nerfWoodCrafting = true;

        @Config.Comment("Whether to nerf wood crafting to 2 sticks from 2 planks. Default is false.")
        public boolean nerfStickCrafting = true;

        @Config.Comment("Whether to make iron bucket recipe harder by requiring hammer and plates. Default is true.")
        public boolean bucketRequirePlatesAndHammer = true;

        @Config.Comment("Recipes for items like iron doors, trapdors, pressure plates, cauldron, hopper and iron bars require iron plates and hammer. Default is true")
        public boolean ironConsumingCraftingRecipesRequirePlates = true;

        @Config.Comment("Require a knife for bowl crafting instead of only plank? Default is true.")
        public boolean bowlRequireKnife = true;

    }

    @Config.Comment("Config options for Energy Converter features")
    public static EnergyConverter energyConverter = new EnergyConverter();

    public static class EnergyConverter {
        @Config.Comment("True if you want the Energy Converter to only accept batteries that match its current tier. False if you want the Energy Converter to accept any tier of batteries.")
        public boolean PermitOnlyExactVoltage = true;

        @Config.Comment("Should GregTech EU to RF energy converters be registered.")
        public boolean registerEUtoRFconverter = true;

        @Config.Comment("Should RF to GregTech EU energy converters be registered.")
        public boolean registerRFtoEUconverter = true;

        @Config.Comment("Ratio 1 EU to X RF")
        public int RatioEUtoRF = 4;

        @Config.Comment("Ratio X RF to 1 EU")
        public int RatioRFtoEU = 4;
    }
}
