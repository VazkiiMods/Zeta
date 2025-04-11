package org.violetmoon.zetaimplforge.event.load;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.violetmoon.zeta.event.load.ZModulesReady;
import org.violetmoon.zeta.event.load.ZTagsUpdated;

public class ForgeZModulesReady extends Event implements ZModulesReady, IModBusEvent {
}
