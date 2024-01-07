package org.violetmoon.zeta.network.message;

import org.apache.commons.lang3.tuple.Pair;
import org.violetmoon.zeta.config.SyncedFlagHandler;
import org.violetmoon.zeta.network.IZetaNetworkEventContext;
import org.violetmoon.zeta.network.ZetaHandshakeMessage;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class S2CLoginFlag extends ZetaHandshakeMessage {

	public BitSet flags;
	public int expectedLength;
	public int expectedHash;

	public S2CLoginFlag() {
		flags = SyncedFlagHandler.compileFlagInfo();
		expectedLength = SyncedFlagHandler.expectedLength();
		expectedHash = SyncedFlagHandler.expectedHash();
	}

	//what The fuck is forge doing
	public static List<Pair<String, S2CLoginFlag>> generateRegistryPackets(boolean isLocal) {
		return !isLocal ? Collections.singletonList(Pair.of(S2CLoginFlag.class.getName(), new S2CLoginFlag())) : Collections.emptyList();
	}

	@Override
	public boolean receive(IZetaNetworkEventContext context) {
		if(expectedLength == SyncedFlagHandler.expectedLength() && expectedHash == SyncedFlagHandler.expectedHash())
			SyncedFlagHandler.receiveFlagInfoFromServer(flags);
		context.reply(new C2SLoginFlag());
		return true;
	}
}
