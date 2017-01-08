package me.dags.blockpalette;

import me.dags.blockpalette.palette.PaletteMain;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * @author dags <dags@dags.me>
 */
@Mod(modid = PaletteMod.MOD_ID, version = PaletteMod.VERSION, clientSideOnly = true)
public class PaletteMod {

    public static final String MOD_ID = "blockpalette";
    public static final String VERSION = "1.0-DEMO";

    private final PaletteMain main = new PaletteMain();
    private final GameEvents events = new GameEvents(main);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        main.onPreInit(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        main.onInit();
        ClientRegistry.registerKeyBinding(main.show);
        MinecraftForge.EVENT_BUS.register(events);
    }
}
