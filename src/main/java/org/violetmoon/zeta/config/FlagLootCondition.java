package org.violetmoon.zeta.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;
import org.violetmoon.zeta.mod.ZetaMod;

import static org.violetmoon.zeta.config.ConfigFlagManager.FLAG_CONDITION_TYPE;

/**
 * @author WireSegal
 *         Created at 1:23 PM on 8/24/19.
 */
public record FlagLootCondition(String flag) implements LootItemCondition {

	public static MapCodec<FlagLootCondition> CODEC = RecordCodecBuilder.mapCodec(
			inst -> inst.group(Codec.STRING.fieldOf("flag").forGetter(FlagLootCondition::flag)
					).apply(inst, FlagLootCondition::new));

	@Override
	public boolean test(LootContext lootContext) {
        return ZetaMod.ZETA.configManager.getConfigFlagManager().getFlag(flag);
	}

	@NotNull
	@Override
	public LootItemConditionType getType() {
		return FLAG_CONDITION_TYPE;
	}
}
