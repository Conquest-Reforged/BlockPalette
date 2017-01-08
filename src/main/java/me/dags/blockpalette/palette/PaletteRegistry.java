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

    private final Map<String, List<Mapping>> textureVariants = new HashMap<>();
    private final Map<String, Mapping> mappings = new HashMap<>();
    private final ColorWheel colorWheel = new ColorWheel();
    private final PaletteMain main;

    public PaletteRegistry(PaletteMain mod) {
        this.main = mod;
    }

    public void buildPalettes() {
        for (Block block : Block.REGISTRY) {
            Item item = Item.getItemFromBlock(block);
            if (item != null) {
                List<ItemStack> items = new ArrayList<>();
                block.getSubBlocks(item, CreativeTabs.SEARCH, items);
                for (ItemStack stack : items) {
                    register(stack);
                }
            }
        }
    }

    public void setupTabFilters() {
        for (CreativeTabs tab : CreativeTabs.CREATIVE_TAB_ARRAY) {
            // Don't filter the search or inventory tabs
            if (tab.getTabLabel().equals(CreativeTabs.SEARCH.getTabLabel()) || tab.getTabLabel().equals(CreativeTabs.INVENTORY.getTabLabel())) {
                continue;
            }
            if (Config.filter_variants) {
                if (!(tab instanceof FilteredCreativeTab)) {
                    // Tabs insert themselves in 'CREATIVE_TAB_ARRAY' on instantiation
                    new FilteredCreativeTab(this, tab);
                }
            } else if (tab instanceof FilteredCreativeTab) {
                // Unwrap the tab from the filter
                ((FilteredCreativeTab) tab).restore();
            }
        }
    }

    public UIPalette getPalette(ItemStack itemStack) {
        if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock)) {
            return UIPalette.EMPTY;
        }

        if (Config.match_textures) {
            return getVariantPalette(itemStack);
        }

        colorWheel.setAngle(Config.angle);
        colorWheel.setLeniency(Config.leniency);

        switch (Config.color_mode) {
            case COMPLIMENTARY:
                return getComplimentaryPalette(itemStack);
            case TRIAD:
                return getTriadPalette(itemStack);
            case TETRAD:
                return getTetradPalette(itemStack);
            default:
                return getAdjacentPalette(itemStack);
        }
    }

    private UIPalette newPalette(ItemStack stack, List<UIVariant> entries) {
        return new UIPalette(main, new UIVariant(stack, true), entries);
    }

    public UIPalette getVariantPalette(ItemStack stack) {
        return getVariantPalette(stack, Sets.newHashSet(new Mapping(stack)));
    }

    public UIPalette getVariantPalette(ItemStack stack, Set<Mapping> filter) {
        List<Mapping> variants = getMatchingTexture(stack);
        List<UIVariant> entries = statesToVariants(variants, filter);
        return newPalette(stack, entries);
    }

    public UIPalette getAdjacentPalette(ItemStack stack) {
        return getAdjacentPalette(stack, Sets.newHashSet(new Mapping(stack)));
    }

    public UIPalette getAdjacentPalette(ItemStack stack, Set<Mapping> filter) {
        Texture texture = getTextureForStack(stack);
        List<Texture> adjacent = colorWheel.getAdjacent(texture, Config.group_size);
        List<UIVariant> entries = texturesToVariants(adjacent, filter);
        return newPalette(stack, entries);
    }

    public UIPalette getComplimentaryPalette(ItemStack stack) {
        return getComplimentaryPalette(stack, Sets.newHashSet(new Mapping(stack)));
    }

    public UIPalette getComplimentaryPalette(ItemStack stack, Set<Mapping> filter) {
        Texture texture = getTextureForStack(stack);
        List<Texture> complimentary = colorWheel.getComplimentary(texture, Config.group_size);
        List<UIVariant> entries = texturesToVariants(complimentary, filter);
        return newPalette(stack, entries);
    }

    public UIPalette getTriadPalette(ItemStack stack) {
        return getTriadPalette(stack, Sets.newHashSet(new Mapping(stack)));
    }

    public UIPalette getTriadPalette(ItemStack stack, Set<Mapping> filter) {
        Texture texture = getTextureForStack(stack);
        List<Texture> triad = colorWheel.getTriad(texture, Config.group_size);
        List<UIVariant> entries = texturesToVariants(triad, filter);
        return newPalette(stack, entries);
    }

    public UIPalette getTetradPalette(ItemStack stack) {
        return getTetradPalette(stack, Sets.newHashSet(new Mapping(stack)));
    }

    public UIPalette getTetradPalette(ItemStack stack, Set<Mapping> filter) {
        Texture texture = getTextureForStack(stack);
        List<Texture> tetrad = colorWheel.getTetrad(texture, Config.group_size);
        List<UIVariant> entries = texturesToVariants(tetrad, filter);
        return newPalette(stack, entries);
    }

    public void filterItems(List<ItemStack> itemStacks) {
        Iterator<ItemStack> iterator = itemStacks.iterator();
        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();
            String texture = getParticleTex(stack);
            Mapping mapping = new Mapping(stack);
            if (mappings.containsKey(texture) && !mappings.containsValue(mapping)) {
                iterator.remove();
            }
        }
    }

    private List<Mapping> getMatchingTexture(ItemStack itemStack) {
        String texture = getItemModel(itemStack).getParticleTexture().getIconName();
        List<Mapping> states = textureVariants.get(texture);
        return states != null ? states : Collections.<Mapping>emptyList();
    }

    private List<UIVariant> statesToVariants(List<Mapping> variants, Set<Mapping> filter) {
        List<UIVariant> results = new ArrayList<>();
        for (Mapping variant : variants) {
            if (!filter.contains(variant)) {
                ItemStack itemStack = variant.itemStack.copy();
                UIVariant entry = new UIVariant(itemStack, false);
                results.add(entry);
            }
        }
        return results;
    }

    private List<UIVariant> texturesToVariants(List<Texture> textures, Set<Mapping> filter) {
        List<UIVariant> results = new ArrayList<>();
        for (Texture texture : textures) {
            Mapping variant = mappings.get(texture.name);
            if (variant != EMPTY_STATE && !filter.contains(variant)) {
                ItemStack itemStack = variant.itemStack.copy();
                UIVariant entry = new UIVariant(itemStack, false, texture.getColor());
                results.add(entry);
            }
        }
        return results;
    }

    private Texture getTextureForStack(ItemStack itemStack) {
        String icon = getItemModel(itemStack).getParticleTexture().getIconName();
        return colorWheel.getTexture(icon);
    }

    private void register(ItemStack stack) {
        IBakedModel model = getModel(stack);

        if (model == missingModel()) {
            return;
        }

        if (model.getParticleTexture().getIconName().equals("missingno")) {
            return;
        }

        String iconName = model.getParticleTexture().getIconName();
        Mapping mapping = new Mapping(stack);
        Mapping current = mappings.get(iconName);

        if (mapping.overrides(current)) {
            mappings.put(iconName, mapping);
        }

        List<Mapping> variants = textureVariants.get(iconName);
        if (variants == null) {
            textureVariants.put(iconName, variants = new ArrayList<>());
        }

        variants.add(mapping);

        if (colorWheel.hasTexture(iconName)) {
            return;
        }

        Texture texture = Texture.fromSprite(model.getParticleTexture());
        if (texture != null) {
            colorWheel.addTexture(texture);
        }
    }

    private static String getParticleTex(ItemStack itemStack) {
        return getModel(itemStack).getParticleTexture().getIconName();
    }

    private static IBakedModel getModel(ItemStack itemStack) {
        IBakedModel blockModel = getBlockModel(itemStack);
        IBakedModel itemModel = getItemModel(itemStack);
        if (blockModel == missingModel()) {
            return itemModel;
        }
        if (!blockModel.getParticleTexture().getIconName().equals(itemModel.getParticleTexture().getIconName())) {
            return itemModel;
        }
        return blockModel;
    }

    private static IBakedModel getBlockModel(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemBlock) {
            IBlockState state = ((ItemBlock) itemStack.getItem()).getBlock().getStateFromMeta(itemStack.getMetadata());
            return Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
        }
        return missingModel();
    }

    private static IBakedModel getItemModel(ItemStack itemStack) {
        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(itemStack);
        return model != null && model.getParticleTexture() != null ? model : missingModel();
    }

    private static IBakedModel missingModel() {
        return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager().getMissingModel();
    }

    private static class Mapping {

        private final ItemStack itemStack;
        private final boolean mainTab;
        private final boolean isFullBlock;
        private final int hashCode;

        private Mapping(ItemStack stack) {
            this.itemStack = stack;
            this.hashCode = itemStack.getItem().getUnlocalizedName(itemStack).hashCode();
            this.mainTab = itemStack.getItem().getCreativeTab() == CreativeTabs.BUILDING_BLOCKS;
            if (stack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock) stack.getItem()).getBlock();
                this.isFullBlock = block.getDefaultState().isFullBlock();
            } else {
                this.isFullBlock = false;
            }
        }

        private boolean overrides(Mapping other) {
            return other == null || (!other.mainTab && this.mainTab) || (!other.isFullBlock && this.isFullBlock);
        }

        @Override
        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }
            if (other instanceof ItemStack) {
                ItemStack stack = (ItemStack) other;
                return stack.getItem().getUnlocalizedName(stack).hashCode() == hashCode();
            }
            return other.hashCode() == this.hashCode();
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }
    }
}
