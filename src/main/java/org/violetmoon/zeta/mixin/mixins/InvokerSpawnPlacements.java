package org.violetmoon.zeta.mixin.mixins;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpawnPlacements.class)
public interface InvokerSpawnPlacements {

    @Invoker("register")
    static <T extends Mob> void zeta$register(EntityType<T> entity, SpawnPlacementType type, Heightmap.Types heightMap, SpawnPlacements.SpawnPredicate<T> predicate) {
        throw new RuntimeException();
    }
}
