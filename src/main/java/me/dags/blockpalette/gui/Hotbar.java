package me.dags.blockpalette.gui;

import me.dags.blockpalette.util.Pointer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.CreativeCrafting;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

/**
 * @author dags <dags@dags.me>
 */
public class Hotbar {

    private final Pointer<ItemStack> hovered;
    private final Pointer<ItemStack> selected;
    private final int padding = 4;
    private final int slotSize = 16;

    private int left = 0;
    private int top = 0;
    private int hoveredSlot = -1;

    public Hotbar(Pointer<ItemStack> hovered, Pointer<ItemStack> selected) {
        this.hovered = hovered;
        this.selected = selected;
    }

    public void init(int width, int height) {
        int barWidth = 9 * (slotSize + 4);
        left = (width / 2) - (barWidth / 2) + (padding / 2);
        top = height - slotSize - 3;
    }

    public void draw(int mouseX, int mouseY) {
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
        if (contains(mouseX, mouseY, left - 5, top - 5, left + (9 * slotSize) + 5, top + slotSize + 5)) {
            if (hoveredSlot != -1) {
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    setSlotStack(hoveredSlot, null);
                    return true;
                }

                ItemStack current = getSlotStack(hoveredSlot);

                if (selected.isPresent()) {
                    if (current != null && current.isItemEqual(selected.get())) {
                        ItemStack copy = selected.get().copy();
                        copy.stackSize += copy.stackSize;
                        setSlotStack(hoveredSlot, copy);
                        current = null;
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
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        CreativeCrafting crafting = new CreativeCrafting(Minecraft.getMinecraft());
        player.inventoryContainer.addListener(crafting);

        if (player.capabilities.isCreativeMode) {
            player.inventoryContainer.detectAndSendChanges();
        }

        player.inventoryContainer.removeListener(crafting);
    }

    private ItemStack getSlotStack(int index) {
        return Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(index);
    }

    private void setSlotStack(int index, ItemStack stack) {
        Minecraft.getMinecraft().thePlayer.inventory.setInventorySlotContents(index, stack);
    }

    private static boolean contains(int x, int y, int left, int top, int right, int bottom) {
        return x > left && x < right && y > top && y < bottom;
    }
}
