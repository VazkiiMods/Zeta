package org.violetmoon.zeta.integration;

import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.violetmoon.zeta.config.ConfigFlagManager;
import org.violetmoon.zeta.config.ConfigObjectMapper;
import org.violetmoon.zeta.event.load.ZGatherHints;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;
import org.violetmoon.zeta.util.RegistryUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JeiGatherHints implements ZGatherHints {

	private final IRecipeRegistration registration;
	private final String modId;
	private final RegistryAccess registryAccess;
	private final List<Item> blacklist;
	private final MutableComponent externalPreamble;

	public JeiGatherHints(IRecipeRegistration registration, String modId, RegistryAccess registryAccess, List<Item> blacklist) {
		this.registration = registration;
		this.modId = modId;
		this.registryAccess = registryAccess;
		this.blacklist = blacklist;

		this.externalPreamble = Component.translatable(modId + ".jei.hint.preamble");
		externalPreamble.setStyle(externalPreamble.getStyle().withColor(0x0b5d4b));
	}

	@Override
	public void accept(ItemLike itemLike, Component extra) {
		Item item = itemLike.asItem();

		if (blacklist.contains(item))
			return;

		MutableComponent compound = Component.literal("");
		if (!BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(modId))
			compound = compound.append(externalPreamble);
		compound = compound.append(extra);

		registration.addItemStackInfo(new ItemStack(item), compound);
	}

	private void applyTag(TagKey<?> tkey, String key, Object... extra) {
		if (key.isEmpty())
			key = tkey.location().getPath();

		try {
			List<?> tagItems = RegistryUtil.getTagValues(registryAccess, tkey);
			applyIterable(tagItems, key, extra);
		} catch (IllegalStateException e) {
			throw new RuntimeException("TagKey " + tkey + " failed to load.", e);
		}
	}

	private void applyIterable(Iterable<?> iter, String key, Object... extra) {
		if (key.isEmpty())
			throw new RuntimeException("Multi-item @Hints need a defined key.");

		for (Object obj : iter)
			applyObject(obj, key, extra);
	}

	private void applyObject(Object obj, String key, Object... extra) {
		if (obj instanceof ItemLike ilike)
			applyItemLike(ilike, key, extra);
		else
			throw new RuntimeException("Not an ItemLike.");
	}

	private void applyItemLike(ItemLike itemLike, String key, Object... extra) {
		if (key.isEmpty())
			hintItem(itemLike, extra);
		else
			hintItem(itemLike, key, extra);
	}

	@Override
	public void hintItem(ItemLike itemLike, Object... extra) {
		Item item = itemLike.asItem();
		ResourceLocation res = BuiltInRegistries.ITEM.getKey(item);
		String ns = res.getNamespace();
		String path = res.getPath();
		if (ns.equals(modId))
			ns = "";
		else ns += ".";

		hintItem(itemLike, ns + path, extra);
	}

	@Override
	public void hintItem(ItemLike itemLike, String key, Object... extra) {
		Item item = itemLike.asItem();
		String hint = modId + ".jei.hint." + key;
		accept(item, Component.translatable(hint, extra));
	}

	@Override
	public void gatherHintsFromModule(ZetaModule module, ConfigFlagManager cfm) {
		if (!module.isEnabled())
			return;

		List<Field> fields = ConfigObjectMapper.walkModuleFields(module.getClass());

		Map<String, Field> fieldsByName = new HashMap<>();
		for (Field f : fields)
			fieldsByName.put(f.getName(), f);

		for (Field f : fields) {
			try {
				Hint hint = f.getAnnotation(Hint.class);
				if (hint == null)
					continue;
				f.setAccessible(true);

				//Target
				Object target = ConfigObjectMapper.getField(module, f);
				if (target == null)
					continue;

				//Flag
				String flag = hint.value();
				if (!flag.isEmpty() && cfm.getFlag(flag) == !hint.negate())
					continue;

				//Translation, & gathering extra content
				String key = hint.key();
				List<Object> extraList = new ArrayList<>(hint.content().length);
				for (String c : hint.content()) {
					if (c.isEmpty())
						continue;

					Field extraField = fieldsByName.get(c);
					if (extraField == null)
						throw new RuntimeException("No field " + c + " referenced in @Hint " + f);

					Object yes = ConfigObjectMapper.getField(module, extraField);
					extraList.add(yes);
				}
				Object[] extra = extraList.toArray(new Object[0]);

				//Application
				if (target instanceof TagKey<?> tkey)
					applyTag(tkey, key, extra);
				else if (target instanceof Iterable<?> iter)
					applyIterable(iter, key, extra);
				else
					applyObject(target, key, extra);

			} catch (Exception e) {
				throw new RuntimeException("Problem applying annotation hint " + f.getName() +
					" from module " + module.getClass().getName() +
					": " + e.getMessage(), e);
			}
		}
	}

	@Override
	public RegistryAccess getRegistryAccess() {
		return registryAccess;
	}
}
