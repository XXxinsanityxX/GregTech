package gregtech.common;

import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.ResourceUtils;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.realmsclient.gui.ChatFormatting;
import gregtech.api.GTValues;
import gregtech.api.render.MetaTileEntityRenderer;
import gregtech.api.render.ToolRenderHandler;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.api.util.GTLog;
import gregtech.api.util.ModCompatibility;
import gregtech.common.blocks.*;
import gregtech.common.covers.facade.FacadeRenderer;
import gregtech.common.items.MetaItems;
import gregtech.common.render.CableRenderer;
import gregtech.common.render.FluidPipeRenderer;
import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    private static final ResourceLocation GREGTECH_CAPE_TEXTURE = new ResourceLocation(GTValues.MODID, "textures/gregtechcape.png");

    public static final IBlockColor COMPRESSED_BLOCK_COLOR = (IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) ->
        state.getValue(((BlockCompressed) state.getBlock()).variantProperty).materialRGB;

    public static final IItemColor COMPRESSED_ITEM_COLOR = (stack, tintIndex) -> {
        BlockCompressed block = (BlockCompressed) ((ItemBlock) stack.getItem()).getBlock();
        IBlockState state = block.getStateFromMeta(stack.getItemDamage());
        return state.getValue(block.variantProperty).materialRGB;
    };

    public static final IBlockColor FRAME_BLOCK_COLOR = (IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) -> {
        Material material = ((BlockFrame) state.getBlock()).frameMaterial;
        EnumDyeColor dyeColor = state.getValue(BlockColored.COLOR);
        return dyeColor == EnumDyeColor.WHITE ? material.materialRGB : dyeColor.colorValue;
    };

    public static final IItemColor FRAME_ITEM_COLOR = (stack, tintIndex) -> {
        IBlockState frameState = ((FrameItemBlock) stack.getItem()).getBlockState(stack);
        BlockFrame block = (BlockFrame) frameState.getBlock();
        EnumDyeColor dyeColor = frameState.getValue(BlockColored.COLOR);
        return dyeColor == EnumDyeColor.WHITE ? block.frameMaterial.materialRGB : dyeColor.colorValue;
    };

    public static final IBlockColor ORE_BLOCK_COLOR = (IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) ->
        tintIndex == 1 ? ((BlockOre) state.getBlock()).material.materialRGB : 0xFFFFFF;

    public static final IItemColor ORE_ITEM_COLOR = (stack, tintIndex) ->
        tintIndex == 1 ? ((BlockOre) ((ItemBlock) stack.getItem()).getBlock()).material.materialRGB : 0xFFFFFF;

    public static final IBlockColor FOAM_BLOCK_COLOR = (IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) ->
        state.getValue(BlockColored.COLOR).colorValue;

    public void onPreLoad() {
        super.onPreLoad();
        MetaTileEntityRenderer.preInit();
        CableRenderer.preInit();
        FluidPipeRenderer.preInit();
        MetaEntities.initRenderers();
        TextureUtils.addIconRegister(MetaFluids::registerSprites);
        MinecraftForge.EVENT_BUS.register(ToolRenderHandler.INSTANCE);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        registerColors();
    }

    @Override
    public void onPostLoad() {
        super.onPostLoad();
        ResourceUtils.registerReloadListener(ToolRenderHandler.INSTANCE);
        ModCompatibility.initCompat();
        FacadeRenderer.init();
        startCapeLoadingThread();
    }

    public void registerColors() {
        MetaBlocks.registerColors();
        MetaItems.registerColors();
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        MetaBlocks.registerStateMappers();
        MetaBlocks.registerItemModels();
        MetaItems.registerModels();
    }

    @SubscribeEvent
    public static void addMaterialFormulaHandler(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (!(itemStack.getItem() instanceof ItemBlock)) {
            UnificationEntry unificationEntry = OreDictUnifier.getUnificationEntry(itemStack);
            if (unificationEntry != null && unificationEntry.material != null) {
                String formula = unificationEntry.material.chemicalFormula;
                if (formula != null && !formula.isEmpty() && !formula.equals("?")) {
                    event.getToolTip().add(1, ChatFormatting.GRAY.toString() + unificationEntry.material.chemicalFormula);
                }
            }
        }
    }

    private static final Set<UUID> capeHoldersUUIDs = new HashSet<>();

    private static void startCapeLoadingThread() {
        Thread capeListLoadThread = new Thread(ClientProxy::loadCapesList, "GregTech Cape List Downloader");
        capeListLoadThread.setDaemon(true);
        capeListLoadThread.start();
    }

    private static void loadCapesList() {
        capeHoldersUUIDs.add(UUID.fromString("4bdba267-1479-449a-8ae4-d1957dd39f29"));
        capeHoldersUUIDs.add(UUID.fromString("6cb05251-cd1b-481e-bf59-07637add1c64"));
        try {
            URL connectURL = new URL("https://www.dropbox.com/s/zc07k4y1h4ftmz3/GregTechPatreonList.txt?dl=1");
            HttpURLConnection connection = (HttpURLConnection) connectURL.openConnection(Minecraft.getMinecraft().getProxy());
            try {
                connection.setDoInput(true);
                connection.setDoOutput(false);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                capeHoldersUUIDs.addAll(retrieveCapeUUIDs(inputStream));
            } finally {
                connection.disconnect();
            }
        } catch (UnknownHostException |
            SocketTimeoutException |
            MalformedURLException ignored) {
        } catch (IOException exception) {
            GTLog.logger.warn("Failed to fetch cape list", exception);
        }
    }

    private static Set<UUID> retrieveCapeUUIDs(InputStream inputStream) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        Set<UUID> result = new HashSet<>();
        for (; ; ) {
            String line = reader.readLine();
            if (line == null)
                break;
            int firstCommentIndex = line.indexOf('#');
            if (firstCommentIndex > -1) {
                line = line.substring(0, firstCommentIndex);
            }
            try {
                UUID playerUUID = UUID.fromString(line.trim());
                result.add(playerUUID);
            } catch (IllegalArgumentException exception) {
                GTLog.logger.warn("Failed to parse cape player UUID {}", line.trim(), exception);
            }
        }
        return result;
    }

    @SubscribeEvent
    public static void onPlayerRender(RenderPlayerEvent.Pre event) {
        AbstractClientPlayer clientPlayer = (AbstractClientPlayer) event.getEntityPlayer();
        if (capeHoldersUUIDs.contains(clientPlayer.getUniqueID()) && clientPlayer.hasPlayerInfo() && clientPlayer.getLocationCape() == null) {
            NetworkPlayerInfo playerInfo = ObfuscationReflectionHelper.getPrivateValue(AbstractClientPlayer.class, clientPlayer, 0);
            Map<Type, ResourceLocation> playerTextures = ObfuscationReflectionHelper.getPrivateValue(NetworkPlayerInfo.class, playerInfo, 1);
            playerTextures.put(Type.CAPE, GREGTECH_CAPE_TEXTURE);
        }
    }

}