package io.github.ilcheese2.crystal_fortunes;

import io.github.ilcheese2.crystal_fortunes.items.CrystalHonk;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class CrystalFortunesDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(HonkTagGenerator::new);
    }

    private static class HonkTagGenerator extends FabricTagProvider.ItemTagProvider {

        public HonkTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
            super(output, completableFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            getOrCreateTagBuilder(TagKey.of(RegistryKeys.ITEM, Identifier.of("honque", "funnies"))).addOptional(Identifier.of(CrystalFortunes.MODID, "the_crystal_funny"));
        }
    }
}
