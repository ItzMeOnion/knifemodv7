package com.knifemod;

import java.util.List;
import java.util.Random;

public final class Cases {
    public record CaseDef(String name, List<String> finishes) {
    }

    public static final List<CaseDef> ALL = List.of(
            new CaseDef("Starter Case", List.of("Vanilla", "Fade", "Doppler", "Tiger Tooth", "Lore", "Slaughter", "Crimson Web", "Black Pearl",
                    "Wooden Sword", "Stone Sword", "Iron Sword", "Golden Sword", "Diamond Sword", "Netherite Sword")),
            new CaseDef("Ember Case", List.of("Fade", "Ruby", "Crimson Web", "Lore", "Gold")),
            new CaseDef("Emerald Case", List.of("Emerald", "Tiger Tooth", "Black Pearl", "Doppler", "Sapphire", "Marble Fade")),
            new CaseDef("Void Case", List.of("Void", "Black Pearl", "Slaughter", "Doppler", "Diamond", "Gold")),
            new CaseDef("Donut Case", List.of("Donut", "Diamond", "Gold", "Marble Fade", "Ruby", "Emerald", "Sapphire", "Void"))
    );

    private Cases() {
    }

    public static List<KnifeDef> pool(CaseDef c) {
        return KnifeList.ALL.stream().filter(k -> c.finishes().contains(k.finish())).toList();
    }

    /** Rolls a rarity, then a random knife of that rarity from the case. Never consumes anything. */
    public static KnifeDef open(CaseDef c, Random random) {
        List<KnifeDef> pool = pool(c);
        Rarity rarity = Rarity.roll(random);
        while (rarity != null) {
            final Rarity wanted = rarity;
            List<KnifeDef> tier = pool.stream().filter(k -> k.rarity() == wanted).toList();
            if (!tier.isEmpty()) return tier.get(random.nextInt(tier.size()));
            rarity = rarity.lower();
        }
        return pool.get(random.nextInt(pool.size()));
    }
}
