package me.dags.blockpalette.palette;

import me.dags.blockpalette.gui.GuiScreenPalette;
import me.dags.blockpalette.gui.UIPalette;
import me.dags.blockpalette.util.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

import java.io.File;

public class PaletteMain implements IResourceManagerReloadListener {

    public final KeyBinding show = new KeyBinding("key.blockpalette.open", Keyboard.getKeyIndex("C"), "Block Palette");

    private PaletteRegistry registry = new PaletteRegistry(this);
    private UIPalette currentPalette = UIPalette.EMPTY;

    public PaletteRegistry getRegistry() {
        return registry;
    }

    public UIPalette getCurrentPalette() {
        return currentPalette;
    }

    public void newPalette() {
        newPalette(currentPalette.getParentStack());
    }

    public void newPalette(ItemStack itemStack) {
        currentPalette = registry.getPalette(itemStack);
    }

    public void showPaletteScreen() {
        if (currentPalette.isPresent()) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiScreenPalette(this));
        }
    }

    public boolean isInventoryKey(int keyCode) {
        return keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode();
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        registry = new PaletteRegistry(this);
        registry.buildPalettes();
        currentPalette = UIPalette.EMPTY;
    }

    public void onPreInit(File config) {
        Config.init(config);
    }

    public void onInit() {
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
    }

    public void onTick() {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer != null && minecraft.currentScreen == null && Minecraft.isGuiEnabled()) {
            if (show.isPressed() && !currentPalette.isActive()) {
                newPalette(minecraft.thePlayer.getHeldItemMainhand());
                showPaletteScreen();
            }

            if (currentPalette.isActive()) {
                currentPalette.draw();
            }
        }
    }
}
