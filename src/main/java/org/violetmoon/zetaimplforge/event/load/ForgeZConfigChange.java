package org.violetmoon.zetaimplforge.event.load;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.violetmoon.zeta.event.load.ZConfigChanged;
import org.violetmoon.zeta.event.load.ZModulesReady;

//aparently this cannot just be forge event. //TODO: investigate
public class ForgeZConfigChange extends Event implements ZConfigChanged, IModBusEvent {
}
