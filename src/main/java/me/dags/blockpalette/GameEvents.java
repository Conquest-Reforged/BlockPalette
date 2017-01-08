package me.dags.blockpalette;

import me.dags.blockpalette.palette.PaletteMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * @author dags <dags@dags.me>
 */
public class GameEvents {

    private final PaletteMain main;

    private boolean inCreativeInventory = false;
    private int mouseX = 0, mouseY = 0;
    private int width = 0, height = 0;

    public GameEvents(PaletteMain main) {
        this.main = main;
    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent event) {
        main.onTick();
        FontRenderer renderer = Minecraft.getMinecraft().fontRendererObj;
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        int left = resolution.getScaledWidth() - renderer.getStringWidth("Demo") - 5;
        renderer.drawString("Demo", left, 5, 0xFF0000, false);
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Pre event) {
        if (event.getGui() instanceof GuiContainerCreative) {
            main.getRegistry().setupTabFilters();
        }
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (inCreativeInventory = event.getGui() instanceof GuiContainerCreative) {
            GuiContainerCreative inventory = (GuiContainerCreative) event.getGui();

            mouseX = event.getMouseX();
            mouseY = event.getMouseY();

            if (main.getCurrentPalette().isActive()) {
                if (width != event.getGui().width || height != event.getGui().height) {
                    width = event.getGui().width;
                    height = event.getGui().height;
                    main.getCurrentPalette().onResize(event.getGui().mc, width, height);
                }

                if (Keyboard.isKeyDown(main.show.getKeyCode())) {
                    main.getCurrentPalette().setOverlay(true);
                    main.getCurrentPalette().draw(mouseX, mouseY);
                    event.setCanceled(true);
                } else {
                    main.getCurrentPalette().onClose();
                    main.getCurrentPalette().setInactive();
                }
            } else if (inventory.getSlotUnderMouse() != null && Keyboard.isKeyDown(main.show.getKeyCode())) {
                main.newPalette(inventory.getSlotUnderMouse().getStack());
            }
        }
    }

    @SubscribeEvent
    public void onMouseInputEvent(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (inCreativeInventory && main.getCurrentPalette().isActive()) {
            event.setCanceled(true);

            int button = Mouse.getEventButton();

            if (Mouse.getEventButtonState()) {
                main.getCurrentPalette().mouseClick(mouseX, mouseY, button);
            } else if (button != -1) {
                main.getCurrentPalette().mouseRelease(mouseX, mouseY, button);
            }
        }
    }
}
