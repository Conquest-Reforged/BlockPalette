package me.dags.blockpalette.search;

import me.dags.blockpalette.util.Pointer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class Spotlight extends GuiScreen {

    private final Pointer<ItemStack> selected = Pointer.of(null);
    private final Pointer<ItemStack> hovered = Pointer.of(null);
    private final int width = 200;
    private final int slotSize = 24;
    private final Index<ItemStack> index;
    private final GuiTextField input;
    private final Hotbar2 hotbar;

    private List<ItemStack> display = Collections.emptyList();

    private int windowWidth = 0;
    private int displayLeft = 0;
    private int displayTop = 0;
    private int hoveredLeft = 0;
    private int hoveredTop = 0;

    public Spotlight() {
        List<ItemStack> stacks = new LinkedList<>();
        for (Item item : Item.REGISTRY) {
            item.getSubItems(item, CreativeTabs.SEARCH, stacks);
        }

        Index.Builder<ItemStack> builder = Index.builder();
        for (ItemStack stack : stacks) {
            String displayName = stack.getDisplayName();
            if (displayName != null) {
                builder.with(displayName, stack);
            }
        }

        this.fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
        this.hotbar = new Hotbar2(hovered, selected);
        this.index = builder.build();
        this.input = new GuiTextField(0, fontRendererObj, 0, 0, width, 20);
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        hotbar.init(width, height);
        input.xPosition = (width / 2) - (input.width / 2);
        input.yPosition = (height / 2) - (4 * input.height);
        windowWidth = width;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        hotbar.draw(mouseX, mouseY);
        input.setFocused(true);
        input.drawTextBox();
        drawGrid(mouseX, mouseY);
        drawSelected(mouseX, mouseY);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (hotbar.keyTyped(typedChar, keyCode)) {
            return;
        }

        input.textboxKeyTyped(typedChar, keyCode);
        display = index.search(input.getText(), 40);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (hotbar.mouseRelease(mouseX, mouseY)) {
            return;
        }

        if (selected.isPresent()) {
            selected.setNullable(null);
            return;
        }

        if (hovered.isPresent()) {
            selected.setNullable(hovered.get());
        }
    }

    @Override
    public void onGuiClosed() {
        hotbar.onClose();
    }


    private void drawGrid(int mouseX, int mouseY) {
        hovered.setNullable(null);
        displayLeft = input.xPosition;
        displayTop = input.yPosition + (input.height / 2) + 15;

        int columns = input.width / slotSize;
        int padding = (input.width - (slotSize * columns)) / 2;

        drawGridBackground(columns);
        drawSlots(mouseX, mouseY, columns, padding);
        drawHovered();
        drawTooltip(columns);
    }

    private void drawGridBackground(int columns) {
        if (!display.isEmpty()) {
            int rows = display.size() / (columns + 1);
            int height = (rows + 1) * slotSize;
            int left = displayLeft - 1;
            int top = displayTop - 1;
            int right = displayLeft + input.width + 1;
            int bottom = displayTop + height + 1;
            Gui.drawRect(left, top, right, bottom, 0X99000000);
        }
    }

    private void drawSlots(int mouseX, int mouseY, int columns, int padding) {
        int pos = 0;
        RenderHelper.enableGUIStandardItemLighting();
        for (ItemStack stack : display) {
            int col = pos % columns;
            int row = pos / columns;
            int x = displayLeft + padding + (slotSize * col);
            int y = displayTop + (slotSize * row);

            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, x + 4, y + 4);
            if (contains(mouseX, mouseY, x, y, x + slotSize, y + slotSize)) {
                hovered.setNullable(stack);
                hoveredLeft = x;
                hoveredTop = y;
            }

            pos++;
        }
        RenderHelper.disableStandardItemLighting();
    }

    private void drawHovered() {
        if (hovered.isPresent()) {
            int right = hoveredLeft + slotSize;
            int bottom = hoveredTop + slotSize;
            Gui.drawRect(hoveredLeft, hoveredTop, hoveredLeft + 1, bottom, 0xFFFFFFFF);
            Gui.drawRect(hoveredLeft, hoveredTop, right, hoveredTop + 1, 0xFFFFFFFF);
            Gui.drawRect(right, hoveredTop, right - 1, bottom, 0xFFFFFFFF);
            Gui.drawRect(hoveredLeft, bottom - 1, right, bottom, 0xFFFFFFFF);
        }
    }

    private void drawSelected(int mouseX, int mouseY) {
        if (selected.isPresent()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0F, 0F, 10F);
            int left = mouseX - (slotSize / 2) + 4;
            int top = mouseY - (slotSize / 2) + 4;
            RenderHelper.enableGUIStandardItemLighting();
            Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(selected.get(), left, top);
            Minecraft.getMinecraft().getRenderItem().renderItemOverlays(fontRendererObj, selected.get(), left, top);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
        }
    }

    private void drawTooltip(int columns) {
        if (hovered.isPresent()) {
            String text = hovered.get().getDisplayName();
            int left = (windowWidth / 2) - (fontRendererObj.getStringWidth(text) / 2);
            int rows = display.size() / columns;
            int height = (1 + rows) * slotSize;
            int top = displayTop + height + 10;
            fontRendererObj.drawStringWithShadow(text, left, top, 0xFFFFFF);
        }
    }

    private static boolean contains(int x, int y, int left, int top, int right, int bottom) {
        return x > left && x < right && y > top && y < bottom;
    }
}
