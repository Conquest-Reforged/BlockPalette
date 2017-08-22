package me.dags.blockpalette.gui;

import me.dags.blockpalette.util.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.CreativeCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

/**
 * @author dags <dags@dags.me>
 */
@SideOnly(Side.CLIENT)
public class Hotbar {

    private final Value<ItemStack> hovered;
    private final Value<ItemStack> selected;
    private final int padding = 4;
    private final int slotSize = 16;

    private int left = 0;
    private int top = 0;
    private int hoveredSlot = -1;

    public Hotbar(Value<ItemStack> hovered, Value<ItemStack> selected) {
        this.hovered = hovered;
        this.selected = selected;
    }

    public void init(int width, int height) {
        int barWidth = 9 * (slotSize + 4);
        left = (width / 2) - (barWidth / 2) + (padding / 2);
        top = height - slotSize - 3;
    }

    public void draw(int mouseX, int mouseY, float ticks) {
        hoveredSlot = -1;

        for (int i = 0; i < 9; i++) {
            int left = this.left + (i * (slotSize + padding));
            if (contains(mouseX, mouseY, left, top, left + slotSize, top + slotSize)) {
                hoveredSlot = i;
                Gui.drawRect(left, top, left + slotSize, top + slotSize, 0x22FFFFFF);
            }
        }
    }

    public boolean mouseRelease(int mouseX, int mouseY) {
        int left = this.left - 5;
        int top = this.top - 5;
        int right = this.left + (9 * (slotSize + 4));
        int bottom = this.top + slotSize + 4 + 5;


        if (contains(mouseX, mouseY, left, top, right, bottom)) {
            if (hoveredSlot != -1) {
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    setSlotStack(hoveredSlot, null);
                    return true;
                }

                ItemStack current = getSlotStack(hoveredSlot);

                if (selected.isPresent()) {
                    if (current != null && current.isItemEqual(selected.get())) {
                        ItemStack copy = selected.get().copy();
                        int total = copy.getCount() + current.getCount();
                        int count = Math.max(total, copy.getMaxStackSize());
                        int remaining = Math.min(0, current.getCount() - count);

                        copy.setCount(count);
                        setSlotStack(hoveredSlot, copy);

                        // TODO test!
                        if (remaining == 0) {
                            current = null;
                        } else {
                            current.setCount(remaining);
                        }
                    } else {
                        setSlotStack(hoveredSlot, selected.get().copy());
                    }
                } else {
                    setSlotStack(hoveredSlot, null);
                }

                selected.setNullable(current);
            }
            return true;
        }
        return false;
    }

    public boolean keyTyped(char typedChar, int keyCode) {
        if (hovered.isPresent()) {
            int index = typedChar - '0' - 1;
            if (index >= 0 && index <= 8) {
                setSlotStack(index, hovered.get());
                return true;
            }
        }
        return false;
    }

    public void onClose() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        CreativeCrafting crafting = new CreativeCrafting(Minecraft.getMinecraft());
        player.inventoryContainer.addListener(crafting);

        if (player.capabilities.isCreativeMode) {
            player.inventoryContainer.detectAndSendChanges();
        }

        player.inventoryContainer.removeListener(crafting);
    }

    private ItemStack getSlotStack(int index) {
        return Minecraft.getMinecraft().player.inventory.getStackInSlot(index);
    }

    private void setSlotStack(int index, ItemStack stack) {
        Minecraft.getMinecraft().player.inventory.setInventorySlotContents(index, stack);
    }

    private static boolean contains(int x, int y, int left, int top, int right, int bottom) {
        return x > left && x < right && y > top && y < bottom;
    }
}
