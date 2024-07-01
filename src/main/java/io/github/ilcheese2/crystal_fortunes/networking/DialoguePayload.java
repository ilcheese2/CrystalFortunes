package io.github.ilcheese2.crystal_fortunes.networking;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.github.ilcheese2.crystal_fortunes.CrystalFortunes;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DialoguePayload(String translate, boolean random) implements CustomPayload {
    public static final CustomPayload.Id<DialoguePayload> DIALOGUE_ID = new CustomPayload.Id<>(Identifier.of(CrystalFortunes.MODID, "dialogue"));
    public static final PacketCodec<ByteBuf, DialoguePayload> CODEC = CustomPayload.codecOf(DialoguePayload::write, DialoguePayload::create);

    private static final PacketCodec<ByteBuf, Pair<String, Boolean>> DIALOGUE_CODEC = PacketCodecs.codec(Codec.pair(Codec.STRING.fieldOf("dialogue").codec(), Codec.BOOL.fieldOf("random").codec()));
    static DialoguePayload create(ByteBuf buf) {
        var pair = DIALOGUE_CODEC.decode(buf);
        return new DialoguePayload(pair.getFirst(), pair.getSecond());
    }

    private void write(ByteBuf buf) {
        DIALOGUE_CODEC.encode(buf, new Pair<>(translate, random));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return DIALOGUE_ID;
    }
}
