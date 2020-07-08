package gregtech.loaders.oreprocessing;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.items.metaitem.MetaItem.MetaValueItem;
import gregtech.api.items.toolitem.ToolMetaItem;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.DustMaterial;
import gregtech.api.unification.material.type.IngotMaterial;
import gregtech.api.unification.material.type.SolidMaterial;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.items.MetaItems;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import static gregtech.api.unification.material.type.DustMaterial.MatFlags.GENERATE_PLATE;
import static gregtech.api.unification.material.type.IngotMaterial.MatFlags.GENERATE_BOLT_SCREW;
import static gregtech.api.unification.material.type.SolidMaterial.MatFlags.GENERATE_ROD;

public class ToolRecipeHandler {

    public static void register() {
        OrePrefix.stick.addProcessingHandler(SolidMaterial.class, ToolRecipeHandler::processStick);
        OrePrefix.stickLong.addProcessingHandler(IngotMaterial.class, ToolRecipeHandler::processLongStick);
    }

    public static MetaValueItem[] motorItems;
    public static SolidMaterial[] baseMaterials;
    public static MetaValueItem[][] batteryItems;

    public static void initializeMetaItems() {
        motorItems = new MetaValueItem[]{MetaItems.ELECTRIC_MOTOR_LV, MetaItems.ELECTRIC_MOTOR_MV, MetaItems.ELECTRIC_MOTOR_HV};
        baseMaterials = new SolidMaterial[]{Materials.StainlessSteel, Materials.Titanium, Materials.TungstenSteel};
        batteryItems = new MetaValueItem[][]{
            {MetaItems.BATTERY_RE_LV_LITHIUM, MetaItems.BATTERY_RE_LV_CADMIUM, MetaItems.BATTERY_RE_LV_SODIUM},
            {MetaItems.BATTERY_RE_MV_LITHIUM, MetaItems.BATTERY_RE_MV_CADMIUM, MetaItems.BATTERY_RE_MV_SODIUM},
            {MetaItems.BATTERY_RE_HV_LITHIUM, MetaItems.BATTERY_RE_HV_CADMIUM, MetaItems.BATTERY_RE_HV_SODIUM}};
    }

    public static void registerPowerUnitRecipes() {
        for(MetaValueItem batteryItem : batteryItems[2]) {
            ItemStack batteryStack = batteryItem.getStackForm();
            long maxCharge = batteryStack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null).getMaxCharge();
            ItemStack hammerBaseStack = MetaItems.JACKHAMMER_BASE.getMaxChargeOverrideStack(maxCharge);
            String recipeName = String.format("jackhammer_base_%s", batteryItem.unlocalizedName);

            ModHandler.addShapedEnergyTransferRecipe(recipeName, hammerBaseStack,
                Ingredient.fromStacks(batteryStack), false,
                "S  ", "PRP", "MPB",
                'M', MetaItems.ELECTRIC_PISTON_HV.getStackForm(),
                'S', new UnificationEntry(OrePrefix.screw, Materials.Titanium),
                'P', new UnificationEntry(OrePrefix.plateDense, Materials.Titanium),
                'R', new UnificationEntry(OrePrefix.spring, Materials.Titanium),
                'B', batteryStack);
        }
    }

    public static void processStick(OrePrefix stickPrefix, SolidMaterial material) {
        if (material.hasFlag(DustMaterial.MatFlags.NO_SMASHING)) {
            return;
        }

        if (material instanceof IngotMaterial && material.toolDurability > 0) {
            ModHandler.addShapedRecipe(String.format("plunger_%s", material),
                MetaItems.PLUNGER.getStackForm(material),
                "xRR", " SR", "S f",
                'S', new UnificationEntry(OrePrefix.stick, material),
                'R', new UnificationEntry(OrePrefix.plate, Materials.Rubber));
        }

        SolidMaterial handleMaterial = Materials.Wood;
        if (material.hasFlag(GENERATE_ROD) && material.toolDurability > 0) {
            ModHandler.addShapedRecipe(String.format("screwdriver_%s_%s", material.toString(), handleMaterial.toString()),
                MetaItems.SCREWDRIVER.getStackForm(material),
                " fS", " Sh", "W  ",
                'S', new UnificationEntry(OrePrefix.stick, material),
                'W', new UnificationEntry(OrePrefix.stick, handleMaterial));

            ModHandler.addShapedRecipe(String.format("crowbar_%s", material),
                MetaItems.CROWBAR.getStackForm(material),
                "hDS", "DSD", "SDf",
                'S', new UnificationEntry(OrePrefix.stick, material),
                'D', new UnificationEntry(OrePrefix.dye, MarkerMaterials.Color.COLORS.get(EnumDyeColor.BLUE)));

            if (material instanceof IngotMaterial) {
                ModHandler.addShapedRecipe(String.format("file_%s", material),
                    MetaItems.FILE.getStackForm(material),
                    "P", "P", "S",
                    'P', new UnificationEntry(OrePrefix.plate, material),
                    'S', new UnificationEntry(OrePrefix.stick, handleMaterial));
            }
            if (material instanceof IngotMaterial) {
                ModHandler.addShapedRecipe(String.format("saw_%s", material),
                    MetaItems.SAW.getStackForm(material),
                    "PPS", "fh ",
                    'P', new UnificationEntry(OrePrefix.plate, material),
                    'S', new UnificationEntry(OrePrefix.stick, handleMaterial));
            }
        }

        if (material.hasFlag(GENERATE_PLATE) && material.toolDurability > 0) {
            ModHandler.addShapedRecipe(String.format("knife_%s", material.toString()),
                MetaItems.KNIFE.getStackForm(material),
                "fPh", " S ",
                'S', new UnificationEntry(stickPrefix, material),
                'P', new UnificationEntry(OrePrefix.plate, material));
        }

        if (material instanceof IngotMaterial) {
            ModHandler.addShapedRecipe(String.format("hammer_%s", material.toString()),
                MetaItems.HARD_HAMMER.getStackForm(material),
                "XX ", "XXS", "XX ",
                'X', new UnificationEntry(OrePrefix.ingot, material),
                'S', new UnificationEntry(OrePrefix.stick, handleMaterial));
        }

        if (material.hasFlag(GENERATE_PLATE | GENERATE_ROD) && material.toolDurability > 0) {
            ModHandler.addShapedRecipe(String.format("butchery_knife_%s", material.toString()),
                MetaItems.BUTCHERY_KNIFE.getStackForm(material),
                "PPf", "PP ", "Sh ",
                'S', new UnificationEntry(OrePrefix.stick, material),
                'P', new UnificationEntry(OrePrefix.plate, material));
        }

        if (material.hasFlag(GENERATE_PLATE | GENERATE_ROD | GENERATE_BOLT_SCREW) && material.toolDurability > 0) {
            ModHandler.addShapedRecipe(String.format("wire_cutter_%s", material.toString()),
                MetaItems.WIRE_CUTTER.getStackForm(material),
                "PfP", "hPd", "STS",
                'S', new UnificationEntry(stickPrefix, material),
                'P', new UnificationEntry(OrePrefix.plate, material),
                'T', new UnificationEntry(OrePrefix.screw, material));
        }
    }



    public static void processLongStick(OrePrefix orePrefix, IngotMaterial material) {
        if (material.toolDurability <= 0) return;
        for (MetaValueItem batteryItem : batteryItems[2]) {
            ItemStack batteryStack = batteryItem.getStackForm();
            long maxCharge = batteryStack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null).getMaxCharge();
            ItemStack chargedHammerStack = MetaItems.JACKHAMMER.getMaxChargeOverrideStack(material, maxCharge);
            String recipeNameFirst = String.format("jack_hammer_%s_%s_full", batteryItem.unlocalizedName, material.toString());

            ModHandler.addShapedEnergyTransferRecipe(recipeNameFirst, chargedHammerStack,
                Ingredient.fromStacks(batteryStack), false,
                "SXd", "PRP", "MPB",
                'X', new UnificationEntry(OrePrefix.stickLong, material),
                'M', MetaItems.ELECTRIC_PISTON_HV.getStackForm(),
                'S', new UnificationEntry(OrePrefix.screw, Materials.Titanium),
                'P', new UnificationEntry(OrePrefix.plateDense, Materials.Titanium),
                'R', new UnificationEntry(OrePrefix.spring, Materials.Titanium),
                'B', batteryStack);
        }

        ItemStack drillStack = MetaItems.JACKHAMMER.getStackForm(material);
        ItemStack powerUnitStack = MetaItems.JACKHAMMER_BASE.getStackForm();
        String recipeNameSecond = String.format("jack_hammer_%s_unit", material);
        ModHandler.addShapedEnergyTransferRecipe(recipeNameSecond, drillStack,
            Ingredient.fromStacks(powerUnitStack), true,
            "wHd", " U ",
            'H', new UnificationEntry(orePrefix, material),
            'U', powerUnitStack);
    }
}
