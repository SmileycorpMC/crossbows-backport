package net.smileycorp.crossbows.common.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.smileycorp.crossbows.common.CrossbowsContent;
import net.smileycorp.crossbows.common.item.ItemCrossbow;

import java.util.function.Consumer;

public class EntityAIAttackRangedCrossbow<T extends EntityLiving> extends EntityAIBase {

    private final T entity;
    private final Consumer<Boolean> chargeFunction;
    private final Runnable shootFunction;
    private final double speedModifier;
    private final float attackRadiusSqr;
    private State state = State.UNCHARGED;
    private int seeTime;
    private int attackDelay;
    private int updatePathDelay;

    public EntityAIAttackRangedCrossbow(T entity, double speed, float radius, Consumer<Boolean> chargeFunction, Runnable shootFunction) {
        this.entity = entity;
        this.speedModifier = speed;
        this.attackRadiusSqr = radius * radius;
        this.chargeFunction = chargeFunction;
        this.shootFunction = shootFunction;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        return entity.getAttackTarget() != null && entity.getAttackTarget().isEntityAlive() &&
                (entity.getHeldItemMainhand().getItem() == CrossbowsContent.CROSSBOW) || (entity.getHeldItemOffhand().getItem() == CrossbowsContent.CROSSBOW);
    }

    public void resetTask() {
        super.resetTask();
        entity.setAttackTarget(null);
        seeTime = 0;
        if (entity.getActiveItemStack().getItem() == CrossbowsContent.CROSSBOW) {
            ItemCrossbow.setCharged(entity.getActiveItemStack(), true);
            entity.resetActiveHand();
            chargeFunction.accept(false);
        }
    }

    @Override
    public void updateTask() {
        EntityLivingBase target = entity.getAttackTarget();
        if (target != null) {
            boolean flag = entity.getEntitySenses().canSee(target);
            boolean flag1 = this.seeTime > 0;
            if (flag != flag1) seeTime = 0;
            if (flag) seeTime++;
            else seeTime--;
            double d0 = entity.getDistanceSq(target);
            boolean flag2 = (d0 > (double)this.attackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;
            if (flag2) {
                if (updatePathDelay-- <= 0) {
                    entity.getNavigator().tryMoveToEntityLiving(target, state == State.UNCHARGED ? this.speedModifier : this.speedModifier * 0.5D);
                    updatePathDelay = entity.getRNG().nextInt(20) + 20;
                }
            } else {
                this.updatePathDelay = 0;
                entity.getNavigator().clearPath();
            }
            entity.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
            if (state == State.UNCHARGED) {
                if (!flag2) {
                    for (EnumHand hand : EnumHand.values()) if (entity.getHeldItem(hand).getItem() == CrossbowsContent.CROSSBOW) {
                        entity.setActiveHand(hand);
                        break;
                    }
                    state = State.CHARGING;
                    chargeFunction.accept(true);
                }
            } else if (state == State.CHARGING) {
                if (!entity.isHandActive()) state = State.UNCHARGED;
                int i = entity.getItemInUseCount();
                ItemStack itemstack = entity.getActiveItemStack();
                if (itemstack.getItem() == CrossbowsContent.CROSSBOW && i < -itemstack.getMaxItemUseDuration()) {
                    entity.resetActiveHand();
                    state = State.CHARGED;
                    itemstack.onPlayerStoppedUsing(entity.world, entity, 0);
                    attackDelay = 20 + entity.getRNG().nextInt(20);
                    chargeFunction.accept(false);
                }
            } else if (state == State.CHARGED) {
                if (attackDelay-- == 0) state = State.READY_TO_ATTACK;
            } else if (state == State.READY_TO_ATTACK && flag) {
                performCrossbowAttack(1f);
                ItemStack stack = ItemStack.EMPTY;
                for (EnumHand hand : EnumHand.values()) if (entity.getHeldItem(hand).getItem() == CrossbowsContent.CROSSBOW) {
                    stack = entity.getHeldItem(hand);
                    break;
                }
                ItemCrossbow.setCharged(stack, false);
                state = State.UNCHARGED;
            }
        }
    }
    
    protected void performCrossbowAttack(float distance) {
        ItemStack stack = ItemStack.EMPTY;
        for (EnumHand hand : EnumHand.values()) if (entity.getHeldItem(hand).getItem() == CrossbowsContent.CROSSBOW) {
            stack = entity.getHeldItem(hand);
            break;
        }
        if (stack.isEmpty()) return;
        ItemCrossbow.performShooting(entity.world, entity, stack, distance, (float)(14 - entity.getEntityWorld().getDifficulty().getDifficultyId() * 4));
        shootFunction.run();
    }

    enum State {
        UNCHARGED,
        CHARGING,
        CHARGED,
        READY_TO_ATTACK;
    }

}
