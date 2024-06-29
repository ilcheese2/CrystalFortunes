package io.github.ilcheese2.crystal_fortunes.networking;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record UpdateWheelPayload(Pair<Float, Float> roll) implements CustomPayload {
    public static final CustomPayload.Id<UpdateWheelPayload> UPDATE_WHEEL_ID = new CustomPayload.Id<>(Identifier.of(CrystalFortunes.MODID, "update_wheel"));
    public static final PacketCodec<ByteBuf, UpdateWheelPayload> CODEC = CustomPayload.codecOf(UpdateWheelPayload::write, UpdateWheelPayload::new);

    private static final PacketCodec<ByteBuf, Pair<Float, Float>> ROLL_CODEC = PacketCodecs.codec(Codec.pair(Codec.FLOAT.fieldOf("roll").codec(), Codec.FLOAT.fieldOf("previousRoll").codec()));
    UpdateWheelPayload(ByteBuf buf) {
        this( ROLL_CODEC.decode(buf));
    }

    private void write(ByteBuf buf) {
        ROLL_CODEC.encode(buf, roll);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return UPDATE_WHEEL_ID;
    }
}
