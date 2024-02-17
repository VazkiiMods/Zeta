package org.violetmoon.zeta.event.play.loading;

import org.violetmoon.zeta.capability.ZetaCapability;
import org.violetmoon.zeta.capability.ZetaCapabilityManager;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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