package io.github.ilcheese2.crystal_fortunes.client.renderers;



import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.github.ilcheese2.crystal_fortunes.entities.FairyEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

public class DialogueRenderer {


    static final double DIALOGUE_X = 1; // in screen percent
    static final double DIALOGUE_Y = 0.75;
    static final int BORDER_SIZE = 5;
    static final int DEFAULT_DISPLAY_TICKS = 50;

    static final float FADE_THRESHOLD = 10;
    static final int MOVE_THRESHOLD = 20;
    static final float MOVE_END_THRESHOLD = 12;

    static class Dialogue { // I miss c++
        Text text;
        int ticksLeft;

        Dialogue(Text text) {
            this.text = text;
            ticksLeft = DEFAULT_DISPLAY_TICKS;
        }
    }

    private final LinkedList<Dialogue> dialogues = new LinkedList<>();

    private FairyEntity fairy = null;

    public static int fairyAlpha = 0x100;

    public DialogueRenderer() {

    }

    public void addText(Text text) {
        dialogues.addLast(new Dialogue(text));
    }

    public void tick() {
        if (dialogues.isEmpty()) { return; }
        dialogues.getFirst().ticksLeft -= 1;
        if (dialogues.getFirst().ticksLeft <= 0) {
            dialogues.removeFirst();
        }
    }

    public void render(DrawContext context) {
        if (!dialogues.isEmpty()) {
            if (fairy == null) {
                fairy = new FairyEntity(CrystalFortunes.FAIRY_ENTITY, MinecraftClient.getInstance().world);
            }
            if (dialogues.size() > 1 && dialogues.getFirst().ticksLeft < (MOVE_END_THRESHOLD)) {
                drawText(context, dialogues.get(1));
            }
            drawText(context, dialogues.getFirst());
        }
    }


    private float ease(float progress, float start, float end) {
        progress = MathHelper.clamp(progress, 0, 1);
        progress = (float) (1f - Math.pow(1f - progress, 3f));
        return MathHelper.lerp(progress, start, end);
    }

    private void drawText(DrawContext context, Dialogue dialogue) {
        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;

        int width = renderer.getWidth(dialogue.text);
        int x = (int) (context.getScaledWindowWidth() * DIALOGUE_X) - width;
        int y = (int) (context.getScaledWindowHeight() * DIALOGUE_Y) - renderer.fontHeight/2;
        int c = renderer.fontHeight + BORDER_SIZE;
        int alpha = (int) ease( dialogue.ticksLeft / FADE_THRESHOLD, 0f, 0xFF);
        int move = (int) ease( (dialogue.ticksLeft - MOVE_END_THRESHOLD) / (MOVE_THRESHOLD-MOVE_END_THRESHOLD), renderer.fontHeight + BORDER_SIZE * 2, 0f);
        if (dialogues.size() > 1) {
            y = y - move;
        }
        fairyAlpha = alpha;

        InventoryScreen.drawEntity(context, x-c-BORDER_SIZE-1, y-BORDER_SIZE+1, x-1, y+c-1, 26, -0.32f, (x-c-BORDER_SIZE-2+1 + x-2+1) / 2.0f, (y-BORDER_SIZE+1 + y+c-1) / 2.0f, fairy);

        fairyAlpha = 0x100;

        alpha <<= 3*8;


        context.fill(-BORDER_SIZE + x-c-BORDER_SIZE+2, -BORDER_SIZE + y, width + x + BORDER_SIZE, renderer.fontHeight + BORDER_SIZE + y, alpha); //argb my beloved
        context.drawText(renderer, dialogue.text, x, y, 0xA020F0 + alpha, false);
    }
}


