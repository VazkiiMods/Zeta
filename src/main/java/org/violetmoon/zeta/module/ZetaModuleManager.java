package org.violetmoon.zeta.module;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.util.ZetaSide;
import org.violetmoon.zetaimplforge.event.load.ForgeZModulesReady;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * TODO: other forms of module discovery and replacement (like a Forge-only module, or other types of 'replacement' modules)
 */
public class ZetaModuleManager {
    private final Zeta z;

    private final Map<Class<? extends ZetaModule>, ZetaModule> modulesByKey = new LinkedHashMap<>();
    private final Map<String, ZetaCategory> categoriesById = new LinkedHashMap<>();
    private final Map<ZetaCategory, List<ZetaModule>> modulesInCategory = new HashMap<>();

    public ZetaModuleManager(Zeta z) {
        this.z = z;
    }

    // Modules //

    public Collection<ZetaModule> getModules() {
        return modulesByKey.values();
    }

    //SAFETY: check how TentativeModule.keyClass is assigned.
    // It's either set to the *same* class as the module implementation,
    // or set to the target of a clientReplacementOf operation, which is
    // checked to be assignableFrom the module implementation during loading.
    @SuppressWarnings("unchecked")
    public <M extends ZetaModule> M get(Class<M> keyClass) {
        return (M) modulesByKey.get(keyClass);
    }

    public <M extends ZetaModule> Optional<M> getOptional(Class<M> keyClass) {
        return Optional.ofNullable(get(keyClass));
    }

    public boolean isEnabled(Class<? extends ZetaModule> keyClass) {
        return get(keyClass).isEnabled();
    }

    public boolean isEnabledOrOverlapping(Class<? extends ZetaModule> keyClass) {
        ZetaModule module = get(keyClass);
        return module.isEnabled() || module.disabledByOverlap();
    }

    // Categories //

    public ZetaCategory getCategory(String id) {
        if (id == null || id.isEmpty()) id = "Unknown";

        return categoriesById.computeIfAbsent(id, ZetaCategory::unknownCategory);
    }

    public Collection<ZetaCategory> getCategories() {
        return categoriesById.values();
    }

    public List<ZetaCategory> getInhabitedCategories() {
        return categoriesById.values().stream()
                .filter(c -> !modulesInCategory(c).isEmpty())
                .toList();
    }

    public List<ZetaModule> modulesInCategory(ZetaCategory cat) {
        return modulesInCategory.computeIfAbsent(cat, __ -> new ArrayList<>());
    }

    // Loading //

    @ApiStatus.Internal
    public final void initialize( ModuleFinder finder, Iterable<ZetaCategory> categories) {
        initCategories(categories);
        findModules(finder);
    }

    protected void initCategories(Iterable<ZetaCategory> cats) {
        for (ZetaCategory cat : cats) categoriesById.put(cat.name, cat);
    }

    protected void findModules(ModuleFinder finder) {
        Collection<TentativeModule> tentative = finder.get()
                .map(data -> TentativeModule.from(data, this::getCategory))
                .filter(tm -> tm.appliesTo(z.side))
                .sorted(Comparator.comparing(TentativeModule::loadPhase).thenComparing(TentativeModule::displayName))
                .toList();

        //this is the part where we handle "client replacement" modules !!
        if (z.side == ZetaSide.CLIENT) {
            Map<Class<? extends ZetaModule>, TentativeModule> byClazz = new LinkedHashMap<>();

            //first, lay down all modules that are not client replacements
            for (TentativeModule tm : tentative)
                if (!tm.clientReplacement())
                    byClazz.put(tm.clazz(), tm);

            //then overlay with the client replacements
            for (TentativeModule tm : tentative) {
                if (tm.clientReplacement()) {
                    //SAFETY: already checked isAssignableFrom in TentativeModule
                    @SuppressWarnings("unchecked")
                    Class<? extends ZetaModule> superclass = (Class<? extends ZetaModule>) tm.clazz().getSuperclass();

                    TentativeModule existing = byClazz.get(superclass);
                    if (existing == null)
                        throw new RuntimeException("Module " + tm.clazz().getName() + " wants to replace " + superclass.getName() + ", but that module isn't registered");

                    byClazz.put(superclass, existing.replaceWith(tm));
                }
            }

            tentative = byClazz.values();
        }

        z.log.info("Discovered " + tentative.size() + " modules to load.");

        for (TentativeModule t : tentative) {
            modulesByKey.put(t.keyClass(), constructModuleAndSetup(t));
        }

        z.log.info("Constructed {} modules.", modulesByKey.size());

        z.loadBus.fire(new ForgeZModulesReady());
    }


    public void setupBusSubscriptions() {
        for (var m : this.getModules()) {
            m.finalized = true;
            m.updatePlayBusSubscriptions();
        }
    }

    private ZetaModule constructModuleAndSetup(TentativeModule t) {
        z.log.info("Constructing module {}...", t.displayName());

        //construct, set properties
        ZetaModule module = construct(t.clazz());

        module.zeta = z;
        module.category = t.category();

        module.displayName = t.displayName();
        module.lowercaseName = t.lowercaseName();
        module.description = t.description();

        module.antiOverlap = t.antiOverlap();

        module.enabledByDefault = t.enabledByDefault();

        //event busses
        module.setEnabled(z, t.enabledByDefault());
        z.loadBus.subscribe(module.getClass()).subscribe(module);

        //category upkeep
        modulesInCategory.computeIfAbsent(module.category, __ -> new ArrayList<>()).add(module);

        populateModuleInstanceField(module);

        //post-construction callback
        module.postConstruct();

        return module;
    }

    // feel free to refactor
    private void populateModuleInstanceField(ZetaModule module) {
        var clazz = module.getClass();


        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        //if module is a client one, look at superclasstoo
        if (module.getClass().isAnnotationPresent(ZetaLoadModule.class)) {
            ZetaLoadModule annotation = module.getClass().getAnnotation(ZetaLoadModule.class);
            if (annotation.clientReplacement()) {
                fields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
            }
        }

        List<Field> targetFields = new ArrayList<>();

        for (Field field : fields) {
            if (field.isAnnotationPresent(ModuleInstance.class)) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    throw new IllegalStateException("@ModuleInstance annotated field must be static");
                }
                //make sure the module class can be assigned to the fields
                if (!field.getType().isAssignableFrom(clazz)) {
                    throw new IllegalStateException("@ModuleInstance annotated field must be assignable from the module class. Expected: " + field.getType() + ", got: " + clazz);
                }
                targetFields.add(field);
            }
        }
        for (Field targetField : targetFields) {
            targetField.setAccessible(true);
            try {
                targetField.set(null, module);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private <Z extends ZetaModule> Z construct(Class<Z> clazz) {
        try {
            Constructor<Z> cons = clazz.getConstructor();
            return cons.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not construct ZetaModule '" + clazz.getName() + "', does it have a public zero-argument constructor?", e);
        }
    }

}
