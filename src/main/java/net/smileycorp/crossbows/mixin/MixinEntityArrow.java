package net.smileycorp.crossbows.mixin;

import com.google.common.collect.Sets;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.smileycorp.crossbows.common.CrossbowsContent;
import net.smileycorp.crossbows.common.entities.ICrossbowArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Set;

@Mixin(EntityArrow.class)
public abstract class MixinEntityArrow extends Entity implements ICrossbowArrow {
    
    @Shadow
    public Entity shootingEntity;
    @Nullable
    private IntOpenHashSet piercingIgnoreEntityIds;
    @Nullable
    private Set<Class<? extends Entity>> piercedAndKilledEntities;
    
    private boolean shotFromCrossbow = false;
    private byte pierceLevel = 0;
    
    private RayTraceResult result;
    
    public MixinEntityArrow(World worldIn) {
        super(worldIn);
    }
    
    @Override
    public void setShotFromCrossbow(boolean crossbow) {
        shotFromCrossbow = true;
    }
    
    @Override
    public void setPierceLevel(byte level) {
        pierceLevel = level;
    }
    
    @Override
    public boolean shotFromCrossbow() {
        return shotFromCrossbow;
    }
    
    @Override
    public byte getPierceLevel() {
        return pierceLevel;
    }
    
    @Inject(method = "writeEntityToNBT", at = @At("HEAD"), cancellable = true)
    public void writeEntityToNBT(NBTTagCompound compound, CallbackInfo ci) {
        compound.setByte("PierceLevel", getPierceLevel());
        compound.setBoolean("ShotFromCrossbow", shotFromCrossbow());
    }
    
    @Inject(method = "readEntityFromNBT", at = @At("HEAD"), cancellable = true)
    public void readEntityFromNBT(NBTTagCompound compound, CallbackInfo ci) {
        if (compound.hasKey("PierceLevel")) setPierceLevel(compound.getByte("PierceLevel"));
        if (compound.hasKey("ShotFromCrossbow")) setShotFromCrossbow(compound.getBoolean("ShotFromCrossbow"));
    }
    
    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/EntityArrow;setDead()V"))
    public void onHit$HEAD(RayTraceResult rayTraceResult, CallbackInfo callback) {
        if (getPierceLevel() > 0 && rayTraceResult.typeOfHit == RayTraceResult.Type.ENTITY) result = rayTraceResult;
    }
    
    @WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/EntityArrow;setDead()V"))
    public void onHit$setDead(EntityArrow instance, Operation<Void> original) {
        if (getPierceLevel() > 0 && result != null && result.entityHit != null) {
            if (piercingIgnoreEntityIds == null) piercingIgnoreEntityIds = new IntOpenHashSet(5);
            if (piercedAndKilledEntities == null) piercedAndKilledEntities = Sets.newHashSetWithExpectedSize(5);
            if (piercingIgnoreEntityIds.size() >= getPierceLevel() + 1) {
                original.call(instance);
                return;
            }
            piercingIgnoreEntityIds.add(result.entityHit.getEntityId());
        } else original.call(instance);
    }
    
    @Inject(method = "onHit", at = @At(value = "TAIL"))
    public void onHit$TAIL(RayTraceResult rayTraceResult, CallbackInfo callback) {
        if (rayTraceResult.typeOfHit == RayTraceResult.Type.ENTITY && rayTraceResult.entityHit != null & !rayTraceResult.entityHit.isEntityAlive()
                && piercedAndKilledEntities != null && shootingEntity instanceof EntityPlayerMP) {
            if (piercedAndKilledEntities.size() < 5) piercedAndKilledEntities.add(rayTraceResult.entityHit.getClass());
            if (piercedAndKilledEntities.size() >= 5)
                CrossbowsContent.PIERCED_ENTITIES.trigger((EntityPlayerMP) shootingEntity);
        }
        result = null;
    }
    
    
}
