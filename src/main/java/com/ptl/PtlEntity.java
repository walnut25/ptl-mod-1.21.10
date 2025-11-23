package com.ptl;

import net.minecraft.entity.EntityType;
// 导入 ItemEntity，用于手动生成掉落物
import net.minecraft.entity.ItemEntity;
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

    // 【测试修改 1】把时间改短！
    // 60 tick = 3秒。测试完记得改回 6000 + random.nextInt(6000)
    public int poopTime = 60;

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
    protected SoundEvent getAmbientSound() {
        return Ptl.PTL_AMBIENT_EVENT;
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.entity.damage.DamageSource source) {
        return Ptl.PTL_HURT_EVENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return Ptl.PTL_HURT_EVENT;
    }

    @Override
    public void tick() {
        super.tick();
        // 使用你的环境能识别的方法获取世界
        World world = this.getEntityWorld();

        if (!world.isClient() && this.isAlive() && !this.isBaby()) {

            if (--this.poopTime <= 0) {
                this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

                // 【测试修改 2】打印一条日志到控制台，证明逻辑跑通了
                System.out.println("彭铁林拉屎了！位置: " + this.getBlockPos());

                // 【关键修改 3】使用万能方法生成掉落物
                // 创建一个物品实体，放在当前位置向上一点点 (y + 0.5)
                ItemEntity poopItem = new ItemEntity(world, this.getX(), this.getY() + 0.5, this.getZ(), new ItemStack(Ptl.POOP));
                // 给它一点随机速度，让它蹦出来
                poopItem.setVelocity((this.random.nextFloat() - 0.5F) * 0.2F, this.random.nextFloat() * 0.2F, (this.random.nextFloat() - 0.5F) * 0.2F);
                // 生成到世界上
                world.spawnEntity(poopItem);

                // 【测试修改 4】重置时间，继续用短时间测试
                this.poopTime = 600; // 3秒后再次拉屎
            }
        }
    }
}