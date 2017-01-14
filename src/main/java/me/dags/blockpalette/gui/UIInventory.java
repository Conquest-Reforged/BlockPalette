package me.dags.blockpalette.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

/**
 * @author dags <dags@dags.me>
 */
public class UIInventory {

    protected static final ResourceLocation WIDGETS_TEX_PATH = new ResourceLocation("textures/gui/widgets.png");

    private final UIPalette palette;
    private final Slot[] slots = new Slot[9];

    private ItemStack selected = null;
    private int left = 0, top = 0;

    public UIInventory(UIPalette palette) {
        this.palette = palette;

        for (int i = 0 ; i < 9; i++) {
            slots[i] = new Slot();
        }

        initSlots(new ScaledResolution(Minecraft.getMinecraft()));
    }

    private void initSlots(ScaledResolution resolution) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        int slotWidth = 20, slotHeight = 20;
        int hotbarLength = (9 * slotWidth) + 2;
        int hCenter = resolution.getScaledWidth() / 2;

        left = hCenter - (hotbarLength / 2);
        top = resolution.getScaledHeight() - slotHeight - 2;

        for (int i = 0; i < 9; i++) {
            Slot slot = slots[i];
            ItemStack stack = player.inventory.getStackInSlot(i);
            // 1px border around hotbar
            slot.x = 1 + left + (i * slotWidth);
            slot.y = 1 + top;
            slot.stack = stack != null ? stack.copy() : null;
        }
    }

    public void draw(int mouseX, int mouseY) {
        // hotbar
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableBlend();
        GlStateManager.enableRescaleNormal();
        Minecraft.getMinecraft().getTextureManager().bindTexture(WIDGETS_TEX_PATH);
        net.minecraft.client.gui.Gui.drawModalRectWithCustomSizedTexture(left, top, 0, 0, 182, 22, 256, 256);

        // items
        RenderHelper.enableGUIStandardItemLighting();

        // draw slot items
        for (int i = 0; i < 9; i++) {
            Slot slot = slots[i];
            drawItem(slot.stack, slot.x + 2, slot.y + 2);
        }

        // draw selected item
        drawItem(selected, mouseX - 8, mouseY - 8);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
    }

    private void drawItem(ItemStack itemStack, int x, int y) {
        if (itemStack != null) {
            GlStateManager.translate(0.0F, 0.0F, 32.0F);
            Minecraft.getMinecraft().getRenderItem().zLevel = 200F;
            Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(itemStack, x, y);
            Minecraft.getMinecraft().getRenderItem().zLevel = 0F;
        }
    }

    public void mouseClick(int mouseX, int mouseY, int btn) {}

    public void mouseRelease(int mouseX, int mouseY, int btn) {
        UIVariant variant = palette.getVariantUnderMouse();
        boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);

        if (variant == null) {
            for (int i = 0; i < 9; i++) {
                Slot slot = slots[i];

                if (slot.mouseOver(mouseX, mouseY)) {
                    if (shift) {
                        slot.stack = null;
                        return;
                    } else {
                        ItemStack stack = slot.stack;
                        slot.stack = selected;
                        selected = stack;
                        return;
                    }
                }
            }

            selected = null;

        } else if (!shift) {
            selected = variant.getItemStack().copy();
            variant.setSelected(false);
        }
    }

    public void keyTyped(char c, int keyCode) {
        UIVariant variant = palette.getVariantUnderMouse();
        if (variant != null) {
            // char typed 1 to 9 -> translate to int value and subtract 1 so in range 0 to 8
            int id = (c - '0') - 1;
            if (id >= 0 && id < slots.length) {
                slots[id].stack = variant.getItemStack().copy();
            }
        }
    }

    public void onResize(Minecraft mc, int w, int h) {
        initSlots(new ScaledResolution(mc));
    }

    public void onClose() {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        if (!player.capabilities.isCreativeMode) {
            return;
        }

        // Update slots
        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = slots[i].stack;
            player.inventory.setInventorySlotContents(i, stack);
        }
    }

    private static class Slot {

        private static final int SLOT_SIZE = 20;

        private int x, y;
        private ItemStack stack;

        private boolean mouseOver(int mx, int my) {
            return mx >= x && mx <= x + SLOT_SIZE && my >= y && my <= y + SLOT_SIZE;
        }

        @Override
        public String toString() {
            return x + ":" + y;
        }
    }
}
