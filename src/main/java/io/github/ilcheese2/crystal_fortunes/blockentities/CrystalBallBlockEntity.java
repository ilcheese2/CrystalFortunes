package io.github.ilcheese2.crystal_fortunes.blockentities;

import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrystalBallBlockEntity extends BlockEntity {
    public CrystalBallBlockEntity(BlockPos pos, BlockState state) {
        super(CrystalFortunes.CRYSTAL_BALL_BLOCK_ENTITY, pos, state);
    }

    @Override
    public BlockEntityType<CrystalBallBlockEntity> getType() {
        return CrystalFortunes.CRYSTAL_BALL_BLOCK_ENTITY;
    }
}
