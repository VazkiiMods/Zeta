package org.violetmoon.zeta.module;

import org.jetbrains.annotations.ApiStatus;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZGatherHints;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class ZetaModule {

    // all these are package private as we cannot guarantee they are final and null since the module is constructed with reflections
    Zeta zeta;
    ZetaCategory category;

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
        //no it isn't
        if (!z.configManager.isCategoryEnabled(category))
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

        if (finalized) updatePlayBusSubscriptions();
    }
//TODO: change
    void updatePlayBusSubscriptions() {
        if (enabled)
            zeta.playBus.subscribe(this.getClass()).subscribe(this);
        else {
            zeta.playBus.unsubscribe(this.getClass()).unsubscribe(this);
        }
    }

    //TODO: why is this here
    @LoadEvent
    public final void addAnnotationHints(ZGatherHints event) {
        event.gatherHintsFromModule(this, zeta.configManager.getConfigFlagManager());
    }

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

    @ApiStatus.Internal
    public void setIgnoreAntiOverlap(boolean b) {
        ignoreAntiOverlap = b;
    }
}
