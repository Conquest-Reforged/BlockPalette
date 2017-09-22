package me.dags.blockpalette.palette;

import me.dags.blockpalette.crafting.RecipeTree;
import me.dags.blockpalette.search.SearchScreen;
import me.dags.blockpalette.util.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.File;

@SideOnly(Side.CLIENT)
public class PaletteMain implements IResourceManagerReloadListener {

    public static final int switchKeyID = Keyboard.KEY_LSHIFT;

    public final KeyBinding show = new KeyBinding("key.blockpalette.open", Keyboard.KEY_C, "Block Palette");
    public final KeyBinding search = new KeyBinding("key.blockpalette.search", Keyboard.KEY_V, "Block Palette");
    private PaletteRegistry registry = new PaletteRegistry();
    private RecipeTree recipeTree = new RecipeTree();
    private Palette palette = Palette.EMPTY;

    public PaletteRegistry getRegistry() {
        return registry;
    }

    public RecipeTree getRecipeTree() {
        return recipeTree;
    }

    public Palette getPalette() {
        return palette;
    }

    public void showPaletteScreen() {
        if (palette.isPresent()) {
            Minecraft.getMinecraft().displayGuiScreen(new PaletteScreen(this));
        }
    }

    public void newPaletteCreative(ItemStack itemStack) {
        palette = getRegistry().getPalette(itemStack);
    }

    public void newCraftingPalette(ItemStack itemStack) {
        palette = getRecipeTree().getPalette(itemStack);
    }

    public boolean isInventoryKey(int keyCode) {
        return keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode();
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        registry = new PaletteRegistry();
        recipeTree = new RecipeTree();
        registry.buildPalettes();
        recipeTree.buildRecipeTree();
        palette = Palette.EMPTY;
    }

    public void onPreInit(File config) {
        Config.init(config);
    }

    public void onInit() {
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
    }

    public void onTick() {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayerSP player = minecraft.player;
        if (player == null || minecraft.currentScreen != null || !Minecraft.isGuiEnabled()) {
            return;
        }

        if (getPalette().isPresent()) {
            return;
        }

        if (search.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new SearchScreen());
        }

        if (show.isPressed()) {
            if (player.isCreative()) {
                newPaletteCreative(player.getHeldItemMainhand());
                showPaletteScreen();
            } else {
                newCraftingPalette(player.getHeldItemMainhand());
                showPaletteScreen();
            }
        }
    }
}
