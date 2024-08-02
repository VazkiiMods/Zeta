package org.violetmoon.zetaimplforge.mixin.mixins;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.concurrent.locks.ReentrantLock;

@Mixin(ModConfig.class)
public interface AccessorModConfig {

    @Invoker("<init>")
    static ModConfig zeta$initModConfig(ModConfig.Type type, IConfigSpec spec, ModContainer container, String fileName, ReentrantLock lock) {
        throw new AssertionError();
    }
}
