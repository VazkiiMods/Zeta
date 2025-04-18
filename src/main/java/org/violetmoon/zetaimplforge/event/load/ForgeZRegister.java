package org.violetmoon.zetaimplforge.event.load;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.advancement.AdvancementModifierRegistry;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.registry.*;

public class ForgeZRegister extends Event implements ZRegister, IModBusEvent {

    public ForgeZRegister() {
    }

    // same as registry but runs right after (our own event ONLY! Don't expect other mod stuff to be ready here)
    // dont extend or we'll match all its subscriptions. Ideally these 2 should have been 2 subclasses
    public static class Post extends Event implements ZRegister.Post, IModBusEvent {

        public Post() {
        }
    }
}
