package net.smileycorp.crossbows.common.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.smileycorp.crossbows.common.entities.IFireworksProjectile;

public class FireworksDispenseBehaviour extends BehaviorProjectileDispense {
    
    @Override
    public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
        World world = source.getWorld();
        IPosition iposition = BlockDispenser.getDispensePosition(source);
        EnumFacing enumfacing = source.getBlockState().getValue(BlockDispenser.FACING);
        IFireworksProjectile projectile = getProjectileEntity(world, iposition, stack);
        projectile.shoot(enumfacing.getFrontOffsetX(), enumfacing.getFrontOffsetY(), enumfacing.getFrontOffsetZ(),
                getProjectileVelocity(), getProjectileInaccuracy());
        projectile.setShotAtAngle();
        world.spawnEntity((Entity)projectile);
        stack.shrink(1);
        return stack;
    }
    
    @Override
    protected float getProjectileInaccuracy() {
        return 1;
    }
    
    @Override
    protected float getProjectileVelocity() {
        return 0.5f;
    }
    
    @Override
    protected void playDispenseSound(IBlockSource source) {
        source.getWorld().playEvent(1004, source.getBlockPos(), 0);
    }
    
    @Override
    protected IFireworksProjectile getProjectileEntity(World world, IPosition pos, ItemStack stack) {
        return (IFireworksProjectile) new EntityFireworkRocket(world, pos.getX(), pos.getY(), pos.getZ(), stack);
    }
    
}
