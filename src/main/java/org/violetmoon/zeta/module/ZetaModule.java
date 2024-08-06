package org.violetmoon.zeta.module;

import org.jetbrains.annotations.ApiStatus;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZGatherHints;

import java.util.List;
import java.util.Set;

public class ZetaModule {

    //all these deprecated are just so one knows that these will become protected soon
    @Deprecated(forRemoval = true)
    protected Zeta zeta;
    protected ZetaCategory category;

    protected String displayName = "";
    protected String lowercaseName = "";
    protected String description = "";

    protected Set<String> antiOverlap = Set.of();

    //TODO: make these protected and provide accessors
    protected boolean enabled = false;
    protected boolean enabledByDefault = false;
    protected boolean disabledByOverlap = false;
    protected boolean ignoreAntiOverlap = false;


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

        setEnabledAndManageSubscriptions(z, willEnable);
    }

    private void setEnabledAndManageSubscriptions(Zeta z, boolean nowEnabled) {
        if (this.enabled == nowEnabled)
            return;
        this.enabled = nowEnabled;

        if (nowEnabled)
            z.playBus.subscribe(this.getClass()).subscribe(this);
        else
            z.playBus.unsubscribe(this.getClass()).unsubscribe(this);
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

    @ApiStatus.Internal
    public void setIgnoreAntiOverlap(boolean b) {
        ignoreAntiOverlap = b;
    }
}
