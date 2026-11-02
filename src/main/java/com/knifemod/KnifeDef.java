package com.knifemod;

/**
 * One knife skin. "texture" is the vanilla sprite it is drawn with, and "color" tints it.
 */
public record KnifeDef(String id, String name, String base, String finish, Rarity rarity, int color, String texture) {
}
