package com.knifemod;

import java.util.Random;

/** Weights add up to 100, so LEGENDARY is a 1% roll. */
public enum Rarity {
    COMMON("Consumer", 0xB0C3D9, 60),
    UNCOMMON("Industrial", 0x5E98D9, 25),
    RARE("Mil-Spec", 0x4B69FF, 10),
    EPIC("Covert", 0xEB4B4B, 4),
    LEGENDARY("Legendary", 0xFFD700, 1);

    private final String label;
    private final int color;
    private final int weight;

    Rarity(String label, int color, int weight) {
        this.label = label;
        this.color = color;
        this.weight = weight;
    }

    public String label() {
        return label;
    }

    public int color() {
        return color;
    }

    public static Rarity roll(Random random) {
        int x = random.nextInt(100);
        int acc = 0;
        for (Rarity r : values()) {
            acc += r.weight;
            if (x < acc) return r;
        }
        return COMMON;
    }

    /** The next rarity down, or null when already at the bottom. */
    public Rarity lower() {
        return ordinal() == 0 ? null : values()[ordinal() - 1];
    }
}
