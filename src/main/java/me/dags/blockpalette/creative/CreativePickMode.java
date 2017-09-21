package me.dags.blockpalette.creative;

import me.dags.blockpalette.palette.PaletteMain;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * @author dags <dags@dags.me>
 */
@SideOnly(Side.CLIENT)
public abstract class CreativePickMode {

    final PaletteMain main;
    ItemStack stackUnderMouse = null;
    int mouseX = 0;
    int mouseY = 0;
    private int width = 0;
    private int height = 0;

    CreativePickMode(PaletteMain main) {
        this.main = main;
    }

    public void onInitGui() {}

    public void onDrawScreen(GuiContainerCreative creative, int mouseX, int mouseY, Event event) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;


        if (main.getPalette().isPresent()) {
            if (width != creative.width || height != creative.height) {
                width = creative.width;
                height = creative.height;
                main.getPalette().resize(width, height);
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
        char c = Keyboard.getEventCharacter();
        int key = Keyboard.getEventKey();
        if (Keyboard.getEventKeyState()) {
            pressKey(event, c, key);
        } else if (key != -1) {
            releaseKey(event, c, key);
        }
    }

    abstract void drawScreen(Event event);

    abstract void pressMouse(Event event, int button);

    abstract void releaseMouse(Event event, int button);

    abstract void pressKey(Event event, char c, int keyCode);

    abstract void releaseKey(Event event, char c, int keyCode);
}
