package net.smileycorp.crossbows.common.item;

import com.google.common.collect.Lists;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemFirework;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.smileycorp.crossbows.common.ConfigHandler;
import net.smileycorp.crossbows.common.Constants;
import net.smileycorp.crossbows.common.CrossbowsContent;
import net.smileycorp.crossbows.common.entities.ICrossbowArrow;
import net.smileycorp.crossbows.common.entities.IFireworksProjectile;

import javax.vecmath.Vector3f;
import java.util.List;
import java.util.Random;

public class ItemCrossbow extends Item {
    
    private final int drawSpeed;
    private final float damageMultiplier;
    private final int enchantability;
    private boolean startSoundPlayed = false;
    private boolean midLoadSoundPlayed = false;
    
    public ItemCrossbow(String modid, String name, int durability, float damageMultiplier, int drawSpeed, int enchantability) {
        this.damageMultiplier = damageMultiplier;
        this.drawSpeed = drawSpeed;
        this.enchantability = enchantability;
        setUnlocalizedName(modid + "." + name.replace("_", ""));
        setRegistryName(new ResourceLocation(modid, name));
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.COMBAT);
        setMaxDamage(durability);
        addPropertyOverride(new ResourceLocation(modid, "pull"), (stack, worldIn, entityIn) -> entityIn == null || isCharged(stack) ? 0 :
                (float) (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / ((float) getChargeDuration(stack)));
        addPropertyOverride(new ResourceLocation(modid, "pulling"), (stack, worldIn, entityIn) ->
                entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack && !isCharged(stack) ? 1 : 0);
        addPropertyOverride(new ResourceLocation(modid, "charged"), (stack, worldIn, entityIn) -> isCharged(stack) ? 1 : 0);
        addPropertyOverride(new ResourceLocation(modid, "firework"), (stack, worldIn, entityIn) -> containsChargedProjectile(stack, Items.FIREWORKS) ? 1 : 0);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (isCharged(stack)) {
            performShooting(world, player, stack, containsChargedProjectile(stack, Items.FIREWORKS) ? 1.6f : 3.15f, 1);
            setCharged(stack, false);
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
        }
        if (getProjectile(player).isEmpty()) return ActionResult.newResult(EnumActionResult.FAIL, stack);
        if (!isCharged(stack)) {
            this.startSoundPlayed = false;
            this.midLoadSoundPlayed = false;
            player.setActiveHand(hand);
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }
    
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
        int duration = getMaxItemUseDuration(stack) - timeLeft;
        float charge = entity instanceof EntityPlayer ? getPowerForTime(duration, stack) : 1f;
        if (charge < 1.0F || isCharged(stack) && tryLoadProjectiles(entity, stack)) return;
        setCharged(stack, true);
        world.playSound(null, entity.posX, entity.posX, entity.posZ, Constants.CROSSBOW_LOADING_END, entity.getSoundCategory(), 1, 1f / (world.rand.nextFloat() * 0.5f + 1) + 0.2f);
    }
    
    private boolean tryLoadProjectiles(EntityLivingBase entity, ItemStack stack) {
        int i = EnchantmentHelper.getEnchantmentLevel(CrossbowsContent.MULTISHOT, stack);
        int j = i == 0 ? 1 : 3;
        boolean creative = entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode;
        ItemStack itemstack = getProjectile(entity);
        ItemStack itemstack1 = itemstack.copy();
        for (int k = 0; k < j; ++k) {
            if (k > 0) itemstack = itemstack1.copy();
            if (itemstack.isEmpty() && creative) {
                itemstack = new ItemStack(Items.ARROW);
                itemstack1 = itemstack.copy();
            }
            if (!loadProjectile(entity, stack, itemstack, k > 0, creative)) return false;
        }
        return true;
    }
    
    private boolean loadProjectile(EntityLivingBase entity, ItemStack stack, ItemStack ammo, boolean consumed, boolean creative) {
        if (ammo.isEmpty()) return false;
        else {
            boolean dontConsume = (creative && ammo.getItem() instanceof ItemArrow) || (ConfigHandler.bowEnchantments && ammo.getItem() == Items.ARROW
                    && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0);
            ItemStack itemstack;
            if (!dontConsume && !creative && !consumed) {
                itemstack = ammo.splitStack(1);
                if (ammo.isEmpty() && entity instanceof EntityPlayer)
                    ((EntityPlayer) entity).inventory.deleteStack(ammo);
            } else itemstack = ammo.copy();
            addChargedProjectile(stack, itemstack);
            return true;
        }
    }
    
    public static boolean isCharged(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) nbt = new NBTTagCompound();
        return nbt != null && nbt.getBoolean("Charged");
    }
    
    public static void setCharged(ItemStack stack, boolean chargedIn) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) nbt = new NBTTagCompound();
        nbt.setBoolean("Charged", chargedIn);
        stack.setTagCompound(nbt);
    }
    
    private static void addChargedProjectile(ItemStack stack, ItemStack projectile) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) nbt = new NBTTagCompound();
        NBTTagList taglist;
        if (nbt.hasKey("ChargedProjectiles", 9)) taglist = nbt.getTagList("ChargedProjectiles", 10);
        else taglist = new NBTTagList();
        NBTTagCompound stacknbt = new NBTTagCompound();
        projectile.writeToNBT(stacknbt);
        taglist.appendTag(stacknbt);
        nbt.setTag("ChargedProjectiles", taglist);
        stack.setTagCompound(nbt);
    }
    
    private static List<ItemStack> getChargedProjectiles(ItemStack stack) {
        List<ItemStack> list = Lists.newArrayList();
        NBTTagCompound NBTTagCompound = stack.getTagCompound();
        if (NBTTagCompound != null && NBTTagCompound.hasKey("ChargedProjectiles", 9)) {
            NBTTagList taglist = NBTTagCompound.getTagList("ChargedProjectiles", 10);
            if (taglist != null) taglist.forEach(t -> list.add(new ItemStack((NBTTagCompound) t)));
        }
        return list;
    }
    
    private static void clearChargedProjectiles(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null) nbt.setTag("ChargedProjectiles", new NBTTagList());
    }
    
    public static boolean containsChargedProjectile(ItemStack stack, Item item) {
        return getChargedProjectiles(stack).stream().anyMatch(proj -> proj.getItem() == item);
    }
    
    private void shootProjectile(World world, EntityLivingBase entity, ItemStack stack, ItemStack ammo, float p_40900_, boolean noPickup, float p_40902_, float p_40903_, float offset) {
        if (world.isRemote) return;
        boolean isFirework = ammo.getItem() == Items.FIREWORKS;
        Entity projectile;
        if (isFirework) {
            projectile = new EntityFireworkRocket(world, entity.posX, entity.posY + entity.getEyeHeight() - 0.15F, entity.posZ, ammo);
            ((IFireworksProjectile) projectile).setOwner(entity);
            ((IFireworksProjectile) projectile).setShotAtAngle();
        } else {
            projectile = getArrow(world, entity, stack, ammo);
            if (noPickup) ((EntityArrow) projectile).pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
        }
        if (entity instanceof EntityLiving) {
            EntityLivingBase target = ((EntityLiving) entity).getAttackTarget();
            double d0 = target.posX - entity.posX;
            double d1 = target.posZ - entity.posZ;
            double d2 = Math.sqrt(d0 * d0 + d1 * d1);
            double d3 = target.posY - entity.posY + d2 * (double) 0.2F;
            Vector3f vector3f = getProjectileShotVector(new Vec3d(d0, d3, d1));
            ((IProjectile) projectile).shoot(vector3f.x, vector3f.y, vector3f.z, 1.6f, (float) (14 - entity.world.getDifficulty().getDifficultyId() * 4));
            entity.playSound(Constants.CROSSBOW_SHOOT, 1.0F, 1.0F / (entity.getRNG().nextFloat() * 0.4F + 0.8F));
        } else {
            Vec3d look = entity.getLook(1.0F);
            float angle = offset * ((float) Math.PI / 180F);
            double s = (float) Math.sin(angle);
            float c = (float) Math.cos(angle);
            ((IProjectile) projectile).shoot(look.x * c - look.z * s, look.y, look.x * s + look.z * c, p_40902_, p_40903_);
        }
        stack.damageItem(isFirework ? 3 : 1, entity);
        world.spawnEntity(projectile);
        world.playSound(null, entity.posX, entity.posY, entity.posZ, Constants.CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, p_40900_);
    }
    
    protected static Vector3f getProjectileShotVector(Vec3d direction) {
        Vec3d vec3 = direction.normalize();
        Vec3d vec31 = vec3.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D));
        Vector3f angle = new Vector3f((float) vec31.x, (float) vec31.y, (float) vec31.z);
        Vector3f vector3f = new Vector3f((float) vec3.x, (float) vec3.y, (float) vec3.z);
        vector3f.angle(angle);
        return vector3f;
    }
    
    private EntityArrow getArrow(World world, EntityLivingBase entity, ItemStack crossbow, ItemStack ammo) {
        ItemArrow arrowitem = (ItemArrow) (ammo.getItem() instanceof ItemArrow ? ammo.getItem() : Items.ARROW);
        EntityArrow arrow = arrowitem.createArrow(world, ammo, entity);
        arrow.setDamage(arrow.getDamage() * damageMultiplier);
        if (entity instanceof EntityPlayer) arrow.setIsCritical(true);
        ICrossbowArrow crossbowArrow = (ICrossbowArrow) arrow;
        crossbowArrow.setShotFromCrossbow(true);
        int i = EnchantmentHelper.getEnchantmentLevel(CrossbowsContent.PIERCING, crossbow);
        if (i > 0) crossbowArrow.setPierceLevel((byte) i);
        if (ConfigHandler.bowEnchantments) {
            int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, crossbow);
            if (power > 0) arrow.setDamage(arrow.getDamage() + (double) power * 0.5 + 0.5);
            int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, crossbow);
            if (punch > 0) arrow.setKnockbackStrength(punch);
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, crossbow) > 0) arrow.setFire(100);
        }
        return arrow;
    }
    
    public static void performShooting(World world, EntityLivingBase entity, ItemStack stack, float power, float inaccuracy) {
        if (!(stack.getItem() instanceof ItemCrossbow)) return;
        if (entity instanceof EntityPlayer && ForgeEventFactory.onArrowLoose(stack, world, (EntityPlayer) entity, 1, true) < 0)
            return;
        ItemCrossbow crossbow = (ItemCrossbow) stack.getItem();
        List<ItemStack> list = getChargedProjectiles(stack);
        float[] afloat = crossbow.getShotPitches(entity.getRNG());
        for (int i = 0; i < list.size(); ++i) {
            ItemStack ammo = list.get(i);
            boolean noPickup = entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative() ||
                    (ConfigHandler.bowEnchantments && ammo.getItem() == Items.ARROW
                            && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0);
            if (ammo.isEmpty()) continue;
            if (i == 0)
                crossbow.shootProjectile(world, entity, stack, ammo, afloat[i], noPickup, power, inaccuracy, 0.0F);
            else if (i == 1)
                crossbow.shootProjectile(world, entity, stack, ammo, afloat[i], true, power, inaccuracy, -10.0F);
            else if (i == 2)
                crossbow.shootProjectile(world, entity, stack, ammo, afloat[i], true, power, inaccuracy, 10.0F);
        }
        crossbow.onCrossbowShot(world, entity, stack);
    }
    
    private float[] getShotPitches(Random rand) {
        boolean flag = rand.nextBoolean();
        return new float[]{1.0F, getRandomShotPitch(flag, rand), getRandomShotPitch(!flag, rand)};
    }
    
    private float getRandomShotPitch(boolean flag, Random rand) {
        return 1f / (rand.nextFloat() * 0.5f + 1.8f) + (flag ? 0.63f : 0.43f);
    }
    
    private void onCrossbowShot(World world, EntityLivingBase entity, ItemStack stack) {
        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            if (!world.isRemote) CrossbowsContent.SHOT_CROSSBOW.trigger(player);
            player.addStat(StatList.getObjectUseStats(stack.getItem()));
        }
        clearChargedProjectiles(stack);
    }
    
    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
        if (entity.world.isRemote) return;
        int i = EnchantmentHelper.getEnchantmentLevel(CrossbowsContent.QUICK_CHARGE, stack);
        SoundEvent startSound = getStartSound(0);
        SoundEvent middleSound = i == 0 ? Constants.CROSSBOW_LOADING_MIDDLE : null;
        float f = (float) (stack.getMaxItemUseDuration() - count) / (float) getChargeDuration(stack);
        if (f < 0.2f) {
            startSoundPlayed = false;
            midLoadSoundPlayed = false;
        }
        if (f >= 0.2f && !startSoundPlayed) {
            startSoundPlayed = true;
            entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, startSound, entity instanceof EntityPlayer ?
                    SoundCategory.PLAYERS : SoundCategory.HOSTILE, 0.5f, 1);
        }
        if (f >= 0.5f && middleSound != null && !midLoadSoundPlayed) {
            midLoadSoundPlayed = true;
            entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, middleSound, entity instanceof EntityPlayer ?
                    SoundCategory.PLAYERS : SoundCategory.HOSTILE, 0.5f, 1);
        }
    }
    
    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return getChargeDuration(stack) + 3;
    }
    
    public static int getChargeDuration(ItemStack stack) {
        int i = EnchantmentHelper.getEnchantmentLevel(CrossbowsContent.QUICK_CHARGE, stack);
        int drawSpeed = stack.getItem() instanceof ItemCrossbow ? ((ItemCrossbow) stack.getItem()).drawSpeed : 25;
        return drawSpeed - (int) ((float) drawSpeed * 0.2f) * i;
    }
    
    private SoundEvent getStartSound(int quick_charge_level) {
        switch (quick_charge_level) {
            case 1:
                return Constants.CROSSBOW_QUICK_CHARGE_1;
            case 2:
                return Constants.CROSSBOW_QUICK_CHARGE_2;
            case 3:
                return Constants.CROSSBOW_QUICK_CHARGE_3;
            default:
                return Constants.CROSSBOW_LOADING_START;
        }
    }
    
    private static float getPowerForTime(int duration, ItemStack stack) {
        return Math.min((float) duration / (float) getChargeDuration(stack), 1);
    }
    
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        List<ItemStack> list = getChargedProjectiles(stack);
        if (!isCharged(stack) || list.isEmpty()) return;
        ItemStack itemstack = list.get(0);
        tooltip.add(new TextComponentTranslation("item.crossbows.crossbow.projectile").getFormattedText() + " " + (itemstack.getDisplayName()));
        if (!flag.isAdvanced() || itemstack.getItem() != Items.FIREWORKS) return;
        List<String> firework_props = Lists.newArrayList();
        Items.FIREWORKS.addInformation(itemstack, world, firework_props, flag);
        if (firework_props.isEmpty()) return;
        for (int i = 0; i < firework_props.size(); ++i)
            firework_props.set(i, (new TextComponentString("  " + firework_props.get(i))
                    .setStyle(new Style().setColor(TextFormatting.GRAY)).getFormattedText()));
        tooltip.addAll(firework_props);
    }
    
    @Override
    public int getItemEnchantability() {
        return enchantability;
    }
    
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }
    
    protected static ItemStack getProjectile(EntityLivingBase entity) {
        if (isAmmo(entity.getHeldItem(EnumHand.OFF_HAND))) return entity.getHeldItem(EnumHand.OFF_HAND);
        else if (isAmmo(entity.getHeldItem(EnumHand.MAIN_HAND))) return entity.getHeldItem(EnumHand.MAIN_HAND);
        if (!(entity instanceof EntityPlayer)) return new ItemStack(Items.ARROW);
        EntityPlayer player = (EntityPlayer) entity;
        if (player.isCreative()) return new ItemStack(Items.ARROW);
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = player.inventory.getStackInSlot(i);
            if (isAmmo(itemstack)) return itemstack;
        }
        return ItemStack.EMPTY;
    }
    
    public static boolean isAmmo(ItemStack stack) {
        return stack.getItem() instanceof ItemArrow || stack.getItem() instanceof ItemFirework;
    }
    
}
