package org.violetmoon.zeta.event.play.loading;

import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

//todo: Capabilities are no more, only reason why this is deprecated vs removed is to double check if we need a replacement
@Deprecated(forRemoval = true)
public interface ZAttachCapabilities<T> extends IZetaPlayEvent {
    //ZetaCapabilityManager getCapabilityManager();
    /* getObject();

    <C> void addCapability(ResourceLocation key, ZetaCapability<C> cap, C impl);

    interface ItemStackCaps extends ZAttachCapabilities<ItemStack> { }
    interface BlockEntityCaps extends ZAttachCapabilities<BlockEntity> { }
    interface LevelCaps extends ZAttachCapabilities<Level> { }

    @Deprecated //Forge only API, we should migrate off ICapabilityProvider.
    void addCapabilityForgeApi(ResourceLocation key, ICapabilityProvider cap);*/
}
