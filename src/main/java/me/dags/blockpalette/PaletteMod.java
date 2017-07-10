package me.dags.blockpalette;

import me.dags.blockpalette.palette.PaletteMain;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Collections;

/**
 * @author dags <dags@dags.me>
 */
@Mod(modid = PaletteMod.MOD_ID, version = PaletteMod.VERSION, clientSideOnly = true)
public class PaletteMod {

    public static final String MOD_ID = "blockpalette";
    public static final String VERSION = "1.4.1";

    private final PaletteMain main = new PaletteMain();
    private final GameEvents events = new GameEvents(main);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        main.onPreInit(event.getSuggestedConfigurationFile());

        ModMetadata modMetadata = event.getModMetadata();
        modMetadata.modId = MOD_ID;
        modMetadata.version = VERSION;
        modMetadata.name = "BlockPalette";
        modMetadata.credits = "Textures by Monsterfish_";
        modMetadata.logoFile = "assets/blockpalette/logo.png";
        modMetadata.url = "https://blockpalette.dags.me";
        modMetadata.authorList = Collections.singletonList("dags");
        modMetadata.description = "A creative-mode block picker and colour wheel";
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        main.onInit();
        ClientRegistry.registerKeyBinding(main.show);
        ClientRegistry.registerKeyBinding(main.search);
        MinecraftForge.EVENT_BUS.register(events);
    }
}
