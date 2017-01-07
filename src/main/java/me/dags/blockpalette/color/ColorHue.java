package me.dags.blockpalette.color;

import java.util.*;

/**
 * @author dags <dags@dags.me>
 */
public class ColorHue {

    private final Comparator<Texture> comparator;
    private final List<Texture> textures = new ArrayList<>();
    private boolean isDirty = true;

    public ColorHue(Comparator<Texture> comparator) {
        this.comparator = comparator;
    }

    public List<Texture> getTextures() {
        if (isDirty) {
            isDirty = false;
            Collections.sort(textures, comparator);
        }
        return textures;
    }

    public int size() {
        return textures.size();
    }

    public boolean isEmpty() {
        return textures.isEmpty();
    }

    public int indexOf(Texture texture) {
        return textures.indexOf(texture);
    }

    public void clear() {
        isDirty = true;
        textures.clear();
    }

    public void addTexture(Texture texture) {
        textures.add(texture);
        isDirty = true;
    }

    public void matchSaturation(Collection<Texture> results, float saturation, float tolerance) {
        List<Texture> textures = getTextures();
        if (textures.isEmpty()) {
            return;
        }

        for (Texture texture : textures) {
            if (delta(texture.saturation, saturation) <= tolerance) {
                results.add(texture);
            }
        }
    }

    public void matchLuminance(Collection<Texture> results, float luminance, float tolerance) {
        List<Texture> textures = getTextures();
        if (textures.isEmpty()) {
            return;
        }

        for (Texture texture : textures) {
            if (delta(texture.saturation, luminance) <= tolerance) {
                results.add(texture);
            }
        }
    }

    private static float delta(float f1, float f2) {
        return Math.min(f1, f2) - Math.max(f1, f2);
    }
}
