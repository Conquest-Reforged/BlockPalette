package me.dags.blockpalette.util;

import me.dags.blockpalette.color.ColorMode;
import me.dags.blockpalette.creative.PickMode;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * @author dags <dags@dags.me>
 */
public class Config {

    public static boolean match_textures = true;
    public static PickMode pick_mode = PickMode.MOUSE;
    public static int animation_speed = 5;

    public static ColorMode color_mode = ColorMode.ADJACENT;
    public static boolean show_hue = false;
    public static int group_size = 3;
    public static int angle = 30;
    public static float leniency = 0.25F;

    private static Configuration cfg = new Configuration();

    public static void init(File file) {
        cfg = new Configuration(file);
        cfg.load();
        match_textures = cfg.get("general", "match_textures", true).getBoolean();
        pick_mode = PickMode.fromId(cfg.get("general", "pick_mode", 0).getInt());
        animation_speed = cfg.get("general", "filter_variants", animation_speed).getInt();
        color_mode = ColorMode.fromId(cfg.get("color", "color_mode", ColorMode.getId(color_mode)).getInt());
        show_hue = cfg.get("color", "show_hue", show_hue).getBoolean();
        angle = cfg.get("color", "angle", angle).getInt();
        group_size = cfg.get("color", "group_size", group_size).getInt();
        leniency = (float) cfg.get("color", "leniency", leniency).getDouble();
        cfg.save();
    }

    public static void save() {
        cfg.get("general", "match_textures", match_textures).set(match_textures);
        cfg.get("general", "pick_mode", 0).set(PickMode.toId(pick_mode));
        cfg.get("general", "animation_speed", animation_speed).set(animation_speed);
        cfg.get("color", "color_mode", 0).set(ColorMode.getId(color_mode));
        cfg.get("color", "show_hue", show_hue).set(show_hue);
        cfg.get("color", "angle", angle).set(angle);
        cfg.get("color", "group_size", group_size).set(group_size);
        cfg.get("color", "leniency", leniency).set(leniency);
        cfg.save();
    }
}
