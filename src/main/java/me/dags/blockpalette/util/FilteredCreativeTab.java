package me.dags.blockpalette.util;

import me.dags.blockpalette.palette.PaletteRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class FilteredCreativeTab extends CreativeTabs {

    private final CreativeTabs wrapped;
    private final PaletteRegistry paletteRegistry;

    public FilteredCreativeTab(PaletteRegistry paletteRegistry, CreativeTabs wrapped) {
        super(wrapped.getTabIndex(), wrapped.getTabLabel());
        this.paletteRegistry = paletteRegistry;
        this.wrapped = wrapped;
    }

    @Override
    public void displayAllRelevantItems(List<ItemStack> items) {
        // Get all the items from the wrapped CreativeTab
        wrapped.displayAllRelevantItems(items);

        // Remove items if they aren't mapped as the 'main' block for their particle texture
        paletteRegistry.filterItems(items);
    }

    @Override
    public Item getTabIconItem() {
        return wrapped.getTabIconItem();
    }

    public void restore() {
        CREATIVE_TAB_ARRAY[wrapped.getTabIndex()] = wrapped;
    }
}
