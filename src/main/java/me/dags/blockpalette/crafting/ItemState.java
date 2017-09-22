package me.dags.blockpalette.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author dags <dags@dags.me>
 */
public class ItemState {

    public static final ItemState EMPTY = new ItemState(Items.AIR, 0, 0);

    private final Item item;
    private final int metadata;
    private final int amount;

    private ItemState(Item item, int metadata, int amount) {
        this.item = item;
        this.metadata = metadata;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isPresent() {
        return this != EMPTY;
    }

    public ItemStack getStack() {
        return isPresent() && amount > 0 ? new ItemStack(item, amount, metadata) : ItemStack.EMPTY;
    }

    public ItemState add(Item item, int meta, int amount) {
        if (isPresent()) {
            return of(this.item, this.metadata, this.amount + amount);
        }
        return of(item, meta, amount);
    }

    public ItemState add(ItemState other) {
        return other.isPresent() ? add(other.item, other.metadata, other.amount) : this;
    }

    public ItemState add(ItemStack stack) {
        return add(stack.getItem(), stack.getMetadata(), stack.getCount());
    }

    public boolean matches(ItemStack stack) {
        return matches(stack.getItem(), stack.getMetadata());
    }

    public boolean matches(Item item, int meta) {
        return this.item == item && this.metadata == meta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemState itemState = (ItemState) o;

        if (metadata != itemState.metadata) return false;
        return item != null ? item.equals(itemState.item) : itemState.item == null;

    }

    @Override
    public int hashCode() {
        int result = item != null ? item.hashCode() : 0;
        result = 31 * result + metadata;
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s[meta=%s]", item.getRegistryName(), metadata);
    }

    public static ItemState of(ItemStack stack) {
        return of(stack.getItem(), stack.getMetadata(), stack.getCount());
    }

    public static ItemState of(Item item, int meta, int amount) {
        return item == Items.AIR ? EMPTY : new ItemState(item, meta, amount);
    }
}
