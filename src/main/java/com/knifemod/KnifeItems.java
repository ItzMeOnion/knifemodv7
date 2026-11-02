package com.knifemod;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public final class KnifeItems {
    private static final Map<String, Item> ITEMS = new HashMap<>();

    private KnifeItems() {
    }

    public static void register() {
        for (KnifeDef k : KnifeList.ALL) {
            Identifier id = Identifier.of(KnifeMod.MOD_ID, k.id());
            RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
            Item item = Registry.register(Registries.ITEM, id,
                    new Item(new Item.Settings().registryKey(key).maxCount(1)));
            ITEMS.put(k.id(), item);
        }
    }

    /** Builds a display stack for a knife: coloured name, glint for Epic and Legendary. */
    public static ItemStack createStack(KnifeDef k) {
        ItemStack stack = new ItemStack(ITEMS.get(k.id()));
        stack.set(DataComponentTypes.ITEM_NAME,
                Text.literal(k.name()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(k.rarity().color()))));
        if (k.rarity().ordinal() >= Rarity.EPIC.ordinal()) {
            stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
        return stack;
    }
}
