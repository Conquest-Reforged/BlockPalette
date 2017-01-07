package me.dags.blockpalette.palette;

import com.google.common.collect.Sets;
import me.dags.blockpalette.color.ColorWheel;
import me.dags.blockpalette.color.Texture;
import me.dags.blockpalette.gui.UIPalette;
import me.dags.blockpalette.gui.UIVariant;
import me.dags.blockpalette.util.Config;
import me.dags.blockpalette.util.FilteredCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.*;

/**
 * @author dags <dags@dags.me>
 */
public class PaletteRegistry {

    private static final IBlockState EMPTY_STATE = Blocks.AIR.getDefaultState();

    private final Map<String, List<IBlockState>> textureVariants = new HashMap<>();
    private final Map<String, IBlockState> mappings = new HashMap<>();
    private final ColorWheel colorWheel = new ColorWheel();
    private final PaletteMain main;

    public PaletteRegistry(PaletteMain mod) {
        this.main = mod;
    }

    public void buildPalettes() {
        for (Block block : Block.REGISTRY) {
            Set<Integer> visited = new HashSet<>();
            for (IBlockState variant : block.getBlockState().getValidStates()) {
                if (visited.add(block.getMetaFromState(variant))) {
                    register(variant);
                }
            }
        }
    }

    public void setupTabFilters() {
        for (CreativeTabs tab : CreativeTabs.CREATIVE_TAB_ARRAY) {
            if (Config.filter_variants) {
                if (!(tab instanceof FilteredCreativeTab)) {
                    new FilteredCreativeTab(this, tab);
                }
            } else if (tab instanceof FilteredCreativeTab) {
                ((FilteredCreativeTab) tab).restore();
            }
        }
    }

    public UIPalette getPalette(ItemStack itemStack) {
        if (itemStack == null) {
            return UIPalette.EMPTY;
        }

        IBlockState state = stateFromItem(itemStack);

        if (state == EMPTY_STATE) {
            return UIPalette.EMPTY;
        }

        if (Config.match_textures) {
            return getVariantPalette(state);
        }

        colorWheel.setAngle(Config.angle);
        colorWheel.setLeniency(Config.leniency);

        switch (Config.color_mode) {
            case COMPLIMENTARY:
                return getComplimentaryPalette(state);
            case TRIAD:
                return getTriadPalette(state);
            case TETRAD:
                return getTetradPalette(state);
            default:
                return getAdjacentPalette(state);
        }
    }

    private UIPalette newPalette(IBlockState state, List<UIVariant> entries) {
        return new UIPalette(main, new UIVariant(stackFromState(state), true), entries);
    }

    public UIPalette getVariantPalette(IBlockState state) {
        return getVariantPalette(state, Sets.newHashSet(state));
    }

    public UIPalette getVariantPalette(IBlockState state, Set<IBlockState> filter) {
        List<IBlockState> variants = getMatchingTexture(state);
        List<UIVariant> entries = statesToVariants(variants, filter);
        return newPalette(state, entries);
    }

    public UIPalette getAdjacentPalette(IBlockState state) {
        return getAdjacentPalette(state, Sets.newHashSet(state));
    }

    public UIPalette getAdjacentPalette(IBlockState state, Set<IBlockState> filter) {
        Texture texture = getTextureForState(state);
        List<Texture> adjacent = colorWheel.getAdjacent(texture, Config.group_size);
        List<UIVariant> entries = texturesToVariants(adjacent, filter);
        return newPalette(state, entries);
    }

    public UIPalette getComplimentaryPalette(IBlockState state) {
        return getComplimentaryPalette(state, Sets.newHashSet(state));
    }

    public UIPalette getComplimentaryPalette(IBlockState state, Set<IBlockState> filter) {
        Texture texture = getTextureForState(state);
        List<Texture> complimentary = colorWheel.getComplimentary(texture, Config.group_size);
        List<UIVariant> entries = texturesToVariants(complimentary, filter);
        return newPalette(state, entries);
    }

    public UIPalette getTriadPalette(IBlockState state) {
        return getTriadPalette(state, Sets.newHashSet(state));
    }

    public UIPalette getTriadPalette(IBlockState state, Set<IBlockState> filter) {
        Texture texture = getTextureForState(state);
        List<Texture> triad = colorWheel.getTriad(texture, Config.group_size);
        List<UIVariant> entries = texturesToVariants(triad, filter);
        return newPalette(state, entries);
    }

    public UIPalette getTetradPalette(IBlockState state) {
        return getTetradPalette(state, Sets.newHashSet(state));
    }

    public UIPalette getTetradPalette(IBlockState state, Set<IBlockState> filter) {
        Texture texture = getTextureForState(state);
        List<Texture> tetrad = colorWheel.getTetrad(texture, Config.group_size);
        List<UIVariant> entries = texturesToVariants(tetrad, filter);
        return newPalette(state, entries);
    }

    public void filterItems(List<ItemStack> itemStacks) {
        Iterator<ItemStack> iterator = itemStacks.iterator();
        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();
            IBlockState state = stateFromItem(stack);
            if (state != EMPTY_STATE) {
                String texture = getModel(state).getParticleTexture().getIconName();
                if (mappings.containsKey(texture) && !mappings.containsValue(state)) {
                    iterator.remove();
                }
            }
        }
    }

    private Texture getTextureForState(IBlockState state) {
        String texture = getModel(state).getParticleTexture().getIconName();
        return colorWheel.getTexture(texture);
    }

    private List<IBlockState> getMatchingTexture(IBlockState blockState) {
        String texture = getModel(blockState).getParticleTexture().getIconName();
        List<IBlockState> states = textureVariants.get(texture);
        return states != null ? states : Collections.<IBlockState>emptyList();
    }

    private List<UIVariant> statesToVariants(List<IBlockState> variants, Set<IBlockState> filter) {
        List<UIVariant> results = new ArrayList<>();
        for (IBlockState variant : variants) {
            if (variant != EMPTY_STATE && !filter.contains(variant)) {
                ItemStack itemStack = stackFromState(variant);
                UIVariant entry = new UIVariant(itemStack, false);
                results.add(entry);
            }
        }
        return results;
    }

    private List<UIVariant> texturesToVariants(List<Texture> textures, Set<IBlockState> filter) {
        List<UIVariant> results = new ArrayList<>();
        for (Texture texture : textures) {
            IBlockState variant = mappings.get(texture.name);
            if (variant != EMPTY_STATE && !filter.contains(variant)) {
                ItemStack itemStack = stackFromState(variant);
                UIVariant entry = new UIVariant(itemStack, false, texture.getColor());
                results.add(entry);
            }
        }
        return results;
    }

    private void register(IBlockState state) {
        if (!hasValidItem(state)) {
            return;
        }

        IBakedModel model = getModel(state);
        String iconName = model.getParticleTexture().getIconName();

        IBlockState current = mappings.get(iconName);
        if (overridesMapping(state, current)) {
            mappings.put(iconName, state);
        }

        List<IBlockState> variants = textureVariants.get(iconName);
        if (variants == null) {
            textureVariants.put(iconName, variants = new ArrayList<>());
        }

        variants.add(state);

        if (colorWheel.hasTexture(iconName)) {
            return;
        }

        Texture texture = Texture.fromSprite(model.getParticleTexture());
        if (texture != null) {
            colorWheel.addTexture(texture);
        }
    }

    private static boolean overridesMapping(IBlockState in, IBlockState current) {
        // No mapping exists, so use the 'in' blocksstate
        if (current == null) {
            return true;
        }

        // Prioritise blockstates that appear in the build blocks tab
        // Then prioritise full blocks over other shapes
        CreativeTabs tabIn = in.getBlock().getCreativeTabToDisplayOn();
        CreativeTabs tabCurr = current.getBlock().getCreativeTabToDisplayOn();
        return tabCurr != CreativeTabs.BUILDING_BLOCKS && tabIn == CreativeTabs.BUILDING_BLOCKS || !current.isFullBlock() && in.isFullBlock();
    }

    private static boolean hasValidItem(IBlockState blockState) {
        if (Item.getItemFromBlock(blockState.getBlock()) == null) {
            return false;
        }
        ItemStack stack = stackFromState(blockState);
        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
        return !isMissingModel(model);
    }

    private static boolean isMissingModel(IBakedModel model) {
        IBakedModel missing = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
        return model == missing;
    }

    private static IBakedModel getModel(IBlockState blockState) {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(blockState);
    }

    private static ItemStack stackFromState(IBlockState blockState) {
        return new ItemStack(blockState.getBlock(), 1, blockState.getBlock().getMetaFromState(blockState));
    }

    private static IBlockState stateFromItem(ItemStack item) {
        if (item.getItem() instanceof ItemBlock) {
            Block block = Block.getBlockFromItem(item.getItem());
            int meta = item.getMetadata();
            return block.getStateFromMeta(meta);
        }
        return EMPTY_STATE;
    }
}
