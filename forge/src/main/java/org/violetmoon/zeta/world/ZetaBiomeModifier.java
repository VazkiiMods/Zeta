package org.violetmoon.zeta.world;

import java.util.List;

import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.util.zetalist.ZetaList;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ZetaBiomeModifier implements BiomeModifier {

	public static final ResourceLocation RESOURCE = new ResourceLocation(Zeta.ZETA_ID, "biome_modifier");
	private static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER = RegistryObject.create(RESOURCE, ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Zeta.ZETA_ID);

	@Override
	public void modify(Holder<Biome> biome, Phase phase, BiomeInfo.Builder builder) {
		if(phase == Phase.ADD) {
			modifyBiome(biome, builder);
			
			for(Zeta zeta : ZetaList.INSTANCE.getZetas())
				ZetaSpawnModifier.modifyBiome(biome, zeta.entitySpawn, builder);
		}
	}

	@Override
	public Codec<? extends BiomeModifier> codec() {
		return SERIALIZER.get();
	}

	public static Codec<ZetaBiomeModifier> makeCodec() {
		return Codec.unit(ZetaBiomeModifier::new);
	}
	
	public static void modifyBiome(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder biomeInfoBuilder) {
		BiomeGenerationSettingsBuilder settings = biomeInfoBuilder.getGenerationSettings();

		for(GenerationStep.Decoration stage : GenerationStep.Decoration.values()) {
			List<Holder<PlacedFeature>> features = settings.getFeatures(stage);
			features.add(WorldGenHandler.defers.get(stage));
		}
	}
	
	public static void registerBiomeModifier(IEventBus bus) {
		DeferredRegister<Codec<? extends BiomeModifier>> biomeModifiers = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Zeta.ZETA_ID);
		biomeModifiers.register(bus);
		biomeModifiers.register(ZetaBiomeModifier.RESOURCE.getPath(), ZetaBiomeModifier::makeCodec);
	}
}