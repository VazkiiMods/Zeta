package org.violetmoon.zeta.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.mod.ZetaMod;
import org.violetmoon.zeta.recipe.IZetaCondition;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;

/**
 * @author WireSegal
 *         Created at 1:23 PM on 8/24/19.
 */


public record FlagCondition(String flag, Optional<Boolean> extraCondition) implements ICondition {
	public static final MapCodec<FlagCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			Codec.STRING.fieldOf("flag").forGetter(FlagCondition::flag),
			Codec.BOOL.optionalFieldOf("extraCondition").forGetter(flagCondition -> flagCondition.extraCondition)
	).apply(inst, FlagCondition::new));

	public static void doEventReal(RegisterEvent event) {
		if (event.getRegistry().equals(NeoForgeRegistries.CONDITION_SERIALIZERS)) {
			Registry.register(NeoForgeRegistries.CONDITION_SERIALIZERS, ResourceLocation.fromNamespaceAndPath(ZetaMod.ZETA.modid, "flag"), CODEC);
		}
	}


	@Override
	public MapCodec<? extends ICondition> codec() {
		return CODEC;
	}

	//todo: I hate it I hate it I hate it I hate it I hate it I hate it
	@Override
	public boolean test(IContext context) {
		if(flag.contains("%"))
			throw new RuntimeException("Illegal flag: " + flag);

		Set<ConfigManager> iHateThis = ConfigManager.EVIL_CONFIG_STORAGE_THAT_I_NEED;
        for (Iterator<ConfigManager> iterator = iHateThis.iterator(); iterator.hasNext(); ) {
            ConfigManager configManager = iterator.next();
            ConfigFlagManager cfm = configManager.getConfigFlagManager();

            if (!cfm.isValidFlag(flag)) {
                if (iterator.hasNext())
					continue;

				cfm.zeta.log.warn("Non-existent flag {} being used", flag);
                // return true for unknown flags
                return true;
            }

            boolean cond = true;
            if (extraCondition().isPresent())
                cond = extraCondition.get();

            return cond && cfm.getFlag(flag);
        }
		return true;
	}

	/*
	public static class Serializer implements IZetaConditionSerializer<FlagCondition> {
		private final ConfigFlagManager cfm;
		private final ResourceLocation location;
		private final BooleanSupplier extraCondition;

		public Serializer(ConfigFlagManager cfm, ResourceLocation location, BooleanSupplier extraCondition) {
			this.cfm = cfm;
			this.location = location;
			this.extraCondition = extraCondition;
		}

		public Serializer(ConfigFlagManager cfm, ResourceLocation location) {
			this(cfm, location, BooleanSuppliers.TRUE);
		}

		@Override
		public void write(JsonObject json, FlagCondition value) {
			json.addProperty("flag", value.flag);
		}

		@Override
		public FlagCondition read(JsonObject json) {
			return new FlagCondition(cfm, json.getAsJsonPrimitive("flag").getAsString(), location, extraCondition);
		}

		@Override
		public ResourceLocation getID() {
			return location;
		}
	}
	 */
}