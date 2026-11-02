package com.knifemod.client;

import com.knifemod.KnifeDef;
import com.knifemod.KnifeItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Every knife you own, best rarity first. Each row shows how many copies you have. */
public class KnifeInventoryScreen extends Screen {
    private static final int ROWS = 9;
    private static final int ROW_H = 22;

    private final Screen parent;
    private final List<Map.Entry<KnifeDef, Integer>> rows;
    private int page = 0;

    public KnifeInventoryScreen(Screen parent) {
        super(Text.literal("Your knives"));
        this.parent = parent;

        Map<KnifeDef, Integer> counts = new LinkedHashMap<>();
        for (KnifeDef k : KnifeState.OWNED) {
            counts.merge(k, 1, Integer::sum);
        }
        this.rows = counts.entrySet().stream()
                .sorted((a, b) -> KnifeState.BY_RARITY.compare(a.getKey(), b.getKey()))
                .toList();
    }

    private int pageCount() {
        return Math.max(1, (rows.size() + ROWS - 1) / ROWS);
    }

    private int top() {
        return 40;
    }

    private int left() {
        return width / 2 - 160;
    }

    @Override
    protected void init() {
        int from = page * ROWS;
        int to = Math.min(rows.size(), from + ROWS);
        for (int i = from; i < to; i++) {
            KnifeDef k = rows.get(i).getKey();
            int y = top() + (i - from) * ROW_H;
            String label = KnifeState.equipped == k ? "Equipped" : "Equip";
            addDrawableChild(ButtonWidget.builder(Text.literal(label), b -> {
                KnifeState.equip(k);
                clearAndInit();
            }).dimensions(left() + 240, y, 80, 18).build());
        }

        int navY = top() + ROWS * ROW_H + 8;
        addDrawableChild(ButtonWidget.builder(Text.literal("< Prev"), b -> {
            if (page > 0) {
                page--;
                clearAndInit();
            }
        }).dimensions(left(), navY, 70, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Next >"), b -> {
            if (page < pageCount() - 1) {
                page++;
                clearAndInit();
            }
        }).dimensions(left() + 250, navY, 70, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> close())
                .dimensions(width / 2 - 35, height - 30, 70, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, 12, 0xFFFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal(KnifeState.OWNED.size() + " knives owned, " + rows.size() + " different. Best rarity first."),
                width / 2, 26, 0xFFAAAAAA);

        if (rows.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("Open some cases first (K)."),
                    width / 2, top() + 20, 0xFFAAAAAA);
            return;
        }

        int from = page * ROWS;
        int to = Math.min(rows.size(), from + ROWS);
        for (int i = from; i < to; i++) {
            KnifeDef k = rows.get(i).getKey();
            int count = rows.get(i).getValue();
            int y = top() + (i - from) * ROW_H;
            KnifeDef icon = KnifeState.equipped == k ? KnifeState.skinFor(k) : k;
            ctx.drawItem(KnifeItems.createStack(icon), left(), y + 1);
            String tag = KnifeState.equipped == k ? "  [equipped]" : "";
            ctx.drawTextWithShadow(textRenderer,
                    Text.literal(k.name() + "  x" + count + tag),
                    left() + 22, y + 5, 0xFF000000 | k.rarity().color());
        }

        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("Page " + (page + 1) + " / " + pageCount()),
                width / 2, top() + ROWS * ROW_H + 12, 0xFFAAAAAA);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
