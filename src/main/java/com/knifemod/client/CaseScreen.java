package com.knifemod.client;

import com.knifemod.Cases;
import com.knifemod.KnifeDef;
import com.knifemod.KnifeItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CaseScreen extends Screen {
    private static final int CARD = 60;
    private static final int WIN_INDEX = 40;
    private static final int STRIP_LENGTH = 52;
    private static final int DURATION_MS = 5000;

    private final Random random = new Random();
    private int selected = 0;
    private boolean opening = false;
    private long openStart;
    private List<KnifeDef> strip = new ArrayList<>();
    private KnifeDef winner;
    private String status = "Pick a case and open it. Cases are infinite.";

    public CaseScreen() {
        super(Text.literal("Knife Cases"));
    }

    @Override
    protected void init() {
        for (int i = 0; i < Cases.ALL.size(); i++) {
            final int idx = i;
            addDrawableChild(ButtonWidget.builder(Text.literal(Cases.ALL.get(i).name()),
                    b -> selected = idx).dimensions(10, 40 + i * 24, 120, 20).build());
        }
        addDrawableChild(ButtonWidget.builder(Text.literal("OPEN CASE"),
                b -> openCase()).dimensions(width / 2 - 130, height - 60, 120, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Equip next owned"),
                b -> KnifeState.equipNext()).dimensions(width / 2 - 4, height - 60, 130, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Draw / put away"),
                b -> toggleHand()).dimensions(width / 2 - 130, height - 36, 120, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Preview case"),
                b -> MinecraftClient.getInstance().setScreen(new CasePreviewScreen(this, Cases.ALL.get(selected))))
                .dimensions(width / 2 - 4, height - 36, 130, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("My knives"),
                b -> MinecraftClient.getInstance().setScreen(new KnifeInventoryScreen(this)))
                .dimensions(10, 40 + Cases.ALL.size() * 24 + 8, 120, 20).build());
    }

    private void toggleHand() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            KnifeState.toggle(client.player.getInventory());
        }
    }

    private void openCase() {
        if (opening) return;
        Cases.CaseDef c = Cases.ALL.get(selected);
        winner = Cases.open(c, random);

        List<KnifeDef> pool = Cases.pool(c);
        strip = new ArrayList<>();
        for (int i = 0; i < STRIP_LENGTH; i++) {
            strip.add(pool.get(random.nextInt(pool.size())));
        }
        strip.set(WIN_INDEX, winner);

        opening = true;
        openStart = System.currentTimeMillis();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, 12, 0xFFFFFFFF);

        int stripY = height / 2 - 60;
        if (opening) {
            float t = Math.min(1f, (System.currentTimeMillis() - openStart) / (float) DURATION_MS);
            float ease = 1f - (float) Math.pow(1f - t, 4); // slows down at the end like CS
            int offset = (int) (ease * WIN_INDEX * CARD);

            ctx.enableScissor(10, stripY - 4, width - 10, stripY + CARD + 4);
            for (int i = 0; i < strip.size(); i++) {
                KnifeDef k = strip.get(i);
                int x = width / 2 - CARD / 2 + i * CARD - offset;
                ctx.fill(x + 2, stripY, x + CARD - 2, stripY + CARD, 0xFF202020);
                ctx.fill(x + 2, stripY, x + CARD - 2, stripY + 3, 0xFF000000 | k.rarity().color());
                ctx.drawItem(KnifeItems.createStack(k), x + (CARD - 16) / 2, stripY + 14);
            }
            ctx.disableScissor();

            // Centre marker
            ctx.fill(width / 2 - 1, stripY - 6, width / 2 + 1, stripY + CARD + 6, 0xFFFFFFFF);

            if (t >= 1f) {
                opening = false;
                KnifeState.OWNED.add(winner);
                if (KnifeState.equipped == null) KnifeState.equipped = winner;
                status = "You got: " + winner.name() + " (" + winner.rarity().label() + ")";
            }
        }

        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(status), width / 2,
                stripY + CARD + 14, opening ? 0xFFAAAAAA : 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer,
                Text.literal("Owned: " + KnifeState.OWNED.size() + "   Equipped: "
                        + (KnifeState.equipped == null ? "none" : KnifeState.equipped.name())),
                10, height - 16, 0xFFAAAAAA);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
