package io.github.ilcheese2.crystal_fortunes.camera;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public class ServerCameraHandler {
    public static void setCameraEntity(ServerPlayerEntity player, Entity entity) {
        CameraData.storePlayerData(player);
        player.changeGameMode(GameMode.SPECTATOR);
        player.setCameraEntity(entity);
    }
}
