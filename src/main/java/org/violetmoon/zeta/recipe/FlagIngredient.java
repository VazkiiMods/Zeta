package org.violetmoon.zeta.recipe;

import java.util.Arrays;
import java.util.stream.Stream;

import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.config.ConfigFlagManager;

import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * @author WireSegal
 * Created at 3:44 PM on 10/20/19.
 */
public class FlagIngredient implements ICustomIngredient, IZetaIngredient<FlagIngredient> { //AccessWidener? Idk I think this is cooked.

	private final Ingredient parent;

	private final ConfigFlagManager cfm;
	private final String flag;
	private final IZetaIngredientSerializer<FlagIngredient> serializer;

	public FlagIngredient(Ingredient parent, String flag, ConfigFlagManager cfm, IZetaIngredientSerializer<FlagIngredient> serializer) {
		this.parent = parent;
		this.cfm = cfm;
		this.flag = flag;
		this.serializer = serializer;
	}

	@Override
	public Stream<ItemStack> getItems() {
		if (!cfm.getFlag(flag))
			return Stream.empty();
		return Arrays.stream(parent.getItems());
	}

	@Override
	public boolean test(@Nullable ItemStack target) {
		if (target == null || !cfm.getFlag(flag))
			return false;

		return parent.test(target);
	}

	@Override
	public boolean isSimple() {
		return parent.isSimple();
	}

	@Override
	public IZetaIngredientSerializer<FlagIngredient> zetaGetSerializer() {
		return serializer;
	}

	public record Serializer(ConfigFlagManager cfm) implements IZetaIngredientSerializer<FlagIngredient> {

		@Deprecated(forRemoval = true)
		public static Serializer INSTANCE;

		@NotNull
		@Override
		public FlagIngredient parse(@NotNull FriendlyByteBuf buffer) {
			return new FlagIngredient(Ingredient.fromNetwork(buffer), buffer.readUtf(), cfm, this);
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

		@Override
		public Zeta getZeta() {
			return cfm.zeta;
		}
	}
}
