package me.dags.blockpalette;

import me.dags.blockpalette.creative.CreativePickMode;
import me.dags.blockpalette.creative.KeybindPickMode;
import me.dags.blockpalette.creative.MousePickMode;
import me.dags.blockpalette.creative.PickMode;
import me.dags.blockpalette.palette.PaletteMain;
import me.dags.blockpalette.util.Config;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author dags <dags@dags.me>
 */
@SideOnly(Side.CLIENT)
public class GameEvents {

    private final PaletteMain main;
    private CreativePickMode creativeEvents;

    private boolean inCreativeInventory = false;

    public GameEvents(PaletteMain main) {
        this.main = main;
    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent event) {
        main.onTick();
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
            GuiContainerCreative inventory = (GuiContainerCreative) event.getGui();
            inCreativeInventory = inventory.getSelectedTabIndex() != CreativeTabs.SEARCH.getTabIndex();
            if (inCreativeInventory) {
                creativeEvents.onDrawScreen(inventory, event.getMouseX(), event.getMouseY(), event);
            }
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
