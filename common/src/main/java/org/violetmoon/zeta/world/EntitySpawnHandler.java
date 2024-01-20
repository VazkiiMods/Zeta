package org.violetmoon.zeta.world;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.config.type.EntitySpawnConfig;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZConfigChanged;
import org.violetmoon.zeta.item.ZetaSpawnEggItem;
import org.violetmoon.zeta.mixin.mixins.AccessorSpawnPlacements;
import org.violetmoon.zeta.module.ZetaModule;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;

public class EntitySpawnHandler {

	public List<TrackedSpawnConfig> trackedSpawnConfigs = new LinkedList<>();
	private final Zeta zeta;
	
	public EntitySpawnHandler(Zeta zeta) {
		this.zeta = zeta;
	}

	public <T extends Mob> void registerSpawn(EntityType<T> entityType, MobCategory classification, Type placementType, Heightmap.Types heightMapType, SpawnPredicate<T> placementPredicate, EntitySpawnConfig config) {
		AccessorSpawnPlacements.zeta$register(entityType, placementType, heightMapType, placementPredicate);

		track(entityType, classification, config, false);
	}

	public <T extends Mob> void track(EntityType<T> entityType, MobCategory classification, EntitySpawnConfig config, boolean secondary) {
		trackedSpawnConfigs.add(new TrackedSpawnConfig(entityType, classification, config, secondary));
	}

	public void addEgg(ZetaModule module, EntityType<? extends Mob> entityType, int color1, int color2, EntitySpawnConfig config) {
		addEgg(entityType, color1, color2, module, config::isEnabled);
	}

	public void addEgg(EntityType<? extends Mob> entityType, int color1, int color2, ZetaModule module, BooleanSupplier enabledSupplier) {
		new ZetaSpawnEggItem(() -> entityType, color1, color2, zeta.registry.getRegistryName(entityType, BuiltInRegistries.ENTITY_TYPE) + "_spawn_egg", module,
				new Item.Properties())
				.setCondition(enabledSupplier);
	}

	@LoadEvent
	public void refresh(ZConfigChanged event) {
		for(TrackedSpawnConfig c : trackedSpawnConfigs)
			c.refresh();
	}

	public static class TrackedSpawnConfig {

		public final EntityType<?> entityType;
		public final MobCategory classification;
		public final EntitySpawnConfig config;
		public final boolean secondary;
		MobSpawnSettings.SpawnerData entry;

		TrackedSpawnConfig(EntityType<?> entityType, MobCategory classification, EntitySpawnConfig config, boolean secondary) {
			this.entityType = entityType;
			this.classification = classification;
			this.config = config;
			this.secondary = secondary;
			refresh();
		}

		private void refresh() {
			entry = new MobSpawnSettings.SpawnerData(entityType, config.spawnWeight, Math.min(config.minGroupSize, config.maxGroupSize), Math.max(config.minGroupSize, config.maxGroupSize));
		}
		
		public MobSpawnSettings.SpawnerData getEntry() {
			return entry;
		}

	}

}
