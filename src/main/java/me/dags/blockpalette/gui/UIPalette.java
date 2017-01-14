package me.dags.blockpalette.gui;

import me.dags.blockpalette.palette.PaletteMain;
import me.dags.blockpalette.util.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.CreativeCrafting;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class UIPalette {

    public static final UIPalette EMPTY = new UIPalette();

    private final PaletteMain mod;
    private final List<UIVariant> options = new ArrayList<>();
    private final int radius;
    private final UIConfig config;
    private final UIInventory inventory;

    private int centerX = 0;
    private int centerY = 0;
    private int increment = 1;
    private int currentRadius = 1;
    private boolean guiOpen = true;
    private boolean overlay = false;
    private long timer = 0L;

    private UIVariant underMouse = null;

    private UIPalette() {
        this.config = null;
        this.inventory = null;
        this.mod = null;
        this.radius = 0;
    }

    public UIPalette(PaletteMain mod, UIVariant parent, List<UIVariant> entries) {
        float spacing = (2F / (float) entries.size()) * (float) Math.PI;
        for (UIVariant entry : entries) {
            entry.setSpacing(spacing);
        }
        this.mod = mod;
        this.options.addAll(entries);
        this.options.add(parent);
        this.config = new UIConfig(mod);
        this.inventory = new UIInventory(this);
        this.radius = 25 + (3 * options.size()); // magic
        this.increment = Math.max(options.size() / 3, 1); // magic
    }

    private boolean animationTick() {
        long time = System.currentTimeMillis();
        if (time - timer > 25L) {
            timer = time;
            return true;
        }
        return false;
    }

    public ItemStack getParentStack() {
        for (UIVariant variant : options) {
            if (variant.isParent()) {
                return variant.getItemStack();
            }
        }
        return null;
    }

    public boolean isOverlay() {
        return overlay;
    }

    public boolean isPresent() {
        return this != EMPTY;
    }

    public boolean isActive() {
        return isPresent() && currentRadius > 0;
    }

    public void setOverlay(boolean overlay) {
        this.overlay = overlay;
    }

    public void setInactive() {
        currentRadius = 0;
    }

    public void draw() {
        draw(-999, -999);
    }

    UIVariant getVariantUnderMouse() {
        return underMouse;
    }

    public void draw(int mouseX, int mouseY) {
        if (!isPresent()) {
            return;
        }

        if (guiOpen) {
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;
            if (screen != null) {
                screen.drawDefaultBackground();
            }

            drawOpen(mouseX, mouseY);

            config.draw(mouseX, mouseY);

            inventory.draw(mouseX, mouseY);
        } else {
            drawClose(mouseX, mouseY);
        }
    }

    public void mouseClick(int mouseX, int mouseY, int button) {
        if (!isPresent()) {
            return;
        }
        if (currentRadius != radius) {
            return;
        }

        config.mouseClick(mouseX, mouseY, button);
        inventory.mouseClick(mouseX, mouseY, button);
    }

    public void mouseRelease(int mouseX, int mouseY, int button) {
        if (!isPresent()) {
            return;
        }
        if (currentRadius != radius) {
            return;
        }

        boolean lshift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);

        for (UIVariant entry : options) {
            if (entry.overSegment(mouseX, mouseY)) {
                if (lshift && button == 0) {
                    boolean selected = !entry.isSelected();
                    entry.setSelected(selected);
                } else if (button == 1) {
                    mod.newPalette(entry.getItemStack());
                }
            }
        }

        config.mouseRelease(mouseX, mouseY, button);
        inventory.mouseRelease(mouseX, mouseY, button);
    }

    public void keyTyped(char c, int keyCode) {
        inventory.keyTyped(c, keyCode);
    }

    public void onClose() {
        if (!isPresent()) {
            return;
        }

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        CreativeCrafting crafting = new CreativeCrafting(Minecraft.getMinecraft());
        player.inventoryContainer.addListener(crafting);

        guiOpen = false;
        config.onClose();
        inventory.onClose();

        if (player.capabilities.isCreativeMode) {
            for (UIVariant entry : options) {
                if (entry.isSelected()) {
                    player.inventory.addItemStackToInventory(entry.getItemStack());
                }
            }

            player.inventoryContainer.detectAndSendChanges();
        }

        player.inventoryContainer.removeListener(crafting);
    }

    public void onResize(Minecraft mc, int w, int h) {
        if (!isPresent()) {
            return;
        }

        config.onResize(mc, w, h);
        inventory.onResize(mc, w, h);

        expand();
    }

    private void drawOpen(int mouseX, int mouseY) {
        if (currentRadius < radius && animationTick()) {
            currentRadius = Math.min(radius, currentRadius + (increment * Config.animation_speed));
            expand();
        }
        drawOptions(mouseX, mouseY);
    }

    private void drawClose(int mouseX, int mouseY) {
        if (isActive()) {
            if (animationTick()) {
                currentRadius = Math.max(0, currentRadius - (increment * Config.animation_speed));
                expand();
            }
            drawOptions(mouseX, mouseY);
        }
    }

    private void drawOptions(int mouseX, int mouseY) {
        underMouse = null;
        boolean drawSegments = options.size() > 3;

        for (UIVariant option : options) {
            option.draw(mouseX, mouseY, drawSegments && Config.show_hue);

            if (underMouse == null && option.isVisible() && option.overSegment(mouseX, mouseY)) {
                underMouse = option;
                option.drawHighlighted();
            }

            if (option.isVisible() && option.isSelected()) {
                option.drawSelected();
            }
        }

        if (underMouse != null) {
            String text = underMouse.getDisplayText();
            int width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
            int x = centerX - (width / 2);
            int y = centerY + radius + 30;
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, x, y, 0xFFFFFF);
        }
    }

    private void expand() {
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        centerX = resolution.getScaledWidth() / 2;
        centerY = resolution.getScaledHeight() / 2;
        float progress = (float) currentRadius / (float) radius;

        for (int i = 0; i < options.size(); i++) {
            UIVariant option = options.get(i);
            option.setOrigin(centerX, centerY);
            option.updatePosition(i, options.size(), currentRadius, progress);
        }
    }
}
