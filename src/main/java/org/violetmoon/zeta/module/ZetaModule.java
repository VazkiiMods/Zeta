package org.violetmoon.zeta.module;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jetbrains.annotations.ApiStatus;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZGatherHints;

public class ZetaModule {

    //all these are just to notify that these will go package private soon. Should be read only! Cant make them final because module is initialized with reflections
    Zeta zeta;
    ZetaCategory category;

    //package protected
    String displayName = "";
    String lowercaseName = "";
    String description = "";

    //This gets dumped into a config comment; ordering must be consistent lest Forge complain the config file is "incorrect"
    protected SortedSet<String> antiOverlap = new TreeSet<>();

    boolean enabled = false;
    boolean enabledByDefault = false;
    boolean disabledByOverlap = false;
    boolean ignoreAntiOverlap = false;

    //hack. Just needed so we can load this, then unload if need in configs and ONLY touch the bus when we know in which state we want to be
    boolean finalized = false;

    public void postConstruct() {
        // NO-OP
    }

    public final void setEnabled(Zeta z, boolean willEnable) {
        //TODO: is this the right approach for handling category enablement :woozy_face:
        if (z.configManager != null && !z.configManager.isCategoryEnabled(category))
            willEnable = false;

        if (category != null && !category.requiredModsLoaded())
            willEnable = false;

        if (!ignoreAntiOverlap && antiOverlap.stream().anyMatch(z::isModLoaded)) {
            disabledByOverlap = true;
            willEnable = false;
        } else
            disabledByOverlap = false;

        if (this.enabled == willEnable)
            return;
        this.enabled = willEnable;

        if (finalized) updateBusSubscriptions(z);
    }

    void updateBusSubscriptions(Zeta z) {
        if (enabled)
            z.playBus.subscribe(this.getClass()).subscribe(this);
        else {
            z.playBus.unsubscribe(this.getClass()).unsubscribe(this);
        }
    }

    //TODO: why is this here
    @LoadEvent
    public final void addAnnotationHints(ZGatherHints event) {
        event.gatherHintsFromModule(this, zeta.configManager.getConfigFlagManager());
    }

    // new accessors. Use these and later make all fields protected. Mods aren't supposed to change those fileds at runtime..


    public Zeta zeta() {
        return zeta;
    }

    public List<String> antiOverlap() {
        return List.copyOf(antiOverlap);
    }

    public boolean disabledByOverlap() {
        return disabledByOverlap;
    }

    public boolean ignoreAntiOverlap() {
        return ignoreAntiOverlap;
    }

    public ZetaCategory category() {
        return category;
    }

    public String description() {
        return description;
    }

    public String displayName() {
        return displayName;
    }

    public String lowerCaseName() {
        return lowercaseName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean enabledByDefault() {
        return enabledByDefault;
    }

    public void setEnabledByDefault(boolean enabledByDefault) {
        this.enabledByDefault = enabledByDefault;
    }

    @ApiStatus.Internal
    public void setIgnoreAntiOverlap(boolean b) {
        ignoreAntiOverlap = b;
    }
}
