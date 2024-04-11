package org.violetmoon.zeta.event.play.loading;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.config.ConfigFlagManager;
import org.violetmoon.zeta.config.ConfigObjectMapper;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;
import org.violetmoon.zeta.util.RegistryUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ZGatherHints extends IZetaPlayEvent {

	void accept(ItemLike itemLike, Component extra);
	RegistryAccess getRegistryAccess();

	default void hintItem(Zeta zeta, ItemLike itemLike, Object... extra) {
		Item item = itemLike.asItem();
		ResourceLocation res = BuiltInRegistries.ITEM.getKey(item);
		String ns = res.getNamespace();
		String path = res.getPath();

		if(ns.equals(zeta.modid))
			ns = "";
		else ns += ".";

		hintItem(zeta, itemLike, ns + path, extra);
	}

	default void hintItem(Zeta zeta, ItemLike itemLike, String key, Object... extra) {
		Item item = itemLike.asItem();
		String hint = zeta.modid + ".jei.hint." + key;
		accept(item, Component.translatable(hint, extra));
	}

	default void gatherHintsFromModule(ZetaModule module, ConfigFlagManager cfm) {
		if(!module.enabled)
			return;

		List<Field> fields = ConfigObjectMapper.walkModuleFields(module.getClass());

		Map<String, Field> fieldsByName = new HashMap<>();
		for(Field f : fields)
			fieldsByName.put(f.getName(), f);

		for(Field f : fields) {
			try {
				Hint hint = f.getAnnotation(Hint.class);
				if(hint == null)
					continue;
				f.setAccessible(true);

				//Target
				Object target = ConfigObjectMapper.getField(module, f);
				if(target == null)
					continue;

				//Flag
				String flag = hint.value();
				if(!flag.isEmpty() && cfm.getFlag(flag) == !hint.negate())
					continue;

				//Translation, & gathering extra content
				String key = hint.key();
				List<Object> extraList = new ArrayList<>(hint.content().length);
				for(String c : hint.content()) {
					if(c.isEmpty())
						continue;

					Field extraField = fieldsByName.get(c);
					if(extraField == null)
						throw new RuntimeException("No field " + c + " referenced in @Hint " + f);

					Object yes = ConfigObjectMapper.getField(module, extraField);
					extraList.add(yes);
				}
				Object[] extra = extraList.toArray(new Object[0]);

				//Application
				if(target instanceof TagKey<?> tkey)
					applyTag(module.zeta, tkey, key, extra);
				else if(target instanceof Iterable<?> iter)
					applyIterable(module.zeta, iter, key, extra);
				else
					applyObject(module.zeta, target, key, extra);

			} catch (Exception e) {
				throw new RuntimeException("Problem applying annotation hint " + f.getName() +
					" from module " + module.getClass().getName() +
					": " + e.getMessage(), e);
			}
		}
	}

	private void applyTag(Zeta zeta, TagKey<?> tkey, String key, Object... extra) {
		if(key.isEmpty())
			key = tkey.location().getPath();

		try {
			List<?> tagItems = RegistryUtil.getTagValues(getRegistryAccess(), tkey);
			applyIterable(zeta, tagItems, key, extra);
		} catch(IllegalStateException e) {
			throw new RuntimeException("TagKey " + tkey + " failed to load.", e);
		}
	}

	private void applyIterable(Zeta zeta, Iterable<?> iter, String key, Object... extra) {
		if(key.isEmpty())
			throw new RuntimeException("Multi-item @Hints need a defined key.");

		for(Object obj : iter)
			applyObject(zeta, obj, key, extra);
	}

	private void applyObject(Zeta zeta, Object obj, String key, Object... extra) {
		if(obj instanceof ItemLike ilike)
			applyItemLike(zeta, ilike, key, extra);
		else
			throw new RuntimeException("Not an ItemLike.");
	}

	private void applyItemLike(Zeta zeta, ItemLike itemLike, String key, Object... extra) {
		if(key.isEmpty())
			hintItem(zeta, itemLike, extra);
		else
			hintItem(zeta, itemLike, key, extra);
	}
}
