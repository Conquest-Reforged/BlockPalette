package me.dags.blockpalette.search;

import me.dags.blockpalette.color.ColorConst;
import me.dags.blockpalette.color.ColorF;
import me.dags.blockpalette.gui.Hotbar;
import me.dags.blockpalette.palette.PaletteMain;
import me.dags.blockpalette.util.Value;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
@SideOnly(Side.CLIENT)
public class SearchScreen extends GuiScreen {

    private final Value<ItemStack> selected = Value.of(null);
    private final Value<ItemStack> hovered = Value.of(null);
    private final ColorF emptyColor = new ColorF(0.05F, 0.05F, 0.05F);
    private final int width = 200;
    private final int slotSize = 24;
    private final Index<ItemStack> index;
    private final GuiTextField input;
    private final Hotbar hotbar;
    private final PaletteMain main;

    private List<ItemStack> display = Collections.emptyList();

    private int windowWidth = 0;
    private int displayLeft = 0;
    private int displayTop = 0;
    private int hoveredLeft = 0;
    private int hoveredTop = 0;

    public SearchScreen(PaletteMain main) {
        List<ItemStack> stacks = new LinkedList<>();
        for (Block block : Block.REGISTRY) {
            Item item = Item.getItemFromBlock(block);
            block.getSubBlocks(item, CreativeTabs.SEARCH, stacks);
        }

        for (Item item : Item.REGISTRY) {
            if (item instanceof ItemBlock) {
                continue;
            }
            item.getSubItems(item, CreativeTabs.SEARCH, stacks);
        }

        Index.Builder<ItemStack> builder = Index.builder();
        for (ItemStack stack : stacks) {
            if (stack != null && stack.getItem() != null) {
                int id = 31 * stack.getItem().hashCode() + stack.getMetadata();
                String name = stack.getDisplayName();
                List<Tag> tags = getTags(main, stack);
                builder.with(stack, id, name, tags);
            }
        }

        this.main = main;
        this.fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
        this.hotbar = new Hotbar(hovered, selected);
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
        hotbar.draw(mouseX, mouseY, partialTicks);
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

        String current = input.getText();
        input.textboxKeyTyped(typedChar, keyCode);

        if (!current.equals(input.getText())) {
            display = index.search(input.getText(), 40);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (hotbar.mouseRelease(mouseX, mouseY)) {
            return;
        }

        ItemStack select = selected.get();
        ItemStack hover = hovered.get();

        if (select != null) {
            selected.setNullable(null);
            return;
        }

        if (hover != null) {
            selected.setNullable(hover);
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
            int rows = (int) Math.ceil(display.size() / (double) columns);
            int height = rows * slotSize;
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

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 250);
        GlStateManager.enableDepth();

        for (ItemStack stack : display) {
            int col = pos % columns;
            int row = pos / columns;
            int x = displayLeft + padding + (slotSize * col);
            int y = displayTop + (slotSize * row);

            ColorF color = main.getRegistry().getColor(stack, emptyColor);
            drawRect(x + 1, y + 1, x + slotSize - 1, y + slotSize - 1, color.toARGB(0.25F));

            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, x + 4, y + 4);
            if (contains(mouseX, mouseY, x, y, x + slotSize, y + slotSize)) {
                hovered.setNullable(stack);
                hoveredLeft = x;
                hoveredTop = y;
            }

            pos++;
        }
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();

        RenderHelper.disableStandardItemLighting();
    }

    private void drawHovered() {
        if (hovered.get() != null) {
            int right = hoveredLeft + slotSize;
            int bottom = hoveredTop + slotSize;
            Gui.drawRect(hoveredLeft, hoveredTop, hoveredLeft + 1, bottom, 0xFFFFFFFF);
            Gui.drawRect(hoveredLeft, hoveredTop, right, hoveredTop + 1, 0xFFFFFFFF);
            Gui.drawRect(right, hoveredTop, right - 1, bottom, 0xFFFFFFFF);
            Gui.drawRect(hoveredLeft, bottom - 1, right, bottom, 0xFFFFFFFF);
        }
    }

    private void drawSelected(int mouseX, int mouseY) {
        ItemStack select = selected.get();
        if (select != null) {
            GlStateManager.pushMatrix();
            GlStateManager.enableDepth();
            GlStateManager.translate(0F, 0F, 300F);
            int left = mouseX - (slotSize / 2) + 4;
            int top = mouseY - (slotSize / 2) + 4;
            RenderHelper.enableGUIStandardItemLighting();
            Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(select, left, top);
            Minecraft.getMinecraft().getRenderItem().renderItemOverlays(fontRendererObj, select, left, top);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableDepth();
            GlStateManager.popMatrix();
        }
    }

    private void drawTooltip(int columns) {
        ItemStack hover = hovered.get();
        if (hover != null) {
            String text = hover.getDisplayName();
            int left = (windowWidth / 2) - (fontRendererObj.getStringWidth(text) / 2);
            int rows = display.size() / columns;
            int height = (1 + rows) * slotSize;
            int top = displayTop + height + 10;
            fontRendererObj.drawStringWithShadow(text, left, top, 0xFFFFFF);
        }
    }

    private List<Tag> getTags(PaletteMain main, ItemStack stack) {
        List<Tag> tags = new LinkedList<>();
        String name = stack.getDisplayName().toLowerCase();
        for (Tag tag : Tag.TAGS) {
            if (tag.test(name)) {
                tags.add(tag);
            }
        }

        Block block = Block.getBlockFromItem(stack.getItem());
        if (block != null) {
            if (block.getDefaultState().isBlockNormalCube()
                    || block.getDefaultState().isFullBlock()
                    || block.getDefaultState().isNormalCube()
                    || block.getDefaultState().isOpaqueCube()) {
                tags.add(Tag.of("block"));
            }
        } else {
            tags.add(Tag.of("item"));
        }

        ColorF color = main.getRegistry().getColor(stack, ColorF.EMPTY);
        if (color != ColorF.EMPTY) {
            ColorConst colorConst = ColorConst.nearest(color);
            tags.add(Tag.of(colorConst.name()));
        }

        return tags;
    }

    private static boolean contains(int x, int y, int left, int top, int right, int bottom) {
        return x > left && x < right && y > top && y < bottom;
    }
}
