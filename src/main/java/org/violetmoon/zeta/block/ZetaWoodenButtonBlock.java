package org.violetmoon.zeta.block;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.jetbrains.annotations.NotNull;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.module.ZetaModule;

public class ZetaWoodenButtonBlock extends ZetaButtonBlock {

	public ZetaWoodenButtonBlock(BlockSetType setType, String regname, @Nullable ZetaModule module, Properties properties) {
		super(setType, 30, true, regname, module, properties);
	}

	@NotNull
	@Override
	protected SoundEvent getSound(boolean powered) {
		return powered ? SoundEvents.WOODEN_BUTTON_CLICK_ON : SoundEvents.WOODEN_BUTTON_CLICK_OFF;
	}

}
