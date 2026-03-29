package net.smileycorp.crossbows.api.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.entity.EntityEvent;

import java.util.List;

public class CrossbowPierceEvent extends EntityEvent {

    private final EntityArrow arrow;
    private final RayTraceResult hitResult;
    private final List<Entity> piercedEntities;

    public CrossbowPierceEvent(Entity entity, EntityArrow arrow, RayTraceResult hitResult, List<Entity> piercedEntities) {
        super(entity);
        this.arrow = arrow;
        this.hitResult = hitResult;
        this.piercedEntities = piercedEntities;
    }

    public EntityArrow getArrow() {
        return arrow;
    }

    public RayTraceResult getHitResult() {
        return hitResult;
    }

    public List<Entity> getPiercedEntities() {
        return piercedEntities;
    }

}
