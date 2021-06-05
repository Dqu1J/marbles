package net.dodogang.marbles.mixin.hooks.client;

import net.dodogang.marbles.entity.ThrownRopeEntity;
import net.dodogang.marbles.init.MarblesEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Shadow private ClientWorld world;

    @Inject(method = "onEntitySpawn", at = @At("TAIL"))
    private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci) {
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();

        if (packet.getEntityTypeId() == MarblesEntities.THROWN_ROPE) {
            marbles_onProjectileEntitySpawn(packet, new ThrownRopeEntity(this.world, x, y, z), x, y, z);
        }
    }

    private void marbles_onProjectileEntitySpawn(EntitySpawnS2CPacket packet, ProjectileEntity entity, double x, double y, double z) {
        entity.updateTrackedPosition(x, y, z);
        entity.refreshPositionAfterTeleport(x, y, z);
        entity.setPitch((float)(packet.getPitch() * 360) / 256.0f);
        entity.setYaw((float)(packet.getYaw() * 360) / 256.0f);
        entity.setUuid(packet.getUuid());

        int entityId = packet.getId();
        entity.setEntityId(entityId);
        this.world.addEntity(entityId, entity);
    }
}
