package me.dags.blockpalette.search;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public interface Tag {

    static Tag of(String name) {
        return () -> name;
    }

    static Tag and(String name, String... and) {
        return new Tag() {
            @Override
            public String getTag() {
                return name;
            }

            @Override
            public boolean test(String in) {
                for (String s : and) {
                    if (!in.contains(s)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    static Tag or(String name, String... or) {
        return new Tag() {
            @Override
            public String getTag() {
                return name;
            }

            @Override
            public boolean test(String in) {
                for (String s : or) {
                    if (in.contains(s)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    static Tag not(String name, String accept, String reject) {
        return new Tag() {
            @Override
            public String getTag() {
                return name;
            }

            @Override
            public boolean test(String in) {
                return in.contains(accept) && !in.contains(reject);
            }
        };
    }

    String getTag();

    default boolean test(String in) {
        return in.contains(getTag());
    }

    List<Tag> TAGS = ImmutableList.<Tag>builder()
            .add(Tag.of("slab"))
            .add(Tag.of("stair"))
            .add(Tag.of("wall"))
            .add(Tag.of("fence"))
            .add(Tag.of("gate"))
            .add(Tag.of("carpet"))
            .add(Tag.of("layer"))
            .add(Tag.not("plate", "plate", "chestplate"))
            .add(Tag.of("log"))
            .add(Tag.of("pillar"))
            .add(Tag.of("column"))
            .add(Tag.not("door", "door", "trapdoor"))
            .add(Tag.of("trapdoor"))
            .add(Tag.or("leaves", "leaves", "leaf"))
            .add(Tag.not("bed", "bed", "bedrock"))
            .add(Tag.of("arch"))
            .add(Tag.of("corner"))
            .add(Tag.of("glass"))
            .add(Tag.of("pane"))
            .add(Tag.of("stained"))
            .add(Tag.of("hardened"))
            .add(Tag.of("dye"))
            .add(Tag.of("egg"))
            .add(Tag.of("potion"))
            .add(Tag.of("arrow"))
            .add(Tag.or("armor", "helmet", "chestplate", "leggings", "boots"))
            .add(Tag.of("helmet"))
            .add(Tag.of("chestplate"))
            .add(Tag.of("leggings"))
            .add(Tag.of("boots"))
            .add(Tag.or("food", "apple", "stew", "bread", "pork", "beef", "steak", "mutton", "chicken", "rabbit", "fish", "carrot", "potato", "pumpkin", "melon"))
            .add(Tag.or("tool", "axe", "pick", "shovel", "hoe", "shear", "compass", "clock", "fishing rod"))
            .add(Tag.or("weapon", "sword", "bow", "splash", "arrow"))
            .add(Tag.of("sword"))
            .add(Tag.of("hoe"))
            .add(Tag.of("pickaxe"))
            .add(Tag.not("axe", "axe", "pickaxe"))
            .add(Tag.of("shovel"))
            .build();
}
