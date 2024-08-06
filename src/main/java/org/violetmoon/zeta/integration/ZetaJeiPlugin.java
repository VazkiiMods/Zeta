package org.violetmoon.zeta.integration;

import com.google.common.collect.Sets;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.config.ConfigFlagManager;
import org.violetmoon.zeta.config.ConfigObjectMapper;
import org.violetmoon.zeta.config.ZetaGeneralConfig;
import org.violetmoon.zeta.event.load.ZGatherHints;
import org.violetmoon.zeta.mod.ZetaMod;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;
import org.violetmoon.zeta.util.RegistryUtil;
import org.violetmoon.zeta.util.zetalist.ZetaList;

import java.lang.reflect.Field;
import java.util.*;

@JeiPlugin
public class ZetaJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = new ResourceLocation(ZetaMod.ZETA_ID, ZetaMod.ZETA_ID);

    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void onRuntimeAvailable(@NotNull final IJeiRuntime jeiRuntime) {

        ZetaMod.ZETA.configManager.addOnReloadListener("jei_plugin", configInternals -> {
            removeDisabledIngredients(jeiRuntime, ZetaList.INSTANCE.getZetas());
        });
        // also registers it for all the zeta as both configs can influence this
        ZetaList.INSTANCE.getZetas().forEach(z -> z.configManager.addOnReloadListener("jei_plugin", configInternals -> {
            removeDisabledIngredients(jeiRuntime, List.of(z));
        }));
    }

    private static void removeDisabledIngredients(@NotNull IJeiRuntime jeiRuntime, Iterable<Zeta> zetas) {

        for (Zeta z : zetas) {

            //needed?

            // List<ItemStack> disabledItems = ZetaMod.ZETA.requiredModTooltipHandler.disabledItems();
            // if (!disabledItems.isEmpty())
            //   jeiRuntime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, disabledItems);


            if (!ZetaGeneralConfig.hideDisabledContent)
                return;


            Set<Potion> hidePotions = Sets.newHashSet();
            NonNullList<ItemStack> stacksToHide = NonNullList.create();

            //we need to manually hide potions derivative items as these are vanilla and will be populated automatically

            for (var h : z.registry.getRegisteredObjects(Registries.POTION)) {
                if (!ZetaMod.ZETA.brewingRegistry.isEnabled(h.get())) {
                    hidePotions.add(h.get());
                }
            }

            if (!hidePotions.isEmpty()) {
                for (Item item : BuiltInRegistries.ITEM) {
                    if (item instanceof PotionItem || item instanceof TippedArrowItem) {
                        NonNullList<ItemStack> potionStacks = NonNullList.create();
                        potionStacks.stream().filter(it -> hidePotions.contains(PotionUtils.getPotion(it))).forEach(stacksToHide::add);
                    }
                }
            }

            //TODO: remove? I have no clue if this should be added or not
            /*
            for (var h : z.registry.getRegisteredObjects(Registries.ITEM)) {
                if (!IDisableable.isEnabled(h.get())) {
                    //TODO 1.20: this just enumerated the item's variants
                    //item.fillItemCategory(CreativeModeTab.TAB_SEARCH, stacksToHide);
                }
            }*/

            if (!stacksToHide.isEmpty())
                Minecraft.getInstance().submitAsync(() -> jeiRuntime.getIngredientManager()
                        .removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, stacksToHide));

        }
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {

        if (!ZetaGeneralConfig.enableJeiItemInfo) return;

        RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();

        List<Item> blacklist = RegistryUtil.massRegistryGet(ZetaGeneralConfig.suppressedInfo, BuiltInRegistries.ITEM);
        for (Zeta z : ZetaList.INSTANCE.getZetas()) {
            z.loadBus.fire(new GatherHintImpl(registration, z, registryAccess, blacklist), ZGatherHints.class);
        }
    }

    // feel free to move it as public outer class
    private static class GatherHintImpl implements ZGatherHints {

        private final Zeta zeta;
        private final RegistryAccess registryAccess;
        private final IRecipeRegistration registration;
        private final List<Item> blacklist;
        private final MutableComponent externalPreamble;

        public GatherHintImpl(IRecipeRegistration registration, Zeta z, RegistryAccess registryAccess, List<Item> blacklist) {
            this.zeta = z;
            this.registryAccess = registryAccess;
            this.registration = registration;
            this.blacklist = blacklist;

            this.externalPreamble = Component.translatable(z.modid + ".jei.hint.preamble");
            externalPreamble.setStyle(externalPreamble.getStyle().withColor(0x0b5d4b));
        }

        @Override
        public void accept(ItemLike itemLike, Component extra) {
            Item item = itemLike.asItem();

            if (blacklist.contains(item))
                return;

            MutableComponent compound = Component.literal("");
            if (!BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(zeta.modid))
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
            if (ns.equals(zeta.modid))
                ns = "";
            else ns += ".";

            hintItem(itemLike, ns + path, extra);
        }

        @Override
        public void hintItem(ItemLike itemLike, String key, Object... extra) {
            Item item = itemLike.asItem();
            String hint = zeta.modid + ".jei.hint." + key;
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
    }

}
