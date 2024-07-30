package org.violetmoon.zetaimplforge.event.play.loading;

import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.capability.ZetaCapability;
import org.violetmoon.zeta.capability.ZetaCapabilityManager;
import org.violetmoon.zeta.event.play.loading.ZAttachCapabilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public abstract class ForgeZAttachCapabilities<T> implements ZAttachCapabilities<T> {
    protected final ZetaCapabilityManager zetaCaps;
    protected final AttachCapabilitiesEvent<T> e;

    public ForgeZAttachCapabilities(ZetaCapabilityManager zetaCaps, AttachCapabilitiesEvent<T> e) {
        this.zetaCaps = zetaCaps;
        this.e = e;
    }

    @Override
    public ZetaCapabilityManager getCapabilityManager() {
        return zetaCaps;
    }

    @Override
    public T getObject() {
        return e.getObject();
    }

    @Override
    public <C> void addCapability(ResourceLocation key, ZetaCapability<C> cap, C impl) {
        zetaCaps.attachCapability(e, key, cap, impl);
    }

    @Override
    @Deprecated //Forge only API, we should migrate off ICapabilityProvider.
    public void addCapabilityForgeApi(ResourceLocation key, ICapabilityProvider cap) {
        e.addCapability(key, cap);
    }

    public static class ItemStackCaps extends ForgeZAttachCapabilities<ItemStack> implements ZAttachCapabilities.ItemStackCaps {
        public ItemStackCaps(ZetaCapabilityManager zetaCaps, AttachCapabilitiesEvent<ItemStack> e) {
            super(zetaCaps, e);
        }
    }

    public static class BlockEntityCaps extends ForgeZAttachCapabilities<BlockEntity> implements ZAttachCapabilities.BlockEntityCaps {
        public BlockEntityCaps(ZetaCapabilityManager zetaCaps, AttachCapabilitiesEvent<BlockEntity> e) {
            super(zetaCaps, e);
        }
    }

    public static class LevelCaps extends ForgeZAttachCapabilities<Level> implements ZAttachCapabilities.LevelCaps {
        public LevelCaps(ZetaCapabilityManager zetaCaps, AttachCapabilitiesEvent<Level> e) {
            super(zetaCaps, e);
        }
    }
}
