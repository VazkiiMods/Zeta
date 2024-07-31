package org.violetmoon.zeta.util.zetalist;

import java.util.HashSet;
import java.util.Set;

import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

public class ZetaList<T extends IZeta> {
	
	public static ZetaList<Zeta> INSTANCE = new ZetaList<>();
	
    private final Set<T> knownZetas = new HashSet<>(2);

    protected ZetaList() { }
    
    public void register(T z) {
        knownZetas.add(z);
    }

    public <E extends IZetaLoadEvent> void fireLoadEvent(E event) {
        knownZetas.forEach(z -> z.asZeta().loadBus.fire(event));
    }

    public <E extends IZetaLoadEvent> void fireLoadEvent(E event, Class<E> eventClass) {
        knownZetas.forEach(z -> z.asZeta().loadBus.fire(event, eventClass));
    }
    
    public Iterable<T> getZetas() {
    	return knownZetas;
    }
}
