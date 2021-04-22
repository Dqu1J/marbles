package net.dodogang.marbles;

import net.dodogang.marbles.client.particle.PinkSaltParticle;
import net.dodogang.marbles.client.render.entity.BouncerEntityRenderer;
import net.dodogang.marbles.client.render.entity.PinkSaltCubeEntityRenderer;
import net.dodogang.marbles.entity.BouncerEntity;
import net.dodogang.marbles.init.MarblesBlocksClient;
import net.dodogang.marbles.init.MarblesEntities;
import net.dodogang.marbles.init.MarblesParticles;
import net.dodogang.marbles.network.MarblesNetwork;
import net.dodogang.marbles.network.MarblesNetworkingConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class MarblesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry pfrInstance = ParticleFactoryRegistry.getInstance();
        pfrInstance.register(MarblesParticles.PINK_SALT, PinkSaltParticle.Factory::new);

        EntityRendererRegistry errInstance = EntityRendererRegistry.INSTANCE;
        errInstance.register(MarblesEntities.BOUNCER, BouncerEntityRenderer::new);
        errInstance.register(MarblesEntities.PINK_SALT_CUBE, PinkSaltCubeEntityRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(MarblesNetworkingConstants.BOUNCER_HIT_PLAYER_SHIELD_PACKET_ID, (client, handler, buf, responseSender) -> {
            int attackerId = buf.readInt();
            UUID playerUUID = buf.readUuid();

            if (client.world != null) {
                client.execute(() -> {
                    Entity attacker = client.world.getEntityById(attackerId);
                    PlayerEntity player = client.world.getPlayerByUuid(playerUUID);

                    if (attacker instanceof BouncerEntity && player != null) {
                        ((BouncerEntity) attacker).tryThrowEntity(player, true);
                    }
                });
            }
        });

        MarblesBlocksClient.init();
        MarblesNetwork.initClient();
    }

    public static Identifier texture(String path) {
        return new Identifier(Marbles.MOD_ID, "textures/" + path + ".png");
    }
}
