package org.violetmoon.zetaimplforge.event.load;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.violetmoon.zeta.event.load.ZConfigChanged;

//aparently this cannot just be forge event. //TODO: investigate
public class ForgeZConfigChange extends Event implements ZConfigChanged, IModBusEvent {
}
