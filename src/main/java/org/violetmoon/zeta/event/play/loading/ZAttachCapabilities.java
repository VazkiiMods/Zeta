package org.violetmoon.zeta.event.play.loading;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import org.violetmoon.zeta.capability.ZetaCapability;
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
