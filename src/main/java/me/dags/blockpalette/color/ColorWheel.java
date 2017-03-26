package me.dags.blockpalette.color;

import java.util.*;

/**
 * @author dags <dags@dags.me>
 */
public class ColorWheel {

    private static final int MAX_OFFSET = 60;
    private static final Comparator<Texture> SORT_GRAY = sortGray();
    private static final Comparator<Texture> SORT_COLOR = sortColor();

    private final int mod;
    private final ColorHue[] hues;
    private final ColorHue grays = new ColorHue(SORT_GRAY);
    private final Map<String, Texture> textureMap = new HashMap<>();

    // The angle used to determine triad and tetrad points
    private int angle = 30;
    // Controls how leniently saturation levels will be matched
    private float leniency = 0.95F;
    // Controls at what point a color should be determined to be gray
    private float grayPoint = 0.15F;
    // Controls the minimum allowed average alpha value
    private float alphaPoint = 0.5F;

    public ColorWheel() {
        this(24);
    }

    public ColorWheel(int colorGroups) {
        this.hues = new ColorHue[colorGroups];
        this.mod = 359 / (colorGroups - 1);
        for (int i = 0; i < hues.length; i++) {
            hues[i] = new ColorHue(SORT_COLOR);
        }
    }

    public List<Texture> getGrays() {
        return grays.getTextures();
    }

    public boolean hasTexture(String name) {
        return textureMap.containsKey(name);
    }

    public Texture getTexture(String name) {
        Texture texture = textureMap.get(name);
        return texture != null ? texture : Texture.EMPTY;
    }

    public void setAngle(int angle) {
        this.angle = Math.min(120, Math.max(0, angle));
    }

    public void setLeniency(float leniency) {
        this.leniency = Math.min(1, Math.max(0, leniency));
    }

    public void setGrayPoint(float grayPoint) {
        this.grayPoint = Math.min(1F, Math.max(0F, grayPoint));
    }

    public void setAlphaPoint(float alphaPoint) {
        this.alphaPoint = Math.min(1F, Math.max(0F, alphaPoint));
    }

    public void refresh() {
        getGrays().clear();

        for (ColorHue hue : hues) {
            hue.clear();
        }

        for (Texture texture : textureMap.values()) {
            addTexture(texture);
        }
    }

    public void addTexture(Texture texture) {
        if (!texture.isPresent()) {
            return;
        }

        textureMap.put(texture.name, texture);

        if (texture.alpha >= alphaPoint) {
            if (isGray(texture)) {
                grays.addTexture(texture);
            } else {
                getHue(texture).addTexture(texture);
            }
        }
    }

    public ColorHue getHue(Texture texture) {
        return getHue(texture.hue);
    }

    public ColorHue getHue(int hue) {
        int index = Math.min(359, Math.max(0, hue / mod));
        return hues[index];
    }

    public boolean isGray(Texture texture) {
        return texture.saturation <= grayPoint;
    }

    public List<Texture> getAdjacent(Texture texture, int size) {
        if (!texture.isPresent()) {
            return Collections.emptyList();
        }

        if (isGray(texture)) {
            return getAdjacentGrays(texture, size);
        } else {
            return getAdjacentHues(texture, size);
        }
    }

    public List<Texture> getComplimentary(Texture texture, int size) {
        if (!texture.isPresent()) {
            return Collections.emptyList();
        }

        if (isGray(texture)) {
            float opposite = Math.min(1F, Math.max(0F, 1F - texture.brightness));
            List<Texture> l1 = matchGray(texture.brightness, size);
            List<Texture> l2 = matchGray(opposite, size);
            l1.addAll(l2);
            return l1;
        } else {
            int opposite = clampHue(texture.hue - 180);
            List<Texture> l1 = matchColor(texture.hue, texture.strength, size);
            List<Texture> l2 = matchColor(opposite, texture.strength, size);
            l1.addAll(l2);
            return l1;
        }
    }

    public List<Texture> getTriad(Texture texture, int groupSize) {
        if (!texture.isPresent()) {
            return Collections.emptyList();
        }

        if (isGray(texture)) {
            return getGrayTriad(texture, groupSize);
        } else {
            return getHueTriad(texture, groupSize);
        }
    }

    public List<Texture> getTetrad(Texture texture, int groupSize) {
        if (!texture.isPresent()) {
            return Collections.emptyList();
        }

        if (isGray(texture)) {
            return getGrayTetrad(texture, groupSize);
        } else {
            return getHueTetrad(texture, groupSize);
        }
    }

    public List<Texture> getRainbow(Texture texture, int groupSize) {
        if (!texture.isPresent()) {
            return Collections.emptyList();
        }

        if (isGray(texture)) {
            return Collections.emptyList();
        } else {
            List<Texture> textures = new ArrayList<>();
            int distance = 360 / 8;
            for (int i = 0; i < 12; i ++) {
                int hue = texture.hue + (i * distance);
                textures.addAll(matchColor(hue, texture.strength, groupSize));
            }
            return textures;
        }
    }

    private List<Texture> getAdjacentHues(Texture texture, int groupSize) {
        int h1 = texture.hue;
        int h2 = clampHue(texture.hue - angle);
        int h3 = clampHue(texture.hue + angle);
        List<Texture> l1 = matchColor(h1, texture.strength, groupSize);
        List<Texture> l2 = matchColor(h2, texture.strength, groupSize);
        List<Texture> l3 = matchColor(h3, texture.strength, groupSize);

        int min = Math.min(l1.size(), Math.min(l2.size(), l3.size()));
        if (min != groupSize) {
            trimList(l1, min);
            trimList(l2, min);
            trimList(l3, min);
        }

        l1.addAll(l2);
        l1.addAll(l3);

        return l1;
    }

    private List<Texture> getHueTriad(Texture texture, int groupSize) {
        int h1 = texture.hue;
        int h2 = clampHue(texture.hue - 180 - angle);
        int h3 = clampHue(texture.hue - 180 + angle);
        List<Texture> l1 = matchColor(h1, texture.strength, groupSize);
        List<Texture> l2 = matchColor(h2, texture.strength, groupSize);
        List<Texture> l3 = matchColor(h3, texture.strength, groupSize);

        int min = Math.min(l1.size(), Math.min(l2.size(), l3.size()));
        if (min != groupSize) {
            trimList(l1, min);
            trimList(l2, min);
            trimList(l3, min);
        }

        l1.addAll(l2);
        l1.addAll(l3);
        return l1;
    }

    private List<Texture> getHueTetrad(Texture texture, int groupSize) {
        int h1 = clampHue(texture.hue);
        int h2 = clampHue(texture.hue + angle);
        int h3 = clampHue(texture.hue + 180);
        int h4 = clampHue(texture.hue + 180 + angle);
        List<Texture> l1 = matchColor(h1, texture.strength, groupSize);
        List<Texture> l2 = matchColor(h2, texture.strength, groupSize);
        List<Texture> l3 = matchColor(h3, texture.strength, groupSize);
        List<Texture> l4 = matchColor(h4, texture.strength, groupSize);

        int min = Math.min(l1.size(), Math.min(l2.size(), Math.min(l3.size(), l4.size())));
        if (min != groupSize) {
            trimList(l1, min);
            trimList(l2, min);
            trimList(l3, min);
            trimList(l4, min);
        }

        l1.addAll(l2);
        l1.addAll(l3);
        l1.addAll(l4);
        return l1;
    }

    private List<Texture> getAdjacentGrays(Texture texture, int groupSize) {
        return Collections.emptyList();
    }

    private List<Texture> getGrayTriad(Texture texture, int groupSize) {
        float range, p1, p2, p3;
        if (texture.brightness < 1 / 3) {
            range = 1F - texture.brightness;
            p1 = texture.brightness;
        } else if (texture.brightness > 2 / 3) {
            range = texture.brightness;
            p1 = 0;
        } else {
            range = 2F * Math.min(texture.brightness, 1F - texture.brightness);
            p1 = texture.brightness - (range / 2);
        }
        p2 = p1 + (range / 2);
        p3 = p1 + range;
        List<Texture> l1 = matchGray(p1, groupSize);
        List<Texture> l2 = matchGray(p2, groupSize);
        List<Texture> l3 = matchGray(p3, groupSize);

        int min = Math.min(l1.size(), Math.min(l2.size(), l3.size()));
        if (min != groupSize) {
            trimList(l1, min);
            trimList(l2, min);
            trimList(l3, min);
        }

        l1.addAll(l2);
        l1.addAll(l3);
        return l1;
    }

    private List<Texture> getGrayTetrad(Texture texture, int groupSize) {
        float l = texture.brightness;
        float range, p1, p2, p3, p4;

        if (l < 0.25F) {
            range = 1F - l;
            p1 = l;
        } else if (l < 0.5F) {
            range = ((1F - l) / 2) * 3;
            p1 = 1F - range;
        } else if (l < 0.75F) {
            range = (l / 2) * 3;
            p1 = 0;
        } else {
            range = l;
            p1 = 0;
        }

        float step = range / 3;
        p2 = p1 + step;
        p3 = p1 + (2 * step);
        p4 = p1 + range;

        List<Texture> l1 = matchGray(p1, groupSize);
        List<Texture> l2 = matchGray(p2, groupSize);
        List<Texture> l3 = matchGray(p3, groupSize);
        List<Texture> l4 = matchGray(p4, groupSize);

        int min = Math.min(l1.size(), Math.min(l2.size(), Math.min(l3.size(), l4.size())));
        if (min != groupSize) {
            trimList(l1, min);
            trimList(l2, min);
            trimList(l3, min);
            trimList(l4, min);
        }

        l1.addAll(l2);
        l1.addAll(l3);
        l1.addAll(l4);

        return l1;
    }

    private List<Texture> matchColor(int hue, float strength, int size) {
        Set<Texture> results = new LinkedHashSet<>();

        for (int offset = 0; offset < MAX_OFFSET && results.size() < size; offset++) {
            if (offset == 0) {
                getHue(clampHue(hue)).matchSaturation(results, strength, leniency);
            } else {
                getHue(clampHue(hue - offset)).matchSaturation(results, strength, leniency);
                getHue(clampHue(hue + offset)).matchSaturation(results, strength, leniency);
            }
        }
        List<Texture> list = new ArrayList<>(results);

        // Adds a bit of variance in the returned textures
        Collections.shuffle(list);
        trimList(list, size);
        Collections.sort(list, SORT_COLOR);

        return list;
    }

    private List<Texture> matchGray(float brightness, int size) {
        Set<Texture> results = new LinkedHashSet<>();
        grays.matchLuminance(results, brightness, leniency / 2);

        List<Texture> list = new ArrayList<>(results);

        // Adds a bit of variance in the returned textures
        Collections.shuffle(list);
        trimList(list, size);
        Collections.sort(list, SORT_GRAY);

        return list;
    }

    private static void trimList(List<?> list, int size) {
        while (list.size() > size) {
            list.remove(list.size() - 1);
        }
    }

    static int clampHue(float input) {
        int hue = Math.round(input);
        if (hue < 0) {
            return 360 + (hue % 360);
        } else if (hue > 360) {
            return hue % 360;
        }
        return Math.min(hue, 359);
    }

    private static Comparator<Texture> sortGray() {
        return new Comparator<Texture>() {
            @Override
            public int compare(Texture t1, Texture t2) {
                return Float.compare(t2.luminosity, t1.luminosity);
            }
        };
    }

    private static Comparator<Texture> sortColor() {
        return new Comparator<Texture>() {
            @Override
            public int compare(Texture t1, Texture t2) {
                if (Math.abs(t2.strength - t1.strength) < 0.05F) {
                    return Float.compare(t2.luminosity, t1.luminosity);
                }
                if (Math.abs(t2.saturation - t1.saturation) < 0.1F) {
                    return Float.compare(t2.brightness, t1.brightness);
                }
                return Float.compare(t2.strength, t1.strength);
            }
        };
    }
}
