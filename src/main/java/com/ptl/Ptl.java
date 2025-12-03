package com.ptl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
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
import net.minecraft.server.MinecraftServer; // 导入 MinecraftServer
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Ptl implements ModInitializer {
	public static final String MOD_ID = "ptl";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// 0. 聊天队列
	public static final Map<UUID, PtlEntity> CHAT_QUEUE = new HashMap<>();

	// 1. 实体
	public static final Identifier PTL_ID = Identifier.of(MOD_ID, "peng_tie_lin");
	public static final RegistryKey<EntityType<?>> PTL_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, PTL_ID);
	public static final EntityType<PtlEntity> PTL_ENTITY = Registry.register(
			Registries.ENTITY_TYPE, PTL_ID,
			EntityType.Builder.create(PtlEntity::new, SpawnGroup.CREATURE).dimensions(0.6f, 1.8f).build(PTL_KEY)
	);

	// 2. 声音
	public static final Identifier PTL_AMBIENT_ID = Identifier.of(MOD_ID, "entity.peng_tie_lin.ambient");
	public static final SoundEvent PTL_AMBIENT_EVENT = SoundEvent.of(PTL_AMBIENT_ID);
	public static final Identifier PTL_HURT_ID = Identifier.of(MOD_ID, "entity.peng_tie_lin.hurt");
	public static final SoundEvent PTL_HURT_EVENT = SoundEvent.of(PTL_HURT_ID);

	// 3. 物品
	public static final Item PTL_SPAWN_EGG = new PtlSpawnEggItem(PTL_ENTITY, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "ptl_spawn_egg"))));
	public static final Item POOP = new Item(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "poop"))));

	@Override
	public void onInitialize() {
		PtlConfig.load();
		FabricDefaultAttributeRegistry.register(PTL_ENTITY, PtlEntity.createPtlAttributes());
		Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "ptl_spawn_egg"), PTL_SPAWN_EGG);
		Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "poop"), POOP);
		Registry.register(Registries.SOUND_EVENT, PTL_AMBIENT_ID, PTL_AMBIENT_EVENT);
		Registry.register(Registries.SOUND_EVENT, PTL_HURT_ID, PTL_HURT_EVENT);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> content.add(PTL_SPAWN_EGG));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> content.add(POOP));

		SpawnRestriction.register(PTL_ENTITY, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
		BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), SpawnGroup.CREATURE, PTL_ENTITY, 50, 2, 4);

		// ==========================================
		// 💬 聊天监听器
		// ==========================================
		ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> {
			if (CHAT_QUEUE.containsKey(sender.getUuid())) {
				PtlEntity target = CHAT_QUEUE.get(sender.getUuid());
				String text = message.getContent().getString();

				CHAT_QUEUE.remove(sender.getUuid());

				sender.sendMessage(net.minecraft.text.Text.of("§7[你对彭铁林说] " + text), false);
				sender.sendMessage(net.minecraft.text.Text.of("§7[彭铁林正在思考...]"), true);

				if (target != null && target.isAlive()) {
					GeminiHelper.chat(text, reply -> {
						// 【修复】通过 World 获取 Server，不再直接调用 sender.getServer()
						// 你的环境里 getEntityWorld() 是好使的，所以我们用它！
						MinecraftServer server = sender.getEntityWorld().getServer();

						if (server != null) {
							server.execute(() -> {
								net.minecraft.text.Text replyMsg = net.minecraft.text.Text.of("§a<彭铁林> §f" + reply);
								sender.sendMessage(replyMsg, false);
								target.playSound(PTL_AMBIENT_EVENT, 1.0F, 1.0F);
							});
						}
					});
				}
				return false;
			}
			return true;
		});

		LOGGER.info("彭铁林 (PTL) 模组 - 聊天功能已修复！");
	}

	public static class PtlSpawnEggItem extends SpawnEggItem {
		public PtlSpawnEggItem(EntityType<?> type, Item.Settings settings) { super(settings); }
		@Override public EntityType<?> getEntityType(net.minecraft.item.ItemStack stack) { return Ptl.PTL_ENTITY; }
	}
}