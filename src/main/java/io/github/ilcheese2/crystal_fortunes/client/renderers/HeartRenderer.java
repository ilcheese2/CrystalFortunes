package io.github.ilcheese2.crystal_fortunes.client.renderers;

import io.github.ilcheese2.crystal_fortunes.client.CrystalFortunesClient;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;

import java.util.Arrays;

public class HeartRenderer {
    private static class Heart {

        private float size = 0;
        private float sizeRate;
        public Heart(float a, float b, float c, int d, float e) {
            size = c;
            setValue(d*3, a);
            setValue(d*3+1, b);
            setValue(d*3+2, c*MAX_INITIAL_SIZE);
            sizeRate = 0.03f + e * 0.02f;
        }
        public Heart() {}

        public void setSize(float size, int i) {
            this.size = size;
            setValue(i*3+2, size);
        }
    }

    static Heart[] hearts = {new Heart(), new Heart(), new Heart(), new Heart(), new Heart()}; // sorry I don't know how to code
    static Random random = new CheckedRandom(TimeHelper.SECOND_IN_NANOS);
    static final float MAX_INITIAL_SIZE = 0.1f;
    static Matrix4f data = new Matrix4f();
    static int lastNew;
    static final Integer[] indices = {0,1,2,3,4};

    static void setValue(int i, float f) {
        data.set(i/4, i%4, f);
    }

    public static void initialize() {
        for (int i = 0; i < hearts.length; i++) {
            hearts[i] = new Heart(random.nextFloat(), random.nextFloat(), random.nextFloat(), i, random.nextFloat());
        }
        CrystalFortunesClient.LOVE_SHADER.setUniformValue("Hearts", data);
    }
    public static void increaseSize() {
        Arrays.sort(indices, (i, r)-> (int) (hearts[i].size));
        for (int i = 0; i < indices.length; i++) {
            int j = indices[i];
            hearts[j].setSize(hearts[j].sizeRate+hearts[j].size, j);
            if (hearts[j].size > 1 && lastNew > 20) {
                lastNew = 0;
                hearts[j] = new Heart(random.nextFloat(), random.nextFloat(), random.nextFloat(), j, random.nextFloat());
            }
            lastNew++;
        }
        CrystalFortunesClient.LOVE_SHADER.setUniformValue("Hearts", data);
        CrystalFortunesClient.LOVE_SHADER_2.setUniformValue("Hearts", data);
    }
}
