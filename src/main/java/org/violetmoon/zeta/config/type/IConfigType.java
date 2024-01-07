package org.violetmoon.zeta.config.type;

import org.violetmoon.zeta.config.ConfigFlagManager;
import org.violetmoon.zeta.module.ZetaModule;

public interface IConfigType {

	default void onReload(ZetaModule module, ConfigFlagManager flagManager) {}

}
