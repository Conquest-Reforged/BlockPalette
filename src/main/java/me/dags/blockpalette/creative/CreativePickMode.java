package me.dags.blockpalette.creative;

import me.dags.blockpalette.palette.PaletteMain;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * @author dags <dags@dags.me>
 */
public abstract class CreativePickMode {

    final PaletteMain main;
    ItemStack stackUnderMouse = null;
    int mouseX = 0, mouseY = 0;
    private int width = 0, height = 0;

    CreativePickMode(PaletteMain main) {
        this.main = main;
    }

    public void onInitGui() {
        main.getRegistry().setupTabFilters();
    }

    public void onDrawScreen(GuiContainerCreative creative, int mouseX, int mouseY, Event event) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        if (main.getCurrentPalette().isActive()) {
            if (width != creative.width || height != creative.height) {
                width = creative.width;
                height = creative.height;
                main.getCurrentPalette().onResize(creative.mc, width, height);
            }
        }

        Slot slot = creative.getSlotUnderMouse();
        stackUnderMouse = slot != null ? slot.getStack() : null;

        drawScreen(event);
    }

    public void onMouseAction(Event event) {
        int button = Mouse.getEventButton();

        if (Mouse.getEventButtonState()) {
            pressMouse(event, button);
        } else if (button != -1) {
            releaseMouse(event, button);
        }
    }

    public void onKeyAction(Event event) {
        int key = Keyboard.getEventKey();
        if (Keyboard.getEventKeyState()) {
            pressKey(event, key);
        } else if (key != -1) {
            releaseKey(event, key);
        }
    }

    abstract void drawScreen(Event event);

    abstract void pressMouse(Event event, int button);

    abstract void releaseMouse(Event event, int button);

    abstract void pressKey(Event event, int keyCode);

    abstract void releaseKey(Event event, int keyCode);
}
