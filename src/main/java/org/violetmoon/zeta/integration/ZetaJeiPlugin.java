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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.jetbrains.annotations.NotNull;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.config.ZetaGeneralConfig;
import org.violetmoon.zeta.event.load.ZGatherHints;
import org.violetmoon.zeta.mod.ZetaMod;
import org.violetmoon.zeta.util.RegistryUtil;
import org.violetmoon.zeta.util.zetalist.ZetaList;

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
            z.loadBus.fire(new JeiGatherHints(registration, z.modid, registryAccess, blacklist), ZGatherHints.class);
        }
    }

}
