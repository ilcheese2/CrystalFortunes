package io.github.ilcheese2.crystal_fortunes.networking;

import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.github.ilcheese2.crystal_fortunes.predictions.Prediction;
import io.github.ilcheese2.crystal_fortunes.predictions.PredictionType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PredictionPayload(Prediction prediction) implements CustomPayload {
    public static final CustomPayload.Id<PredictionPayload> PREDICTION_ID = new CustomPayload.Id<>(Identifier.of(CrystalFortunes.MODID, "prediction"));
    public static final PacketCodec<ByteBuf, PredictionPayload> CODEC = CustomPayload.codecOf(PredictionPayload::write, PredictionPayload::new);

    PredictionPayload(ByteBuf buf) {
        this(PacketCodecs.codec(PredictionType.CODEC).decode(buf));
    }

    private void write(ByteBuf buf) {
        PacketCodecs.codec(PredictionType.CODEC).encode(buf, prediction);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PREDICTION_ID;
    }
}
