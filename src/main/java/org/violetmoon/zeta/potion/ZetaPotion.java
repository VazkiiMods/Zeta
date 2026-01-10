package org.violetmoon.zeta.potion;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.BooleanSuppliers;

import java.util.function.BooleanSupplier;

public class ZetaPotion extends Potion implements IZetaPotion {
    private final @Nullable ZetaModule module;
    private BooleanSupplier enabledSupplier = BooleanSuppliers.TRUE;

    public ZetaPotion(@Nullable ZetaModule module, MobEffectInstance... effects) {
        this(module, null, effects);

    }

    public ZetaPotion(@Nullable ZetaModule module, String potionName, MobEffectInstance... effects) {
        super(potionName, effects);

        this.module = module;

    }

    @Override
    public @Nullable ZetaModule getModule() {
        return module;
    }

    @Override
    public ZetaPotion setCondition(BooleanSupplier enabledSupplier) {
        this.enabledSupplier = enabledSupplier;
        return this;
    }

    @Override
    public boolean doesConditionApply() {
        return enabledSupplier.getAsBoolean();
    }
}
