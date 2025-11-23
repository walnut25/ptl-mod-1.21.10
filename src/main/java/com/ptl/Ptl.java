package com.ptl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ptl implements ModInitializer {
	public static final String MOD_ID = "ptl";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// ==========================================
	// 1. 定义实体
	// ==========================================
	public static final Identifier PTL_ID = Identifier.of(MOD_ID, "peng_tie_lin");
	public static final RegistryKey<EntityType<?>> PTL_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, PTL_ID);

	public static final EntityType<PtlEntity> PTL_ENTITY = Registry.register(
			Registries.ENTITY_TYPE,
			PTL_ID,
			EntityType.Builder.create(PtlEntity::new, SpawnGroup.CREATURE)
					.dimensions(0.6f, 1.8f)
					.build(PTL_KEY)
	);

	// ==========================================
	// 2. 定义声音 (闲置 + 受伤)
	// ==========================================
	// 闲置声音 ID: ptl:entity.peng_tie_lin.ambient
	public static final Identifier PTL_AMBIENT_ID = Identifier.of(MOD_ID, "entity.peng_tie_lin.ambient");
	public static final SoundEvent PTL_AMBIENT_EVENT = SoundEvent.of(PTL_AMBIENT_ID);

	// 受伤声音 ID: ptl:entity.peng_tie_lin.hurt
	public static final Identifier PTL_HURT_ID = Identifier.of(MOD_ID, "entity.peng_tie_lin.hurt");
	public static final SoundEvent PTL_HURT_EVENT = SoundEvent.of(PTL_HURT_ID);

	// ==========================================
	// 3. 定义物品 (刷怪蛋 + 大便)
	// ==========================================
	public static final Item PTL_SPAWN_EGG = new PtlSpawnEggItem(
			PTL_ENTITY,
			new Item.Settings()
					.registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "ptl_spawn_egg")))
	);

	public static final Item POOP = new Item(new Item.Settings()
			.registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "poop")))
	);

	@Override
	public void onInitialize() {
		// 1. 绑定实体属性 (血量、速度、吸引距离)
		FabricDefaultAttributeRegistry.register(PTL_ENTITY, PtlEntity.createPtlAttributes());

		// 2. 注册物品
		Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "ptl_spawn_egg"), PTL_SPAWN_EGG);
		Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "poop"), POOP);

		// 3. 注册声音 (关键步骤)
		Registry.register(Registries.SOUND_EVENT, PTL_AMBIENT_ID, PTL_AMBIENT_EVENT);
		Registry.register(Registries.SOUND_EVENT, PTL_HURT_ID, PTL_HURT_EVENT);

		// 4. 添加到创造模式物品栏
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> content.add(PTL_SPAWN_EGG));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> content.add(POOP));

		// ==========================================
		// 🌍 5. 自然生成配置
		// ==========================================

		// 注册生成规则：必须在草地上(ON_GROUND) + 光照充足
		SpawnRestriction.register(
				PTL_ENTITY,
				SpawnLocationTypes.ON_GROUND,
				Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
				AnimalEntity::isValidNaturalSpawn
		);

		// 添加到生物群系：权重 50 (很容易找到)
		BiomeModifications.addSpawn(
				BiomeSelectors.foundInOverworld(),
				SpawnGroup.CREATURE,
				PTL_ENTITY, // 【修改点】这里改成 PTL_ENTITY (直接传实体对象)
				50, 2, 4
		);
		LOGGER.info("彭铁林 (PTL) 模组 - 所有功能已就绪！");
	}

	// 自定义刷怪蛋内部类 (修复构造函数问题)
	public static class PtlSpawnEggItem extends SpawnEggItem {
		public PtlSpawnEggItem(EntityType<?> type, Item.Settings settings) {
			super(settings);
		}
		@Override
		public EntityType<?> getEntityType(net.minecraft.item.ItemStack stack) {
			return Ptl.PTL_ENTITY;
		}
	}
}