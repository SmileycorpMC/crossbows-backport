package net.smileycorp.crossbows.common.entities;

import net.minecraft.entity.item.EntityFireworkRocket;

public interface IFireworksDamage {
    
    void setFireworksEntity(EntityFireworkRocket projectile);
    
    boolean hasFireworksEntity();
    
    EntityFireworkRocket getFireworksEntity();
    
}
