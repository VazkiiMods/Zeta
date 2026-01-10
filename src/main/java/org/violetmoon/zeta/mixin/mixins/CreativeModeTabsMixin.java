package org.violetmoon.zeta.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.Holder;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.alchemy.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.zeta.module.IDisableable;

import java.util.stream.Stream;

@Mixin(CreativeModeTabs.class)
public class CreativeModeTabsMixin {
    @ModifyExpressionValue(method = "generatePotionEffectTypes", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;"))
    private static Stream<Holder.Reference<Potion>> filterForZetaDisabledPotions(Stream<Holder.Reference<Potion>> original) {
        return original.filter(potionReference -> {
            if (potionReference.value() instanceof IDisableable<?> disableable)
                return disableable.isEnabled();
            else return true;
        });
    }
}
