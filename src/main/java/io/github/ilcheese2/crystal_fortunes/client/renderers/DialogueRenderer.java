package io.github.ilcheese2.crystal_fortunes.client.renderers;



import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayDeque;
import java.util.Deque;

public class DialogueRenderer {


    static final double DIALOGUE_X = 0.5;// in screen percent;
    static final double DIALOGUE_Y = 0.75;
    static final int BORDER_SIZE = 2;
    static final int DEFAULT_DISPLAY_TICKS = 50;

    static final int FADE_THRESHOLD = 10;

    static class Dialogue { // I miss c++
        Text text;
        int ticksLeft;

        Dialogue(Text text) {
            this.text = text;
            ticksLeft = DEFAULT_DISPLAY_TICKS;
        }
    }

    private final Deque<Dialogue> dialogues = new ArrayDeque<>();

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
            drawText(context, dialogues.getFirst());
        }
    }

    private void drawText(DrawContext context, Dialogue dialogue) {
        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
        int x = (int) (context.getScaledWindowWidth() * DIALOGUE_X) - renderer.getWidth(dialogue.text)/2;
        int y = (int) (context.getScaledWindowHeight() * DIALOGUE_Y) - renderer.fontHeight/2;
        int alpha = MathHelper.lerp(Math.min((float) dialogue.ticksLeft / FADE_THRESHOLD, 1.0f), 0, 0xFF);
        alpha <<= 3*8;
        context.fill(-BORDER_SIZE + x, -BORDER_SIZE + y, renderer.getWidth(dialogue.text) + x + BORDER_SIZE, renderer.fontHeight + BORDER_SIZE + y, alpha); //argb my beloved
        context.drawText(renderer, dialogue.text, x, y, 0xA020F0 + alpha, false);
    }
}


