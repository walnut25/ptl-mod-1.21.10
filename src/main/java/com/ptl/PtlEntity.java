package com.ptl;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PtlEntity extends AnimalEntity {

    public int poopTime = this.random.nextInt(6000) + 6000;

    public PtlEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createPtlAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 20.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.3f)
                .add(EntityAttributes.TEMPT_RANGE, 10.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.25));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(3, new TemptGoal(this, 1.1, Ingredient.ofItems(Ptl.POOP), false));
        this.goalSelector.add(4, new LookAtEntityGoal(this, net.minecraft.entity.player.PlayerEntity.class, 6.0F));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(Ptl.POOP);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return new PtlEntity(Ptl.PTL_ENTITY, world);
    }

    @Override
    protected SoundEvent getAmbientSound() { return Ptl.PTL_AMBIENT_EVENT; }
    @Override
    protected SoundEvent getHurtSound(net.minecraft.entity.damage.DamageSource source) { return Ptl.PTL_HURT_EVENT; }
    @Override
    protected SoundEvent getDeathSound() { return Ptl.PTL_HURT_EVENT; }

    @Override
    public void tick() {
        super.tick();
        // 【修复】使用 getEntityWorld 解决报错
        World world = this.getEntityWorld();

        if (!world.isClient() && this.isAlive() && !this.isBaby()) {
            if (--this.poopTime <= 0) {
                this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                if (world instanceof ServerWorld serverWorld) {
                    this.dropStack(serverWorld, new ItemStack(Ptl.POOP));
                }
                this.poopTime = this.random.nextInt(6000) + 6000;
            }
        }
    }

    // =====================================================
    // 🧠 交互逻辑：加入聊天队列
    // =====================================================
    @Override
    public net.minecraft.util.ActionResult interactMob(net.minecraft.entity.player.PlayerEntity player, net.minecraft.util.Hand hand) {
        if (hand == net.minecraft.util.Hand.MAIN_HAND) {

            // 1. 繁殖逻辑优先
            if (this.isBreedingItem(player.getStackInHand(hand))) {
                return super.interactMob(player, hand);
            }

            // 2. 聊天逻辑 (服务端)
            // 【修复】使用 getEntityWorld()
            if (!this.getEntityWorld().isClient()) {

                // 把玩家加入"待聊天名单"
                Ptl.CHAT_QUEUE.put(player.getUuid(), this);

                // 提示玩家
                player.sendMessage(net.minecraft.text.Text.of("§e[系统] §f请在聊天栏输入你要对彭铁林说的话..."), false);
            }

            return net.minecraft.util.ActionResult.SUCCESS;
        }
        return super.interactMob(player, hand);
    }
}