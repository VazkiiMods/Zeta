package org.violetmoon.zetaimplforge.world;

import java.util.List;

import com.mojang.serialization.MapCodec;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.mod.ZetaMod;
import org.violetmoon.zeta.util.zetalist.ZetaList;
import org.violetmoon.zeta.world.WorldGenHandler;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeGenerationSettingsBuilder;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.util.zetalist.ZetaList;
import org.violetmoon.zeta.world.WorldGenHandler;

import java.util.List;

public class ZetaBiomeModifier implements BiomeModifier {

	public static final ResourceLocation RESOURCE = ResourceLocation.fromNamespaceAndPath(ZetaMod.ZETA_ID, "biome_modifier");
	private static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<? extends BiomeModifier>> SERIALIZER = DeferredHolder.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, RESOURCE);

	@Override
	public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
		if(phase == Phase.ADD) {
			modifyBiome(biome, builder);
			
			for(Zeta zeta : ZetaList.INSTANCE.getZetas())
				ZetaSpawnModifier.modifyBiome(biome, zeta.entitySpawn, builder);
		}
	}

	@Override
	public @NotNull MapCodec<? extends BiomeModifier> codec() {
		return SERIALIZER.get();
	}

	public static MapCodec<ZetaBiomeModifier> makeCodec() {
		return MapCodec.unit(ZetaBiomeModifier::new);
	}
	
	public static void modifyBiome(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder biomeInfoBuilder) {
		BiomeGenerationSettingsBuilder settings = biomeInfoBuilder.getGenerationSettings();

		for(GenerationStep.Decoration stage : GenerationStep.Decoration.values()) {
			List<Holder<PlacedFeature>> features = settings.getFeatures(stage);
			features.add(WorldGenHandler.defers.get(stage));
		}
	}
	
	public static void registerBiomeModifier(IEventBus bus) {
		DeferredRegister<MapCodec<? extends BiomeModifier>> biomeModifiers = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, ZetaMod.ZETA_ID);
		biomeModifiers.register(bus);
		biomeModifiers.register(ZetaBiomeModifier.RESOURCE.getPath(), ZetaBiomeModifier::makeCodec);
	}
}