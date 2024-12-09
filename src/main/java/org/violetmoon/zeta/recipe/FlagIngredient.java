package org.violetmoon.zeta.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.config.ConfigFlagManager;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author WireSegal
 * Created at 3:44 PM on 10/20/19.
 */
public class FlagIngredient implements IZetaCustomIngredient { // TODO: Abstract later, use NF-provided CustomIngredient for now

	private final Ingredient parent;
	private final ConfigFlagManager cfm;
	private final String flag;

	public FlagIngredient(Ingredient parent, String flag, ConfigFlagManager cfm) {
		this.parent = parent;
		this.cfm = cfm;
		this.flag = flag;
	}

	@Override
	public Stream<ItemStack> getItems() {
		return (!cfm.getFlag(flag)) ? Stream.empty() : Arrays.stream(parent.getItems());
	}

	@Override
	public boolean test(@Nullable ItemStack target) {
		return cfm.getFlag(flag) && parent.test(target);
	}

	@Override
	public boolean isSimple() {
		return parent.isSimple();
	}

	@Override
	public IngredientType<?> getType() {
		return null;
	}

	@Override
	public Zeta getZeta() {
		return cfm.zeta;
	}

	/*
	public record Serializer(ConfigFlagManager cfm) implements IZetaCustomIngredient {

		@Override
		public boolean test(ItemStack stack) {
			return false;
		}

		@Override
		public Stream<ItemStack> getItems() {
			return Stream.empty();
		}

		@Override
		public boolean isSimple() {
			return false;
		}

		@Override
		public IngredientType<?> getType() {
			return null;
		}

		@Override
		public Zeta getZeta() {
			return cfm.zeta;
		}

		@NotNull
		@Override
		public FlagIngredient parse(@NotNull FriendlyByteBuf buffer) {
			return new FlagIngredient(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), buffer.readUtf(), cfm, this);
		}

		@NotNull
		@Override
		public FlagIngredient parse(@NotNull JsonObject json) {
			Ingredient value = Ingredient.fromJson(json.get("value"));
			String flag = json.getAsJsonPrimitive("flag").getAsString();
			return new FlagIngredient(value, flag, cfm, this);
		}

		@Override
		public void write(@NotNull FriendlyByteBuf buffer, @NotNull FlagIngredient ingredient) {
			ingredient.parent.toNetwork(buffer);
			buffer.writeUtf(ingredient.flag);
		}
		*/
}
