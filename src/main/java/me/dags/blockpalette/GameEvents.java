package me.dags.blockpalette;

import me.dags.blockpalette.creative.CreativePickMode;
import me.dags.blockpalette.creative.KeybindPickMode;
import me.dags.blockpalette.creative.MousePickMode;
import me.dags.blockpalette.creative.PickMode;
import me.dags.blockpalette.palette.PaletteMain;
import me.dags.blockpalette.util.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * @author dags <dags@dags.me>
 */
public class GameEvents {

    private final PaletteMain main;
    private CreativePickMode creativeEvents;

    private boolean inCreativeInventory = false;

    public GameEvents(PaletteMain main) {
        this.main = main;
        creativeEvents = new KeybindPickMode(main);
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
            creativeEvents = Config.pick_mode == PickMode.KEYBOARD ? new KeybindPickMode(main) : new MousePickMode(main);
            creativeEvents.onInitGui();
        }
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (event.getGui() instanceof GuiContainerCreative) {
            inCreativeInventory = true;
            GuiContainerCreative inventory = (GuiContainerCreative) event.getGui();
            creativeEvents.onDrawScreen(inventory, event.getMouseX(), event.getMouseY(), event);
        } else if (inCreativeInventory) {
            inCreativeInventory = false;
        }
    }

    @SubscribeEvent
    public void onMouseInputEvent(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (inCreativeInventory) {
            creativeEvents.onMouseAction(event);
        }
    }

    @SubscribeEvent
    public void onKeyboarInputEvent(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (inCreativeInventory) {
            creativeEvents.onKeyAction(event);
        }
    }
}
