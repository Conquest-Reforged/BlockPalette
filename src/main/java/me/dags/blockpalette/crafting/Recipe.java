package me.dags.blockpalette.crafting;

import net.minecraft.item.ItemStack;

/**
 * @author dags <dags@dags.me>
 */
public class Recipe {

    private final ItemState input;
    private final ItemState output;

    Recipe(ItemState in, ItemState out) {
        this.input = in;
        this.output = out;
    }

    public ItemState getOutput() {
        return output;
    }

    public boolean test(ItemStack stack) {
        return input.matches(stack) && stack.getCount() >= input.getAmount();
    }

    public ItemStack apply(ItemStack input) {
        if (test(input)) {
            input.setCount(input.getCount() - this.input.getAmount());
            return output.getStack();
        }
        return ItemStack.EMPTY;
    }
}
