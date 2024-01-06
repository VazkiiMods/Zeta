package org.violetmoon.zetaimplforge.mixin.client;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.violetmoon.zeta.client.event.play.ZEarlyRender;
import org.violetmoon.zeta.util.mixinevent.MixinFireEventUtilClient;
import org.violetmoon.zetaimplforge.client.event.play.ForgeZEarlyRender;

@Mixin(GameRenderer.class)
public class GameRenderMixin {
    @Inject(method = "render", at = @At(target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", value = "INVOKE", shift = At.Shift.AFTER))
    private void quark$renderEvent(float v, long l, boolean b, CallbackInfo ci) {
        MixinFireEventUtilClient.fireEvent(new ForgeZEarlyRender(), ZEarlyRender.class);
    }
}
