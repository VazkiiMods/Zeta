package org.violetmoon.zeta.network;

import java.util.BitSet;

import org.violetmoon.zeta.mod.ZetaMod;
import org.violetmoon.zeta.network.message.C2SLoginFlag;
import org.violetmoon.zeta.network.message.C2SUpdateFlag;
import org.violetmoon.zeta.network.message.S2CLoginFlag;
import org.violetmoon.zeta.network.message.S2CUpdateFlag;

public class ZetaModInternalNetwork {

	public static final int PROTOCOL = 2;

	public static void registerMessages(ZetaNetworkHandler network) {

		network.getSerializer().mapHandlers(BitSet.class, (buf, field) -> BitSet.valueOf(buf.readLongArray()), (buf, field, bitSet) -> buf.writeLongArray(bitSet.toLongArray()));
		
		// Flag Syncing
		network.register(S2CUpdateFlag.class, ZetaNetworkDirection.PLAY_TO_CLIENT);
		network.register(C2SUpdateFlag.class, ZetaNetworkDirection.PLAY_TO_SERVER);

		// Login
		network.registerLogin(S2CLoginFlag.class, ZetaNetworkDirection.LOGIN_TO_CLIENT, 98, true, S2CLoginFlag::generateRegistryPackets);
		network.registerLogin(C2SLoginFlag.class, ZetaNetworkDirection.LOGIN_TO_SERVER, 99, false, null);
	}


}
