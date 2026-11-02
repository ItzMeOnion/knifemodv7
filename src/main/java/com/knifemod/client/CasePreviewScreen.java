package com.knifemod.client;

import com.knifemod.Cases;
import com.knifemod.KnifeDef;
import com.knifemod.KnifeItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

/** Shows every knife a case can drop, best rarity first. Nothing is rolled or opened here. */
public class CasePreviewScreen extends Screen {
    private static final int COLS = 12;
    private static final int ROWS = 5;
    private static final int CELL = 26;
    private static final int PAGE = COLS * ROWS;

    private final Screen parent;
    private final List<KnifeDef> items;
    private int page = 0;

    public CasePreviewScreen(Screen parent, Cases.CaseDef caseDef) {
        super(Text.literal(caseDef.name()));
        this.parent = parent;
        this.items = Cases.pool(caseDef).stream().sorted(KnifeState.BY_RARITY).toList();
    }

    private int pageCount() {
        return Math.max(1, (items.size() + PAGE - 1) / PAGE);
    }

    private int gridX() {
        return width / 2 - COLS * CELL / 2;
    }

    private int gridY() {
        return 40;
    }

    @Override
    protected void init() {
        int y = gridY() + ROWS * CELL + 10;
        addDrawableChild(ButtonWidget.builder(Text.literal("< Prev"), b -> {
            if (page > 0) {
                page--;
                clearAndInit();
            }
        }).dimensions(gridX(), y, 70, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Next >"), b -> {
            if (page < pageCount() - 1) {
                page++;
                clearAndInit();
            }
        }).dimensions(gridX() + COLS * CELL - 70, y, 70, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> close())
                .dimensions(width / 2 - 35, height - 30, 70, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, 12, 0xFFFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("Page " + (page + 1) + " / " + pageCount() + "   " + items.size() + " knives"),
                width / 2, 26, 0xFFAAAAAA);

        KnifeDef hovered = null;
        int from = page * PAGE;
        int to = Math.min(items.size(), from + PAGE);
        for (int i = from; i < to; i++) {
            int idx = i - from;
            int cx = gridX() + (idx % COLS) * CELL;
            int cy = gridY() + (idx / COLS) * CELL;
            KnifeDef k = items.get(i);
            ctx.fill(cx + 1, cy + 1, cx + CELL - 1, cy + CELL - 1, 0xFF202020);
            ctx.fill(cx + 1, cy + 1, cx + CELL - 1, cy + 3, 0xFF000000 | k.rarity().color());
            ctx.drawItem(KnifeItems.createStack(k), cx + 5, cy + 6);
            if (mouseX >= cx && mouseX < cx + CELL && mouseY >= cy && mouseY < cy + CELL) {
                hovered = k;
            }
        }

        int infoY = gridY() + ROWS * CELL + 36;
        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("Drop chance: Common 60%   Uncommon 25%   Rare 10%   Epic 4%   Legendary 1%"),
                width / 2, infoY, 0xFFCCCCCC);
        if (hovered != null) {
            ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(hovered.name()),
                    width / 2, infoY + 14, 0xFF000000 | hovered.rarity().color());
        }
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
